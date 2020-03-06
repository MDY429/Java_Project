import com.alibaba.fastjson.JSON;

import java.io.Serializable;

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
    public int type = 0;

    // Check the transmission status is success or failed.
    public int flag = 0;

    // User information.
    public String username = "default";
    public String userpw = "";
    public String message = "";

    /**
     * Constructor for data package.
     */
    public DataPackage() {

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