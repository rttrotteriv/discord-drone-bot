package com.cheesetron;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.managers.AudioManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;


/**
 * Class that listens to and responds to slash commands and buttons.
 */
public class DroneBotEventListener extends ListenerAdapter {

    private static final Logger logger = LogManager.getLogger("com.cheesetron.dronebot");
    private static final Logger audioLogger = LogManager.getLogger("com.cheesetron.dronebot.audio");

    AudioPlayerManager playerManager;

    Map<String, AudioPlayer> guildPlayers = new HashMap<>();

    Map<String, TrackScheduler> guildQueues = new HashMap<>();


    DroneBotEventListener() {
        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        logger.debug("Command received: " + event.getName());

        switch (event.getName()) {
            case "say" ->
                // content is required so no null-check here
                //noinspection DataFlowIssue
                    say(event, event.getOption("content").getAsString());
            /*
            case "prune" -> prune(event);
            */
            case "boop" -> boop(event);
            case "play" -> startPlaying(event);
            case "skip" -> skipSong(event);
            case "repeat" -> toggleRepeat(event);
            case "leave" -> leaveVoice(event);
            default -> {
                // the registered command isn't handled in code
                event.reply("Something went wrong with that command :c").setEphemeral(true).queue();
                logger.error("Couldn't handle slash command, check if command list updated");
            }
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String[] id = event.getComponentId().split(":");  // this is the custom id we specified in our button
        String authorId = id[0];
        String type = id[1];
        // Check that the button is for the user that clicked it, otherwise just ignore the event (let interaction fail)
        if (!authorId.equals(event.getUser().getId()))
            return;
        event.deferEdit().queue();  // acknowledge the button was clicked, otherwise the interaction will fail

        TextChannel channel = (TextChannel) event.getChannel();
        switch (type) {
            case "prune":
                int amount = Integer.parseInt(id[2]);
                event.getChannel().getIterableHistory()
                        .skipTo(event.getMessageIdLong())
                        .takeAsync(amount)
                        .thenAccept(channel::purgeMessages);
                // fallthrough delete the prompt message with our buttons
            case "delete":
                event.getHook().deleteOriginal().queue();
        }
    }

    @Override
    public void onUserContextInteraction(@NotNull UserContextInteractionEvent event) {
        switch (event.getName()) {
            default -> {
                event.reply("Something went wrong with that command :c").setEphemeral(true).queue();
                logger.error("Failure in user context interaction " + event.getName());
            }
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getMessage().getMentions().isMentioned(event.getJDA().getSelfUser())) {
            event.getMessage().getChannel().sendMessage("Hai c:").queue();
        }
    }

    public void say(SlashCommandInteractionEvent event, String content) {
        if (event.getUser().getId().equals("")) {
            event.reply(content).queue();
        } else {
            event.reply("Only Cheesetron's allowed to do that \uD83D\uDE1C").setEphemeral(true).queue();
        }
    }

    /*
    public void prune(SlashCommandInteractionEvent event) {
        OptionMapping amountOption = event.getOption("amount");  // This is configured to be optional so check for null
        int amount = amountOption == null
                ? 100 // default 100
                : (int) Math.min(200, Math.max(2, amountOption.getAsLong()));  // enforcement: must be between 2-200
        String userId = event.getUser().getId();
        event.reply("This will delete " + amount + " messages.\nAre you sure?")  // prompt the user with a button menu
                .addActionRow(
                        // this means "<style>(<id>, <label>)", you can encode anything you want in the id (up to 100 characters)
                        Button.secondary(userId + ":delete", "Nevermind"),
                        Button.danger(userId + ":prune:" + amount, "Yes!"))  // the first parameter is the component id we use in onButtonInteraction above
                .queue();
    }
    */
    private void boop(SlashCommandInteractionEvent event) {
        if (event.getOption("furry", false, OptionMapping::getAsBoolean)) {
            event.reply("You receive a light tap on the nose. The fuzzy paw makes it even better.").setEphemeral(true).queue();
        } else {
            event.reply("You receive a light tap on the nose. You feel better.").setEphemeral(true).queue();
        }

        event.getChannel().sendMessage(event.getUser().getAsMention() + ", boop!").queue();

        logger.always().log("Boopage has occurred.");
    }

    private void startPlaying(SlashCommandInteractionEvent event) {
        if (event.getGuild() == null) {
            event.reply("You can only do this in a server.").queue();
            return;
        }

        //noinspection DataFlowIssue already checked if we're in a guild
        if (!event.getMember().getVoiceState().inAudioChannel()) {
            event.reply("You need to be in a voice channel first.").queue();
            return;
        }

        //event.deferReply(true).queue();
        String guildId = event.getGuild().getId();

        AudioManager guildAudioManager = event.getGuild().getAudioManager();
        guildAudioManager.openAudioConnection(event.getMember().getVoiceState().getChannel());

        if (!guildPlayers.containsKey(guildId) || !guildQueues.containsKey(guildId)) {


            guildPlayers.put(guildId, playerManager.createPlayer());

            // we don't need to keep track of this object as we don't need it again
            AudioPlayerSendHandler guildAudioPlayerSendHandler = new AudioPlayerSendHandler(guildPlayers.get(guildId));
            guildAudioManager.setSendingHandler(guildAudioPlayerSendHandler);

            guildQueues.put(guildId, new TrackScheduler());
            guildPlayers.get(guildId).addListener(guildQueues.get(guildId));
        }

        playerManager.loadItem(event.getOption("url", OptionMapping::getAsString), new AudioLoadResultHandler() {
            // This is an anonymous class.
            @Override
            public void trackLoaded(AudioTrack track) {
                event.reply("Playing " + track.getInfo().title + "...").queue();
                guildQueues.get(guildId).queue(guildPlayers.get(guildId), track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                event.reply("Queuing " + playlist.getName() + "...").queue();
                for (AudioTrack track : playlist.getTracks()) {
                    guildQueue.queue(guildPlayers.get(guildId), track);
                }
            }

            @Override
            public void noMatches() {
                event.reply("No video was found.").queue();
            }

            @Override
            public void loadFailed(FriendlyException throwable) {
                audioLogger.error("Error in loading: " + throwable.getMessage());
            }
        });
        audioLogger.debug("Loaded track.");
    }

    private void skipSong(SlashCommandInteractionEvent event) {
        if (event.getGuild() == null) {
            event.reply("You can only do this in a server.").queue();
            return;
        }

        if (guildPlayers.containsKey(event.getGuild().getId()) && guildQueues.get(event.getGuild().getId()).skip(guildPlayers.get(event.getGuild().getId()))) {
            event.reply("Skipped.").queue();
        } else {
            event.reply("No songs queued.").queue();
        }
    }

    private void toggleRepeat(SlashCommandInteractionEvent event) {
        if (event.getGuild() == null) {
            event.reply("You can only do this in a server.").queue();
            return;
        }

        String guildID = event.getGuild().getId();

        TrackScheduler guildQueue = guildQueues.get(guildID);

        if (guildPlayers.containsKey(guildID)) {
            if (!guildQueue.repeat) {
                event.reply("Repeat is on.").setEphemeral(true).queue();
                guildQueue.repeat = true;
            }
            else {
                event.reply("Repeat is off.").setEphemeral(true).queue();
                guildQueue.repeat = false;
            }
        } else {
            event.reply("No songs queued.").queue();
        }
    }

    private void leaveVoice(SlashCommandInteractionEvent event) {
        if (event.getGuild() == null) event.reply("You can only do this in a server.").queue();

        //noinspection DataFlowIssue  CacheFlag.VOICE_STATE should be enabled.
        if (event.getGuild().getSelfMember().getVoiceState().inAudioChannel()) {
            event.getGuild().getAudioManager().closeAudioConnection();
            event.reply("Left voice channel.").queue();
            guildQueues.get(event.getGuild().getId()).clearQueue();
        }
    }
}

/*####      ::
#####    :::::
##     ***:::
##    ******
##   *****    #
#   ****     ##
   **        ##
  *     #######
       ######*/