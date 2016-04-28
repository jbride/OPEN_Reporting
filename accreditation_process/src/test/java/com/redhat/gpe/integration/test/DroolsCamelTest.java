package com.redhat.gpe.integration.test;

import com.redhat.gpe.domain.canonical.AccreditationDefinition;
import com.redhat.gpte.util.PropertiesSupport;

import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * Created by samueltauil on 2/23/16.
 */
public class DroolsCamelTest extends CamelSpringTestSupport {

    private static final String DROOLS_CAMEL_ROUTE_URI = "cc_simple_rule_call1";

    private static final Logger logger = LoggerFactory.getLogger(DroolsCamelTest.class);
    private String routeURI = null;

    public DroolsCamelTest() throws IOException {
        PropertiesSupport.setupProps();
    }

    @Before
    public void init() {

        routeURI = System.getProperty(DROOLS_CAMEL_ROUTE_URI);
        if(routeURI == null)
            throw new RuntimeException("init() must pass a system property: "+DROOLS_CAMEL_ROUTE_URI);

    }

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("/spring/accreditation-camel-context.xml");
    }

    @Ignore
    @Test
    public void testDroolsCamel() throws InterruptedException {

        template.setDefaultEndpointUri(routeURI);
        template.sendBody(new AccreditationDefinition(1,"a","b","c","d","e","f"));
        StatefulKnowledgeSession ksession = (StatefulKnowledgeSession) applicationContext.getBean("ksession1");
        ksession.fireAllRules();

        Assert.assertEquals(1, ksession.getFactHandles().size());

        for (FactHandle factHandle: ksession.getFactHandles()) {
            ksession.delete(factHandle);
        }
        Assert.assertEquals(0, ksession.getFactHandles().size());
    }
}
