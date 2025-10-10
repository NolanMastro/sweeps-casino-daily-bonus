import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

//From https://github.com/google/gson modified for app.java


public class UserStorage {
    private static final String FILE_PATH = "users.json";
    private static final Gson gson = new Gson();
    private static Map<Long, UserData> users = new HashMap<>();

    static {
        load();
    }

    public static void load() {
        try (Reader reader = new FileReader(FILE_PATH)) {
            users = gson.fromJson(reader, new TypeToken<Map<Long, UserData>>(){}.getType());
            if (users == null) users = new HashMap<>();
        } catch (IOException e) {
            users = new HashMap<>();
        }
    }

    public static void save() {
        try (Writer writer = new FileWriter(FILE_PATH)) {
            gson.toJson(users, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setUserData(long userId, String gmail, String appPassword) {
        users.put(userId, new UserData(gmail, appPassword));
        save();
    }

    public static UserData getUserData(long userId) {
        return users.get(userId);
    }

    public static class UserData {
        String gmail;
        String appPassword;
        public UserData(String gmail, String appPassword) {
            this.gmail = gmail;
            this.appPassword = appPassword;
        }
    }
}