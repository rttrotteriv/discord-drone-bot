import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;

/**
 * Starts the drone bot and initializes commands.
 */
public class Main extends ListenerAdapter {
    public static void main(String[] args) throws LoginException, InterruptedException {
        JDA jda = JDABuilder.createLight("OTIxOTM1NzU5NjA1MzY2ODI0.Yb6JlQ.WikNE78fB16-bTK2bCHkMkMkEdk", // TODO configuration file instead of hardcoded token
                        EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT))
                .addEventListeners(new DroneBotEventListener())
                .build(); // start connecting and logging in

        jda.awaitReady();
        jda.getPresence().setActivity(Activity.playing("now from Java!"));

        // Start of command initialization logic
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