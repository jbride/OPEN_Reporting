package com.redhat.gpte.studentregistration.test;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.sun.jndi.ldap.LdapCtx;

import com.redhat.gpte.util.PropertiesSupport;

@SuppressWarnings("restriction")
public class LDAPTest {
    
    public static final String UID = "uid=";
    public static final String MAIL = "mail=";
    public static final String COMMA = ",";
    public static final String BASE_CTX_DN = "cn=users,cn=accounts,dc=opentlc,dc=com";
    public static final String IPA_PROVIDER_URL = "ipa.provider.url";
    public static final String IPA_SECURITY_PRINCIPAL = "ipa.security.principal";
    public static final String IPA_SECURITY_CREDENTIALS = "ipa.security.credentials";
    public static final String NUMBER_TEST_LDAP_LOOPS = "ipa.number.test.ldap.loops";
    public static final String TEST_LDAP_ID = "ipa.test.ldap.id";
    private static final String OPEN_PAREN = "(";
    private static final String CLOSE_PAREN = ")";
    private static final String TITLE_ATTRIBUTE = "title";
    private static final String GEO_ATTRIBUTE_PREFIX = "cn=rhpds-geo-";
    public static final int GPTE_QUERY_SIZE_LIMIT = 2000;
    public static final String GPSETRAINING_WILDCARD = "gpsetraining*";
    private static final String MEMBEROF_ATTRIBUTE = "memberOf";
    private static final String GEO_ATTRIBUTE_SUFFIX = ",cn=groups,cn=accounts,dc=opentlc,dc=com";
    
    private String providerUrl = null;
    private String securityPrincipal = null;
    private String securityCredentials = null;
    private String testLDAPId = null;
    private String testEmail = "jbride@redhat.com";
    private Hashtable<String,String> env = new Hashtable<String,String>();
    
