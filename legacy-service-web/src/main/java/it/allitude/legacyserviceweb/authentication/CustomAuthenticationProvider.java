package it.allitude.legacyserviceweb.authentication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import it.allitude.legacyserviceweb.models.AppConfig;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    AppConfig _cfg;
    
    protected final Log logger = LogFactory.getLog(getClass());

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        try {
            Class.forName("com.ibm.as400.access.AS400JDBCDriver");
        } catch (ClassNotFoundException ex) {
            System.err.println("JDBC Driver Not Found.");
        }

        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

        Connection con = getCheckLoginConnection(_cfg.getISeriesName(), name, password);
        if (con !=null)
        {
            Authentication auth = new UsernamePasswordAuthenticationToken(name, password, new ArrayList<>());
            try {
                con.close();
                con=null;
            } catch (SQLException e) {                
                e.printStackTrace();
            }
            return auth;
        }
        else        
            return null;  // utente non validato
    }

    private Connection getCheckLoginConnection(String sysname, String user, String password)  {
        String jdbc = "";
        String jdbcf = "jdbc:as400://%s;user=%s;password=%s;prompt=false;";
        jdbc = String.format(jdbcf, sysname, user,password);
        try {
            return DriverManager.getConnection(jdbc);
        } catch (SQLException e) {                        
            e.printStackTrace();            
        }
        return null;
    }
}