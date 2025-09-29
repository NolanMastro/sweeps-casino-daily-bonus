import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class App extends ListenerAdapter{

    private static final String[] commands = {"run", "help"};
    public static void main(String[] args) throws Exception {
        
        String token = "token";
        if (token == null){
            throw new IllegalStateException("Discord token not found in environmental variables.");
        }

        JDABuilder.createDefault(token).enableIntents(GatewayIntent.MESSAGE_CONTENT) .addEventListeners(new App()).build();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        if (event.getAuthor().isBot()){
            return;
        }

        String message = event.getMessage().getContentRaw();
        
        if (checkformat(message, commands)){
            String[] parts = message.split("\\s+", 4);
            String command = parts[1].toLowerCase();

            //chumba
            if (command.equals("run")){
                String service = parts[1].toLowerCase();
                String email = parts[2].trim();
                String password = parts[3].trim();



                System.out.printf("Running #s for %s:%s", service, email, password);
            }

            if (command.equals("help")){ //TODO add embed help message.
                event.getChannel().sendMessage("Usage: `!run <service> <email> <password>`").queue();
            }
        }else{
            event.getChannel().sendMessage("Invalid command format. Try `!help`.").queue();
        }



    }


    private void runPythonScript(MessageReceivedEvent event, String scriptPath, String email, String password) {
        try {
            ProcessBuilder pb = new ProcessBuilder("python", scriptPath, email, password); // can make scriptPath final, then add just name at end.
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
                event.getChannel().sendMessage("Script completed successfully.").queue();
            } else {
                event.getChannel().sendMessage("Script failed..."); //TODO add mentions, user name, and script name in messages.
            }
        } catch (Exception e) {
            event.getChannel().sendMessage("Script failed... " + e);
        }
    }


    public static boolean checkFormat(String message, String[] commands) {
        if (message == null || message.trim().isEmpty()) return false;
        if (!message.startsWith("!")) return false;

        String[] parts = message.split("\\s+");
        if (parts.length < 2) return false;

        String command = parts[1].toLowerCase();

        if (command.equals("help")) {
            return parts.length == 2;
        }

        if (parts.length < 4) return false;
        if (!parts[2].contains("@")) return false;
        if (parts[3].trim().isEmpty()) return false;

        for (String c : commands) {
            if (command.equalsIgnoreCase(c)) {
                return true;
            }
        }
        return false;
    }
}


not 4, false
not a named command, false