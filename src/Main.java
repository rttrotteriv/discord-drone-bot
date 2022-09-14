import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;


public class DroneBot extends ListenerAdapter
{
    public static void main(String[] args) throws LoginException, InterruptedException {
        JDA jda = JDABuilder.createLight("OTIxOTM1NzU5NjA1MzY2ODI0.Yb6JlQ.WikNE78fB16-bTK2bCHkMkMkEdk",
                        EnumSet.noneOf(GatewayIntent.class))
                .addEventListeners(new SlashBotExample())
                .build();

        jda.awaitReady();
        jda.getPresence().setActivity(Activity.playing("now from Java!"));
        // jda.getPresence().setActivity(Activity.playing("with beta code. Might be wonky for a bit, hang tight."));

        // Start of command initialization logic
        if (java.util.Arrays.asList(args).contains("-initialize"))  // TODO implement initialization logic
        {
            CommandListUpdateAction commands = jda.updateCommands();
            // CommandListUpdateAction commands = jda.getGuildById("927561153213767760").updateCommands();
            // Use first for global commands, second only for 'The Testing Grounds'.

            commands.addCommands(
                    Commands.slash("say", "Makes the bot say what you tell it to")
                            .addOption(STRING, "message", "What the bot should say", true) // you can add required options like this too
            );

            commands.addCommands(
                    Commands.slash("prune", "Prune messages from this channel")
                            .addOption(INTEGER, "amount", "How many messages to prune (Default 100)") // simple optional argument
                            .setGuildOnly(true)
                            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE))

            );

            // Send updated list to discord
            commands.queue();
            System.out.println("Commands initialized");  // TODO implement actual logging
        }
    }


    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event)
    {
        // Only accept commands from guilds
        if (event.getGuild() == null)
            return;
        switch (event.getName()) {
            case "say" ->
                // content is required so no null-check here
                say(event, event.getOption("content").getAsString());
            case "prune" -> prune(event);
            case "blep" -> blep(event);
            default ->
                // the registered command isn't handled in code
                event.reply("Something went wrong with that command :c").setEphemeral(true).queue();
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event)
    {
        String[] id = event.getComponentId().split(":");  // this is the custom id we specified in our button
        String authorId = id[0];
        String type = id[1];
        // Check that the button is for the user that clicked it, otherwise just ignore the event (let interaction fail)
        if (!authorId.equals(event.getUser().getId()))
            return;
        event.deferEdit().queue();  // acknowledge the button was clicked, otherwise the interaction will fail

        MessageChannel channel = event.getChannel();
        switch (type)
        {
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

    public void say(SlashCommandInteractionEvent event, String content)
    {
        event.reply(content).queue(); // This requires no permissions!
    }

    public void prune(SlashCommandInteractionEvent event)
    {
        OptionMapping amountOption = event.getOption("amount");  // This is configured to be optional so check for null
        int amount = amountOption == null
                ? 100 // default 100
                : (int) Math.min(200, Math.max(2, amountOption.getAsLong()));  // enforcement: must be between 2-200
        String userId = event.getUser().getId();
        event.reply("This will delete " + amount + " messages.\nAre you sure?")  // prompt the user with a button menu
            .addActionRow(
                    // this means "<style>(<id>, <label>)", you can encode anything you want in the id (up to 100 characters)
                Button.secondary(userId + ":delete", "Nevermind!"),
                Button.danger(userId + ":prune:" + amount, "Yes!"))  // the first parameter is the component id we use in onButtonInteraction above
            .queue();
    }

    public void blep(SlashCommandInteractionEvent event)
    {
        event.reply("Hewo! I'll send a cute" + "your way.").queue();
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