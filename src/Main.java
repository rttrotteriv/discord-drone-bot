import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
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
            jda = JDABuilder.create("OTIxOTM1NzU5NjA1MzY2ODI0.Yb6JlQ.WikNE78fB16-bTK2bCHkMkMkEdk", // TODO configuration file instead of hardcoded token
                            EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT))
                    .addEventListeners(new DroneBotEventListener())
                    .build();
        } catch (ErrorResponseException | LoginException exception) {
            System.err.println("Problem connecting to Discord. " + exception.getMessage());
            System.exit(1);
        }

        try { jda.awaitReady(); } catch (InterruptedException e) {
            System.err.println("awaitReady was interrupted, JDA caches may be incomplete!");
        }

        jda.getPresence().setActivity(Activity.playing("now from Java!"));

        // Initialize commands if told to
        if (java.util.Arrays.asList(args).contains("-initialize")) CommandInitializer.initializeSlashCommands(jda);

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