import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class App extends ListenerAdapter {

    private static final String[] commands = {"run", "help"};
    private static final String path = "scripts/";
    private final Map<Long, UserSession> sessions = new HashMap<>();



    public static void main(String[] args) throws Exception {
        String token = "token";
        JDABuilder.createDefault(token)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(new App())
                .build();
    }

    @Override
    public void onMessageReceived(@javax.annotation.Nonnull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        String message = event.getMessage().getContentRaw();
        long userId = event.getAuthor().getIdLong();

        //if user is already in session
        if (sessions.containsKey(userId)) {
            UserSession session = sessions.get(userId);

            if (session.stage == 1) {
                if (!message.contains(":")) {
                    event.getChannel().sendMessage("Invalid format. Use: `email@gmail.com:AppPasswordHere`").queue();
                    return;
                }

                String[] creds = message.split(":", 2);
                session.gmail = creds[0];
                session.gmailPassword = creds[1];
                session.stage = 2;

                event.getChannel().sendMessage("Got your credentials. Running script...").queue();
                runPythonScript(event, session.service, session.casinoEmail, session.casinoPassword, session.gmail, session.gmailPassword);
                sessions.remove(userId);
            }
            return;
        }

        //inital command, user is not in session
        if (checkFormat(message, commands)) {
            String[] parts = message.split("\\s+", 4);
            String command = parts[0].substring(1).toLowerCase();

            if (command.equals("run")) {
                if (parts.length < 4) {
                    event.getChannel().sendMessage("Invalid !run format. Use: `!run <service> <email> <password>`").queue();
                    return;
                }

                String service = parts[1];
                String casinoEmail = parts[2];
                String casinoPassword = parts[3];

                UserSession session = new UserSession(service, casinoEmail, casinoPassword);
                sessions.put(userId, session);

                event.getChannel().sendMessage("Please enter your Gmail and App Password. Format: `email@gmail.com:AppPasswordHere`").queue();
            }

            if (command.equals("help")) {
                event.getChannel().sendMessage("Usage: `!run <service> <email> <password>`").queue();
            }
        }
    }

    private void runPythonScript(MessageReceivedEvent event, String scriptName, String casinoEmail, String casinoPassword, String gmail, String gmailPassword) {
        try {
            ProcessBuilder pb = new ProcessBuilder("python3", path + scriptName + ".py",
            casinoEmail, casinoPassword, gmail, gmailPassword
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader console = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;

            while ((line = console.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                event.getMessage().reply(scriptName + ".py ran successfully. Response: " + output).queue();
            } else {
                event.getMessage().reply(scriptName + ".py failed. Please try again later. exit code " + exitCode).queue();
            }
        } catch (Exception e) {
            event.getMessage().reply(scriptName + ".py response lost. Please contact @nono").queue();
        }
    }

    public static boolean checkFormat(String message, String[] commands) {
        if (message == null || message.trim().isEmpty()) return false;
        if (message.toLowerCase().contains("help")) {
            return true;
        }
        if (!message.startsWith("!")) return false;

        String[] parts = message.split("\\s+");
        if (parts.length < 1) return false;

        String command = parts[0].substring(1).toLowerCase();

        if (command.equals("help")) {
            return parts.length == 1;
        }

        boolean validCommand = false;
        for (String c : commands) {
            if (command.equalsIgnoreCase(c)) {
                validCommand = true;
                break;
            }
        }
        if (!validCommand) return false;

        if (command.equals("run")) {
            if (parts.length != 4) {
                return false;
            }
            if (!parts[2].contains("@")) {
                return false;
            }
            if (parts[3].trim().isEmpty()) {
                return false;
            }
        }

        return true;
    }
}
