package com.redhat.gpte.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.Properties;

public abstract class PropertiesSupport {
    
    public static final String PROPS_FILE_LOCATION = "props_file_location";
    
    public static void setupProps() throws IOException {
        
        String propsFileLocation = System.getProperty(PROPS_FILE_LOCATION);
        if(propsFileLocation == null || propsFileLocation.equals(""))
            throw new RuntimeException("setupProps() need to set system property: "+PROPS_FILE_LOCATION);
        
        InputStream input = null; 
        try {
            Properties originalProps = System.getProperties();
            //printProps(originalProps);
            
            input = new FileInputStream(propsFileLocation);
            originalProps.load(input);
            
            // Simulate setting of jboss.host.name system property (which is available in a jboss environment)
            String hostname = InetAddress.getLocalHost().getHostName();
            originalProps.put("jboss.host.name", hostname);
            
            //printProps(originalProps);
            
        } finally {
            if(input !=null)
                input.close();
        }
    }
    
    public static void printProps(Properties props) {
        System.out.println("printProps() # of props = "+props.size());
        Enumeration<?> e = props.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            String value = props.getProperty(key);
            System.out.println("Key : " + key + ", Value : " + value);
        }
    }
}
