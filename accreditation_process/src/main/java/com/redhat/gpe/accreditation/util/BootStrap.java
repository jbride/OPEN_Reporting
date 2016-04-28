package com.redhat.gpe.accreditation.util;

import org.apache.camel.spring.Main;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.redhat.gpte.util.PropertiesSupport;

public class BootStrap {
    
    private static final String CAMEL_CONTEXT_PATH="camel_context_path";
    
    private static String camel_context_path = null;
    private Main springMain;
    private Logger logger = Logger.getLogger(getClass());
    
    public static void main (String[] args) throws Exception {
        
        PropertiesSupport.setupProps();
        
        camel_context_path = System.getProperty(CAMEL_CONTEXT_PATH);
        if(StringUtils.isEmpty(camel_context_path))
            throw new Exception("main() need to pass system property: "+CAMEL_CONTEXT_PATH);
        
        BootStrap bStrap = new BootStrap();
        bStrap.boot();
    }
    
    public void boot() throws Exception {
        
        springMain = new Main();
        logger.info("boot() starting with the following camel context: "+this.camel_context_path);
        springMain.setApplicationContext(new ClassPathXmlApplicationContext(this.camel_context_path));
        springMain.enableHangupSupport();
        springMain.run();
    }

}
