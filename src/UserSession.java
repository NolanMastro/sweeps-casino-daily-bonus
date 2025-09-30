class UserSession {
    String service;
    String casinoEmail;
    String casinoPassword;
    String gmail;
    String gmailPassword;
    int stage = 1;

    UserSession(String service, String casinoEmail, String casinoPassword) {
        this.service = service;
        this.casinoEmail = casinoEmail;
        this.casinoPassword = casinoPassword;
    }
}
