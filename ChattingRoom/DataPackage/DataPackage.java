import com.alibaba.fastjson.JSON;

import java.io.Serializable;

public class DataPackage implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public int type = 0;
    public int flag = 0;
    public String str = "AAAAA";

    public DataPackage(){

    }
    // public String toString(){
    //     return str;
    // }

    @Override
    public String toString() {
        Object obj = JSON.toJSON(this);
        return obj.toString();
    }

    public static DataPackage fromString(String pkgString) {
        return JSON.parseObject(pkgString, DataPackage.class);
    }
}