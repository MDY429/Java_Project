/**
 * The user class define the user's relative information.
 * @author Ta-Yu Mar
 * @version 0.1 beta 2020-03-05
 */
public class User {

    public String username;
    public String password;    
    private DataHandler dataHandler = null;

    /**
     * Constructor for user.
     */
    public User() {
        
    }

    /**
     * Constructor for user.
     * @param username
     * @param password
     */
    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

    /**
     * Get the corresponding data handler.
     * @return DataHandler
     */
    public DataHandler getDataHandler(){
        return dataHandler;
    }

    /**
     * Set the data handler.
     * @param dataHandler The input of dataHandler.
     */
    public void setDataHandler(DataHandler dataHandler){
        this.dataHandler = dataHandler;
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