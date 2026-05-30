package nexlink.client.auth;

/**
 *
 * @author ALI
 */
public class LoginScreenLogic {

    private String username;
    private String password;
    public boolean isAuthenticated = false;
    public boolean isRegisterTriggered = false;
    // This lock object will manage thread signaling
    public final Object lock = new Object();

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
