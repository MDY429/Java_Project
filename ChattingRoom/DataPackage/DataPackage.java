import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * The DataPackage include the all information into a package.
 * It will be transferred between server and client.
 * @author Ta-Yu Mar
 * @version 0.1 beta 2020-03-05
 */
public class DataPackage implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * The Package Type List
     * 0: Sign In 
     * 1: Sign Up
     */
    public int type;

    // Check the transmission status is success or failed.
    public int flag;

    // User information.
    public int userId;
    public String userName;
    public String userPw;
    public String email;
    
    // Send Msg information.
    public int receiveUserId;
    public String receiveUserName;
    public String message;

    // Data
    public List<OnlineUser> onlineUser= null;

    // online User
    public class OnlineUser {
        public int userId;
        public String userName;
        public OnlineUser(int userId, String userName) {
            this.userId = userId;
            this.userName = userName;
        }
    }

    /**
     * Constructor for data package.
     */
    public DataPackage() {

    }

    public void addOnlineUserInfo(int userId, String userName) {
        if(onlineUser == null) {
            onlineUser = new ArrayList<>();
        }
        OnlineUser users = new OnlineUser(userId, userName);
        onlineUser.add(users);
    }

    /**
     * Via Json jar to transfer the package information to string.
     */
    @Override
    public String toString() {
        Object obj = JSON.toJSON(this);
        return obj.toString();
    }

    /**
     * Via Json jar to build a package.
     * @param pkgString The input package information.
     * @return DataPackage
     */
    public static DataPackage fromString(String pkgString) {
        return JSON.parseObject(pkgString, DataPackage.class);
    }
}