package main.java.com.kucing;

public class LoginManager {
    private static String currentUsername;
    
    public static void setCurrentUsername(String username) {
        currentUsername = username;
    }
    
    public static String getCurrentUsername() {
        return currentUsername;
    }
}