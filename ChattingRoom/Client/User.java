/**
 * The user class define the user's relative information.
 * @author Ta-Yu Mar
 * @version 0.1 beta 2020-03-05
 */
public class User {

    // User online status.
    public enum UserStatus {
        OFFLINE,    // 0
        ONLINE      // 1
    }

    public int userId;
    public String userName;
    private String password;
    private DataHandler dataHandler = null;
    private UserStatus status;

    /**
     * Constructor for user.
     */
    public User() {
        
    }

    /**
     * Constructor for user.
     * @param userId    The user ID from database.
     * @param userName  The user name.
     */
    public User(int userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    /**
     * Get user password.
     * @return Password string.
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Set user password
     * @param password The input of user password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get the corresponding data handler.
     * @return DataHandler
     */
    public DataHandler getDataHandler() {
        return dataHandler;
    }

    /**
     * Set the data handler.
     * @param dataHandler The input of dataHandler.
     */
    public void setDataHandler(DataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }

    /**
     * Get user online status.
     * @return UserStatus
     */
    public UserStatus getUserStatus() {
        return this.status;
    }

    /**
     * Set user online or offline status
     * @param status The input of UserStatus.
     */
    public void setUserStatus(UserStatus status) {
        this.status = status;
    }

    /**
     * Send the DataPackage to server.
     * @param pkg The input of data package.
     * @return boolean
     */
    public boolean sendDataPackage(DataPackage pkg) {
        return  dataHandler.sendDataHandle(pkg.toString());
    }

}