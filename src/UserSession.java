public class UserSession {
    public String service;
    public String email;
    public String password;
    public String extraAnswer;
    public String name;
    public int stage = 1;

    public UserSession(String service, String email, String password) {
        this.service = service;
        this.email = email;
        this.password = password;
    }
}