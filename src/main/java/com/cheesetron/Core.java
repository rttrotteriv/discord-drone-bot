package com.cheesetron;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;

/**
 * Starts the drone bot and initializes commands.
 */
public class Core extends ListenerAdapter {
    private static final Logger logger = LogManager.getLogger("com.cheesetron.dronebot");

    public static void main(String[] args) {
        if (args.length < 1) {
            logger.log(Level.FATAL, "No API token provided.");
            System.exit(1);
        }

        JDA jda = null;

        try {
            jda = JDABuilder.create(args[args.length - 1],
                            EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_VOICE_STATES))
                    .addEventListeners(new DroneBotEventListener())
                    .build();
        } catch (ErrorResponseException | LoginException exception) {
            System.err.println("Problem connecting to Discord. " + exception.getMessage());
            System.exit(1);
        }

        try { jda.awaitReady(); } catch (InterruptedException e) {
            System.err.println("awaitReady was interrupted, JDA caches may be incomplete!");
        }

        jda.getPresence().setActivity(Activity.playing("it works!"));

        // Initialize commands if told to
        if (java.util.Arrays.asList(args).contains("-initialize")) CommandInitializer.initializeSlashCommands(jda);

        // Log connected guilds
        if (logger.isInfoEnabled()) {
            StringBuilder guildList = new StringBuilder();
            for (Guild guild : jda.getGuilds()) {
                guildList.append(" ").append(guild.getName());
            }
            logger.info("In guilds:" + guildList);
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