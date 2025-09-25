import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class App extends ListenerAdapter{
    public static void main(String[] args) throws Exception {
        String token = "token";

        JDABuilder.createDefault(token).addEventListeners(new App()).build();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        if (event.getAuthor().isBot()){
            return;
        }

        String message = event.getMessage().getContentRaw();

        if (message.equalsIgnoreCase("!ping")){
            event.getChannel().sendMessage("Pong!").queue();
        }
    }
}
