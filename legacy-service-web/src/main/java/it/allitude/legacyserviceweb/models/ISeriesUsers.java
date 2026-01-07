package it.allitude.legacyserviceweb.models;

import java.sql.Connection;
import java.util.HashMap;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import it.allitude.legacyserviceweb.db.ConnectionService;

@Component
public class ISeriesUsers {

    private final AppConfig appConfig;
    private static final Logger log = LoggerFactory.getLogger(ISeriesUsers.class);
    private final ConnectionService _connectionService;
    private HashMap<String, String> users = new HashMap<>();
    
    public ISeriesUsers(ConnectionService anISeriesConnectionService, AppConfig appConfig) throws Exception {
        this._connectionService = anISeriesConnectionService;
        this.appConfig = appConfig;         
    }

    public Pair<String, String> getUser(String user) {
        String u = user.toUpperCase();
        return getUsers().containsKey(u) ? Pair.of(u, getUsers().get(u)) : null;
    }
    public String getUserDescription(String user) {
        String u = user.toUpperCase();
        return getUsers().containsKey(u) ? getUsers().get(u) : "";
    }

    public HashMap<String, String> getUsers() {
        if (users.isEmpty()) {
            try {
                String sql = "select authorization_name as NAME ,text_description as DES from qsys2.user_info";
                Connection con = _connectionService.getAS400JdbcConnection();
                java.sql.Statement stmt = con.createStatement();
                java.sql.ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    String usr = rs.getString("NAME").trim();
                    String des = rs.getString("DES").trim();
                    users.put(usr, des);
                }
            } catch (Exception ex) {
                log.error("Errore nel caricamento della lista utenti: " + ex.getMessage());
            }
        }   
        return users;
    }


}
