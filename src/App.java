import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class App extends ListenerAdapter{
    public static void main(String[] args) throws Exception {
        String[] commands = {"run", "help"};
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
        //chumba
        if (message.toLowerCase().contains("!run")){
            if (message.substring(5).toLowerCase().contains("chumba")){
                String[] parts = message.split("\\s+", 4);
                if (parts.length < 4) {
                    event.getChannel().sendMessage("Usage: `!run <service> <email> <password>`").queue();
                    return;
                }

                String service = parts[1].toLowerCase();
                String email = parts[2].trim();
                String password = parts[3].trim();
                
                System.out.printf("Running %s with account %s:%s", service,email,password);

            }
            //event.getChannel().sendMessage("Pong!").queue();
        }
    }
    public static boolean checkFormat(String message, String[] commands) {
        if (message == null || message.trim().isEmpty()) {
            return false;
        }
        if (!message.startsWith("!")) {
            return false;
        }
    
        String[] messageParts = message.split("\\s+");
        if (messageParts.length < 2) {
            return false;
        }

        String command = messageParts[1].toLowerCase();
        if (command.equals("help")) {
            return messageParts.length == 2;
        }
    
        if (messageParts.length < 4) {
            return false;
        }
        if (!messageParts[2].contains("@")) {
            return false;
        }
    
        if (messageParts[3].trim().isEmpty()) {
            return false;
        }
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

