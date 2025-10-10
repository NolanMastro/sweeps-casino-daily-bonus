import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;

public class App extends ListenerAdapter {

    private static final String SCRIPTS_PATH = "scripts/";

    public static void main(String[] args) throws Exception {
    String token = "token";
    String GUILD_ID = "1420870365890220105";//temp

    JDA jda = JDABuilder.createDefault(token)
            .addEventListeners(new App())
            .build();

    jda.awaitReady();

    //temporary just to show slash commands instantly for my guild.
    OptionData serviceOption = new OptionData(OptionType.STRING, "service", "Service name", true)
        .addChoice("Chumba", "chumba")
        .addChoice("Dara", "dara")
        .addChoice("Modo", "modo");

    jda.getGuildById(GUILD_ID)
            .updateCommands()
            .addCommands(
                    Commands.slash("run", "Run a script")
                            .addOptions(
                                    serviceOption,
                                    new OptionData(OptionType.STRING, "email", "Casino email", true),
                                    new OptionData(OptionType.STRING, "password", "Casino password", true)
                            ),
                    Commands.slash("setgmail", "Set your Gmail and App Password")
                            .addOption(OptionType.STRING, "email", "Your Gmail", true)
                            .addOption(OptionType.STRING, "app_password", "Your Gmail App Password", true),
                    Commands.slash("help", "Show help info")
            ).queue();


    System.out.println("Bot is ready and slash commands registered for guild " + GUILD_ID);
}

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent event) {
        String name = event.getName();

        switch (name) {
            case "run" -> handleRunCommand(event);
            case "setgmail" -> handleSetGmailCommand(event);
            case "help" -> handleHelpCommand(event);
        }
    }


    private void handleSetGmailCommand(SlashCommandInteractionEvent event) {
        String gmail = event.getOption("email").getAsString().strip();
        String appPassword = event.getOption("app_password").getAsString().strip();
        long userId = event.getUser().getIdLong();

        UserStorage.setUserData(userId, gmail, appPassword);
        event.reply("[Success] Saved gmail and app password.").queue();
    }

    private void handleRunCommand(SlashCommandInteractionEvent event) {
        String service = event.getOption("service").getAsString();
        String casinoEmail = event.getOption("email").getAsString();
        String casinoPassword = event.getOption("password").getAsString();
        long userId = event.getUser().getIdLong();

        //try to get saved gmail and app password data
        UserStorage.UserData userData = UserStorage.getUserData(userId);
        String gmail = userData != null ? userData.gmail : "";
        String appPassword = userData != null ? userData.appPassword : "";

        event.reply("Running script.").queue();

        runPythonScriptAsync(service, casinoEmail, casinoPassword, gmail, appPassword, (output, success) -> {
            TextChannel channel = event.getChannel().asTextChannel();
            if (success) {
                channel.sendMessage("Script `" + service + ".py` ran successfully:\n```\n" + output + "\n```").queue();
            } else {
                channel.sendMessage("Script `" + service + ".py` failed. Please try again later.").queue();
            }
        });
    }

    private void handleHelpCommand(SlashCommandInteractionEvent event) {
        String helpMsg = "**Available Commands:**\n" +
                "/run <service> <email> <password> - Run a script\n" +
                "/setgmail <email> <app_password> - Save Gmail and App Password locally\n" +
                "/help - Show this message";
        event.reply(helpMsg).queue();
    }




    private void runPythonScriptAsync(String scriptName, String casinoEmail, String casinoPassword,
                                      String gmail, String gmailPassword, PythonCallback callback) {
        CompletableFuture.runAsync(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder("python", SCRIPTS_PATH + scriptName + ".py",
                casinoEmail, casinoPassword, gmail, gmailPassword);
                pb.redirectErrorStream(true);
                Process process = pb.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                StringBuilder output = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }

                int exitCode = process.waitFor();
                boolean success = exitCode == 0;
                callback.onComplete(output.toString(), success);

            } catch (Exception e) {
                e.printStackTrace();
                callback.onComplete("", false);
            }
        });
    }

    interface PythonCallback {
        void onComplete(String output, boolean success);
    }
}
