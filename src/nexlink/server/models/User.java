package nexlink.server.models;

/**
 *
 * @author ALI
 */
public class User {

    private int id;
    private String username;
    private String password;
    private String status;

    // register new user
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.status = "offline";
        
    }

    // fetch from DB
    public User(int id, String username, String password, String status) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) { this.status = status; }

}
