package application;

/**
 * The User class represents a user entity in the system.
 * It contains user details such as userName, password, role, and email.
 */
public class User {
    private String userName;
    private String password;
    private String role;
    private String email;
    private boolean isFirstLogin;

    // Constructor for new users (includes email)
    public User(String userName, String password, String role, String email, boolean isFirstLogin) {
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.email = email;
        this.isFirstLogin = isFirstLogin;
    }
    
    public User() {
        this.userName = "";
        this.password = "";
        this.role = "";
        this.email = "";
        this.isFirstLogin = false;
    }

    
    public User(String userName, String password, String role, String email) {
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.email = email;
        this.isFirstLogin = false; // Default value
    }
    
    public void setEmail(String email) {
        if (email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            this.email = email;
        } else {
            throw new IllegalArgumentException("Invalid email format");
        }
    }



    // Constructor for existing users (default email to "")
    public User(String userName, String password, String role, boolean isFirstLogin) {
        this(userName, password, role, "", isFirstLogin);
    }

    // Getters and Setters
    public String getUserName() { return userName; }
    public String getPassword() { return password; } // In real use, hash passwords
    public String getRole() { return role; }
    public String getEmail() { return email; }
    public boolean isFirstLogin() { return isFirstLogin; }

    public void setRole(String role) { this.role = role; }
    public void setFirstLogin(boolean firstLogin) { this.isFirstLogin = firstLogin; }
}
