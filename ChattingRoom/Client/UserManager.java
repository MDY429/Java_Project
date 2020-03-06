/**
 * The UserManager is middle role of client and server.
 * @author Ta-Yu Mar
 * @version 0.1 beta 2020-03-05
 */
public class UserManager {

    /**
     * Server handle user sign up new account.
     * @param pkg The input of data package.
     * @param dataHandler The input of corresponding data handler.
     */
    public void userSignUp(DataPackage pkg, DataHandler dataHandler){
        boolean checkdatabase = true;
        // TODO: Here will connect Database and check other details.
        if(checkdatabase){
            System.out.println("UserManager - signUp");
            User user = new User(pkg.username, pkg.userpw);

            // Get correspond data handler.
            user.setDataHandler(dataHandler);

            pkg.flag = 1;
            
            // Send back to Client and return success.
            user.getDataHandler().sendDataHandle(pkg.toString());
        }
        else {
            // Send back sign up fail.
            System.out.println("User sign up Error!!!");
            pkg.flag = 0;
            
            dataHandler.sendDataHandle(pkg.toString());
            return;
        }
    }
}