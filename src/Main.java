import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import static net.dv8tion.jda.api.interactions.commands.OptionType.*;

import javax.security.auth.login.LoginException;
import java.util.EnumSet;

/**
 * Starts the drone bot and initializes commands.
 */
public class Main extends ListenerAdapter {
    public static void main(String[] args) throws LoginException, InterruptedException {
        JDA jda = JDABuilder.createLight("OTIxOTM1NzU5NjA1MzY2ODI0.Yb6JlQ.WikNE78fB16-bTK2bCHkMkMkEdk",
                        EnumSet.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT))
                .addEventListeners(new DroneBot())
                .build();

        jda.awaitReady();
        // jda.getPresence().setActivity(Activity.playing("now from Java!"));
        jda.getPresence().setActivity(Activity.playing("with beta code. Might be wonky for a bit, hang tight."));

        // Start of command initialization logic
        if (java.util.Arrays.asList(args).contains("-initialize"))  // TODO implement initialization logic
        {
            // CommandListUpdateAction commands = jda.updateCommands();
            CommandListUpdateAction commands = jda.getGuildById("927561153213767760").updateCommands();
            // Use first for global commands, second only for 'The Testing Grounds'.

            //noinspection ResultOfMethodCallIgnored
            commands.addCommands(
                    Commands.slash("say", "Makes the bot say what you tell it to")
                            .addOption(STRING, "message", "What the bot should say", true) // you can add required options like this too
            );

            //noinspection ResultOfMethodCallIgnored
            commands.addCommands(
                    Commands.slash("prune", "Prune messages from this channel")
                            .addOption(INTEGER, "amount", "How many messages to prune (Default 100)") // simple optional argument
                            .setGuildOnly(true)
                            .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.MESSAGE_MANAGE))

            );

            //noinspection ResultOfMethodCallIgnored
            commands.addCommands(
                    Commands.slash("boop", "Receive a boop")
                            .addOption(BOOLEAN, "furry", "If paw-boops are your thing.")
            );

            // Send updated list to discord
            commands.queue();
            System.out.println("Commands initialized");  // TODO implement actual logging
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