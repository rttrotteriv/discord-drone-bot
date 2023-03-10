import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;

/**
 * Starts the drone bot and initializes commands.
 */
public class Main extends ListenerAdapter {
    public static void main(String[] args) {
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
        System.out.print("In guilds:");
        for (Guild guild : jda.getGuilds()) {
            System.out.print(" | " + guild.getName());
        }
        System.out.println();
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