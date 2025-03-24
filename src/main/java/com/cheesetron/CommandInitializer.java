package com.cheesetron;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.InteractionContextType;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

/**
 * Class with one method to initialize slash commands for a given JDA.
 */
public class CommandInitializer {
    private static final Logger logger = LogManager.getLogger("com.cheesetron.dronebot");

    public static void initializeSlashCommands(JDA jda) {
        CommandListUpdateAction commands = jda.updateCommands();

        commands.addCommands(
                        Commands.slash("say", "Makes the bot say what you tell it to")
                                .addOption(STRING, "message", "What the bot should say", true)
                                .setContexts(InteractionContextType.GUILD, InteractionContextType.PRIVATE_CHANNEL),

                        /*
                        Commands.slash("prune", "Prune messages from this channel")
                                .addOption(INTEGER, "amount", "How many messages to prune (Default 100)") // simple optional argument
                                .setGuildOnly(true)
                                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE)),
                        */

                        Commands.slash("play", "Queue a youtube video or playlist audio")
                                .addOption(STRING, "url", "Video or playlist URL", true)
                                .setContexts(InteractionContextType.GUILD),

                        Commands.slash("skip", "Skip to next song in queue")
                                .setContexts(InteractionContextType.GUILD),

                        Commands.slash("repeat", "Toggle repeating this song indefinitely")
                                .setContexts(InteractionContextType.GUILD),

                        Commands.slash("leave", "Removes the bot from the voice channel")
                                .setContexts(InteractionContextType.GUILD),

                        Commands.slash("boop", "Receive a boop")
                                .addOption(BOOLEAN, "furry", "If paw-boops are your thing.")
                                .setContexts(InteractionContextType.GUILD, InteractionContextType.PRIVATE_CHANNEL,
                                             InteractionContextType.BOT_DM)
                )

                // Send updated list to discord
                .queue();

        logger.info("Commands initialized");
    }
}
