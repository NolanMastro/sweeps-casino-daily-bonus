import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;



public class App extends ListenerAdapter{

    private static final String[] commands = {"run", "help"};
    private static final String path = "scripts/";
    private static long nonoId = 1384615269384720488L;
    private final Map<Long, UserSession> sessions = new HashMap<>();

    public static void main(String[] args) throws Exception {
        
        String token = "";

        JDABuilder.createDefault(token).enableIntents(GatewayIntent.MESSAGE_CONTENT) .addEventListeners(new App()).build();
    }

    @Override
    public void onMessageReceived(@javax.annotation.Nonnull MessageReceivedEvent event){
        if (event.getAuthor().isBot()){
            return;
        }

        String message = event.getMessage().getContentRaw();
        long userId = event.getAuthor().getIdLong();
        
        //check if user is in session storage
        if (sessions.containsKey(userId)){
            UserSession session = sessions.get(userId);
            if (session.stage == 1){
                session.extraQuestion = message;
                event.getChannel().sendMessage("Please send email password").queue();
                session.stage = 2;
            }else if (session.stage == 2){
                session.name = message;
                event.getChannel().sendMessage("Running script.").queue();
                runPythonScript(event, session.service, session.email, session.password, session.extraQuestion, session.name);
                sessions.remove(userId);
            }
            return;
        }

        if (checkFormat(message, commands)){
            String[] parts = message.split("\\s+", 4); //max 4 parameters for disc cmds
            String command = parts[0].substring(1).toLowerCase();

            if (command.equals("run")){
                if (parts.length < 4) {
                    event.getChannel().sendMessage("Invalid !run format. Use: `!run <service> <email> <password>`").queue();
                    return;
                }

                String service = parts[1];
                String email = parts[2];
                String password = parts[3];

                UserSession session = new UserSession(service, email, password);
                sessions.put(userId, session);
                event.getChannel().sendMessage("answer before running").queue();

                //System.out.printf("Running %s for %s:%s%n", service, email, password);
                //runPythonScript(event, service, email, password);
            }
            if (command.equals("help")){
                event.getChannel().sendMessage("Usage: `!run <service> <email> <password>`").queue();
            }
        }


    }


    private void runPythonScript(MessageReceivedEvent event, String scriptName, String email, String password) {
        try {
            ProcessBuilder pb = new ProcessBuilder("python", path+scriptName+".py" , email, password);
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
                event.getMessage().reply(scriptName+".py ran successfuly. Response: " + output).queue();
            } else {
                event.getMessage().reply(scriptName +".py failed. Please try again later.");
            }
        } catch (Exception e) {
            event.getMessage().reply(scriptName +".py reponse lost. Please contact @nono Error: " + e);
        }
    }


    public static boolean checkFormat(String message, String[] commands) {

        if (message == null || message.trim().isEmpty()) return false;
        if (message.toLowerCase().contains("help")){
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
            if (parts.length != 4){
                 return false;
            }
            if (!parts[2].contains("@")){
                return false;
            }
            if (parts[3].trim().isEmpty()){
                return false;
            }
        }

        return true;
    }
    

}