    @Before
    @Ignore
    public void init() throws java.io.IOException {
        PropertiesSupport.setupProps();

        providerUrl = System.getProperty(IPA_PROVIDER_URL);
        if(providerUrl == null)
            throw new RuntimeException("must pass system property: "+IPA_PROVIDER_URL);

        securityPrincipal = System.getProperty(IPA_SECURITY_PRINCIPAL); 
        securityCredentials = System.getProperty(IPA_SECURITY_CREDENTIALS);
        
        testLDAPId = System.getProperty(TEST_LDAP_ID);
        
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, providerUrl);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, securityPrincipal);
        env.put(Context.SECURITY_CREDENTIALS, securityCredentials);
        env.put("com.sun.jndi.ldap.connect.pool", "true"); // http://docs.oracle.com/javase/jndi/tutorial/ldap/connect/pool.html
    }
    
    @Ignore
    @Test
    public void testLDAPConnectionPool() throws NamingException, InterruptedException {

        int sleepDuration = 5000;
        int numberTestLDAPLoops = Integer.parseInt(System.getProperty(NUMBER_TEST_LDAP_LOOPS));

        StringBuilder sBuilder = new StringBuilder(UID);
        sBuilder.append(testLDAPId);
        sBuilder.append(COMMA);
        sBuilder.append(this.BASE_CTX_DN);
        System.out.println("testSearch() number of loops = "+numberTestLDAPLoops+" : will search on: "+sBuilder.toString());
        for(int c = 0; c < numberTestLDAPLoops; c++) {
            DirContext ctx = null;
            LdapCtx lookupResult = null;
            
            // while in this loop, use the following shell command to identify # of open LDAP connections:   lsof -i :ldaps | wc -l
            
            try {
                ctx = grabLDAPConnectionFromPool();
                lookupResult = (LdapCtx) ctx.lookup(sBuilder.toString());
            } finally {
                
                // Critical that both LDAP result object and the connection are closed
                // http://blog.avisi.nl/2015/03/27/java-ldap-connection-pooling/
                if(lookupResult != null)
                    lookupResult.close();
                if(ctx != null) {
                    ctx.close();
                }
            }
            System.out.println("testLDAPConnectionPool lookupResult = "+lookupResult+" using the following ldapId = "+testLDAPId+"\n");
            Thread.sleep(sleepDuration);
        }
    }
    
    @Ignore
    @Test
    public void listNames() throws NamingException, InterruptedException {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(this.OPEN_PAREN);
        sBuilder.append(this.UID);
        sBuilder.append(this.GPSETRAINING_WILDCARD);
        sBuilder.append(this.CLOSE_PAREN);
        System.out.println("testSearch() will search names on: "+sBuilder.toString());
        DirContext ctx = null;
        NamingEnumeration listResults = null;
        
        int count=0;
        try {
            ctx = grabLDAPConnectionFromPool();
            SearchControls ctls = new SearchControls();
            ctls.setCountLimit(this.GPTE_QUERY_SIZE_LIMIT);
            ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            
            listResults = (NamingEnumeration) ctx.search(this.BASE_CTX_DN, sBuilder.toString(), ctls);
            
            while (listResults.hasMore()) {
                count++;
                if(this.GPTE_QUERY_SIZE_LIMIT == count) {
                    System.out.println("listNames() will stop at GPTE_SIZE_LIMIT = "+this.GPTE_QUERY_SIZE_LIMIT);
                    break;
                }
                   NameClassPair ncp = (NameClassPair) listResults.next();
                   System.out.println(count +" : "+ncp.getName());
               }
        }catch(NamingException x) {
            System.out.println("NamingException: "+x.getLocalizedMessage()+" : query = : "+sBuilder.toString());
        } finally {
                
                // Critical that both LDAP result object and the connection are closed
                // http://blog.avisi.nl/2015/03/27/java-ldap-connection-pooling/
                if(listResults != null)
                    listResults.close();
                if(ctx != null) {
                    ctx.close();
                }
        }
    }
    
    @Ignore
    @Test
    public void searchEmail() throws NamingException, InterruptedException {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(this.OPEN_PAREN);
        sBuilder.append(this.MAIL);
        sBuilder.append(testEmail);
        sBuilder.append(this.CLOSE_PAREN);
        System.out.println("searchName()  will search on: "+sBuilder.toString());
    
        DirContext ctx = null;
        NamingEnumeration listResults = null;
        
        try {
            ctx = grabLDAPConnectionFromPool();
            
            SearchControls ctls = new SearchControls();
            ctls.setCountLimit(this.GPTE_QUERY_SIZE_LIMIT);
            ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            
            listResults = (NamingEnumeration) ctx.search(this.BASE_CTX_DN, sBuilder.toString(), ctls);
            if(!listResults.hasMore()) {
               throw new RuntimeException("following mail not found : "+testEmail);
            }

            while (listResults.hasMore()) {
               SearchResult si =(SearchResult)listResults.next();
               System.out.println("searchName() name = "+si.getName());
               Attributes attrs = si.getAttributes();
               if (attrs == null) {
                   System.out.println("   No attributes");
                   continue;
               }
               NamingEnumeration ae = attrs.getAll(); 


               while (ae.hasMoreElements()) {
                   Attribute attr =(Attribute)ae.next();
                   String id = attr.getID();
                   if(this.TITLE_ATTRIBUTE.equals(id)){
                       String attValue = (String)attr.get(0);
                       System.out.println("searchName() "+this.TITLE_ATTRIBUTE+" = "+attValue);
                   }else if(this.MEMBEROF_ATTRIBUTE.equals(id)) {
                       Enumeration vals = attr.getAll();
                       while (vals.hasMoreElements()) {
                           String attributeValue = (String)vals.nextElement();
                           if(attributeValue.startsWith(GEO_ATTRIBUTE_PREFIX)){
                               String geoValue = attributeValue.substring(13, attributeValue.indexOf(GEO_ATTRIBUTE_SUFFIX));
                               System.out.println("searchName() geo attribute; value = " + geoValue);
                           }
                       }
                   }
               }
           }
        }catch(NamingException x) {
            System.out.println("NamingException: "+x.getLocalizedMessage()+" : query = : "+sBuilder.toString());
        } finally {
                
                // Critical that both LDAP result object and the connection are closed
                // http://blog.avisi.nl/2015/03/27/java-ldap-connection-pooling/
                if(listResults != null)
                    listResults.close();
                if(ctx != null) {
                    ctx.close();
                }
        }
    }
    
    private DirContext grabLDAPConnectionFromPool() throws NamingException {
        try {
            DirContext ctx = new InitialDirContext(env);
            return ctx;
        }catch(AuthenticationException x) {
            System.out.println("init() unable to connect to: "+providerUrl+" using user="+securityPrincipal);
            throw x;
        }
    }

}
