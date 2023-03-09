import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

/**
 * Class with one method to initialize slash commands for a given JDA.
 */
public class CommandInitializer {
    public static void initializeSlashCommands(JDA jda) {
        CommandListUpdateAction commands = jda.updateCommands();

        commands.addCommands(
                        Commands.slash("say", "Makes the bot say what you tell it to")
                                .addOption(STRING, "message", "What the bot should say", true)

                /*
                ).addCommands(
                        Commands.slash("prune", "Prune messages from this channel")
                                .addOption(INTEGER, "amount", "How many messages to prune (Default 100)") // simple optional argument
                                .setGuildOnly(true)
                                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE))
                */

                ).addCommands(
                        Commands.slash("play", "Queue a youtube video or playlist audio")
                                .addOption(STRING, "url", "Video or playlist URL", true)
                                .setGuildOnly(true)

                ).addCommands(
                        Commands.slash("skip", "Skip to next song in queue")
                                .setGuildOnly(true)

                ).addCommands(
                        Commands.slash("leave", "Removes the bot from the voice channel")
                                .setGuildOnly(true)

                ).addCommands(
                        Commands.slash("boop", "Receive a boop")
                                .addOption(BOOLEAN, "furry", "If paw-boops are your thing.")
                )

                // Send updated list to discord
                .queue();

        System.out.println("Commands initialized");  // TODO implement actual logging
    }
}
