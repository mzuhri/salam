package at.ac.tuwien.dsg.smartsociety.demo.rest.common;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Util {

    private static Properties properties = null;
    private static String lastPropFile = "";

    private static void initProperties(String file) {
        if (properties==null || !lastPropFile.equals(file)) {
            properties = new Properties(); 
            try { 
                properties.load(new FileInputStream(file));  
                lastPropFile = file;
            } catch (IOException e) { 
                System.out.println(e);
            }
        }
    }

    public static String getProperty(String file, String key) {
        initProperties(file);
        return properties.getProperty(key);
    }

}
