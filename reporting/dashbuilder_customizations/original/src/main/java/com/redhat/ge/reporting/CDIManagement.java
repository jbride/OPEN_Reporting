package com.redhat.ge.reporting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessBean;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
import javax.enterprise.util.AnnotationLiteral;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CDIManagement implements Extension {
    
    private static final String LOG_BEFORE_BEAN_DISCOVERY="com.redhat.gp.reporting.cdi.logBeforeBeanDiscovery";
    private static final String LOG_PROCESS_ANNOTATED_TYPE="com.redhat.gp.reporting.cdi.logProcessAnnotatedType";
    private static final String LOG_AFTER_BEAN_DISCOVERY="com.redhat.gp.reporting.cdi.logAfterBeanDiscovery";
    private static final String LOG_PROCESS_INJECTION_TARGET="com.redhat.gp.reporting.cdi.logProcessInjectionTarget";
    private static final String LOG_PROCESS_PRODUCER="com.redhat.gp.reporting.cdi.logProcessProducer";
    private static final String LOG_AFTER_DEPLOYMENT_VALIDATION="com.redhat.gp.reporting.cdi.logAfterDeploymentValidation";
    private static final String LOG_BEFORE_SHUTDOWN="com.redhat.gp.reporting.cdi.logBeforeShutdown";
    private static final String LOG_PROCESS_BEAN="com.redhat.gp.reporting.cdi.logProcessBean";
    private static final String VETO_CLASSES="com.redhat.gp.reporting.cdi.space.delimited.veto.classes";
    
    private Logger log = LoggerFactory.getLogger(CDIManagement.class);
    private boolean logBeforeBeanDiscovery = false;
    private boolean logProcessAnnotatedType = false;
    private boolean logProcessInjectionTarget = false;
    private boolean logProcessProducer = false;
    private boolean logAfterBeanDiscovery = false;
    private boolean logAfterDeploymentValidation = false;
    private boolean logBeforeShutdown = false;
    private boolean logProcessBean = false;
    private List<String> vetoClasses;
   
    /*
     *  the CDI container will pick up this extension via the Java ServiceLoader mechanism
     *  place a file named javax.enterprise.inject.spi.Extension (the same name as the interface) within the META-INF/services
     *  ensure this file contins the fully-qualified name of this Extension implementation
     *  The container will look up the name of the class at runtime and instantiate it via the default constructor  
     */ 
    public CDIManagement(){
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("CDIManagement() public constructor");
        logBeforeBeanDiscovery = Boolean.parseBoolean(System.getProperty(this.LOG_BEFORE_BEAN_DISCOVERY, "FALSE"));
        logProcessAnnotatedType = Boolean.parseBoolean(System.getProperty(this.LOG_PROCESS_ANNOTATED_TYPE, "FALSE"));
        logProcessInjectionTarget = Boolean.parseBoolean(System.getProperty(this.LOG_PROCESS_INJECTION_TARGET, "FALSE"));
        logProcessProducer = Boolean.parseBoolean(System.getProperty(this.LOG_PROCESS_PRODUCER, "FALSE"));
        logAfterBeanDiscovery = Boolean.parseBoolean(System.getProperty(this.LOG_AFTER_BEAN_DISCOVERY, "FALSE"));
        logAfterDeploymentValidation = Boolean.parseBoolean(System.getProperty(this.LOG_AFTER_DEPLOYMENT_VALIDATION, "FALSE"));
        logBeforeShutdown = Boolean.parseBoolean(System.getProperty(this.LOG_BEFORE_SHUTDOWN, "FALSE"));
        logProcessBean = Boolean.parseBoolean(System.getProperty(this.LOG_PROCESS_BEAN, "FALSE"));
        String vetoClassesString = System.getProperty(this.VETO_CLASSES);
        
        sBuilder.append("\nlogBeforeBeanDiscovery = "+logBeforeBeanDiscovery);
        sBuilder.append("\nlogProcessAnnotatedType = "+logProcessAnnotatedType);
        sBuilder.append("\nlogProcessInjectionTarget = "+logProcessInjectionTarget);
        sBuilder.append("\nlogProcessProducer = "+logProcessProducer);
        sBuilder.append("\nlogAfterBeanDiscovery = "+logAfterBeanDiscovery);
        sBuilder.append("\nlogAfterDeploymentValidation = "+logAfterDeploymentValidation);
        sBuilder.append("\nlogBeforeShutdown = "+logBeforeShutdown);
        sBuilder.append("\nlogProcessBean = "+logProcessBean);
        sBuilder.append("\nvetoClassesString = "+vetoClassesString);
        log.info(sBuilder.toString());
        
        if(vetoClassesString != null){
            vetoClasses = new ArrayList<String>();
            String[] vetoClassesArray = vetoClassesString.split("\\s");
            for(String vetoClass : vetoClassesArray){
                vetoClasses.add(vetoClass);
            }
        }
    }
    
    public void beforeBeanDiscovery(@Observes BeforeBeanDiscovery bbd, BeanManager bm) {
        if(this.logBeforeBeanDiscovery)
            log.info("beforeBeanDiscovery() bbd = "+bbd);
    }
    
    public void processAnnotatedType(@Observes ProcessAnnotatedType pat, BeanManager bm) {
        String name = pat.getAnnotatedType().getJavaClass().getName();
        if(this.logProcessAnnotatedType)
            log.info("processAnnotatedType() class = "+name);
       
        // NOTE:  This does not prevent the class from being instantiated 
        if(vetoClasses != null && vetoClasses.contains(name)){
            pat.veto();
            log.info("processAnnotatedType() just vetoed : "+ name);
        }
    }
    
    public void processInjectionTarget(@Observes ProcessInjectionTarget pit, BeanManager bm){
        if(this.logProcessInjectionTarget)
            log.info("processInjectionTarget() class = "+pit.getAnnotatedType().toString());
    }
    
    public void processBean(@Observes ProcessBean pBean, BeanManager bm) {
        String name = pBean.getBean().getBeanClass().getName();
        if(this.logProcessBean)
            log.info("processBean() class = "+name);
    
    }
    
    public void afterBeanDiscovery(@Observes AfterBeanDiscovery abd, BeanManager bm) {
        if(this.logAfterBeanDiscovery){
            Set<Bean<?>> allBeans = bm.getBeans(Object.class, new AnnotationLiteral<Any>() {});
            StringBuilder sBuilder = new StringBuilder();
            for(Bean bObj : allBeans){
                sBuilder.append("\n\t"+bObj.getBeanClass().getName());
            }
            log.info("afterBeanDiscovery() registered beans = "+sBuilder.toString());
        }
    }
    
    public void logProcessInjectionTarget(@Observes ProcessInjectionTarget pit) {
        if(this.logProcessInjectionTarget){
            Iterator injectionPoints = pit.getInjectionTarget().getInjectionPoints().iterator();
            StringBuilder sBuilder = new StringBuilder();
            sBuilder.append("processInjectionTarget() pit = "+pit.getClass());
            while(injectionPoints.hasNext()) {
                Object obj = injectionPoints.next();
                if(obj instanceof InjectionPoint){
                    sBuilder.append("\n\tinjectionPoint = "+((InjectionPoint)obj).toString());
                }else {
                    sBuilder.append("\n\t class = "+obj.getClass().toString());
                }
            }
        }
    }

}
