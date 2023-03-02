import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;


/**
 * Class that listens to and responds to slash commands and buttons.
 */
public class DroneBotEventListener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        System.out.println("Command received");
        // Only accept commands from guilds
        if (event.getGuild() == null)
            return;
        switch (event.getName()) {
            case "say" ->
                // content is required so no null-check here
                //noinspection DataFlowIssue
                    say(event, event.getOption("content").getAsString());
            case "prune" -> prune(event);
            case "boop" -> boop(event);
            default -> {
                // the registered command isn't handled in code
                event.reply("Something went wrong with that command :c").setEphemeral(true).queue();
                System.out.println("Command didn't work");
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
            case "debug" -> stopResponding(event);
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getMessage().getMentions().isMentioned(event.getJDA().getSelfUser())) {
            event.getMessage().getChannel().sendMessage("Hai c:").queue();
        }
    }

    private void stopResponding(UserContextInteractionEvent event) {
        if (event.getGuild() == null && event.getUser().getId().equals("685525568581926933")) {
            int sleepTime = event.getOption("time", 3600, OptionMapping::getAsInt);
            event.reply("This instance not responding for " + sleepTime + " seconds.").queue();
            System.gc();
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                throw new RuntimeException(e); // just crash if it's interrupted
            }
        } else {
            event.reply("You're not allowed to do that.").queue();
        }
    }

    public void say(SlashCommandInteractionEvent event, String content) {
        event.reply(content).queue(); // This requires no permissions as it's a reply
    }

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

    public void boop(SlashCommandInteractionEvent event) {
        if (event.getOption("furry", false, OptionMapping::getAsBoolean)) {
            event.reply("You receive a light tap on the nose. The fuzzy paw makes it even better.").setEphemeral(true).queue();
        } else {
            event.reply("You receive a light tap on the nose. You feel better.").setEphemeral(true).queue();
        }

        event.getChannel().sendMessage(event.getUser().getAsMention() + ", boop!").queue();
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