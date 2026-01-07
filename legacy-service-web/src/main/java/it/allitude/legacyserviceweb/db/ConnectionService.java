package it.allitude.legacyserviceweb.db;

import java.sql.Connection;

import com.ibm.as400.access.AS400;

public interface ConnectionService  {
    public Connection getAS400JdbcConnection() throws Exception;
    public AS400 getAS400Connection()  throws Exception ;
    public Connection getOracleS2AConnection() throws Exception;
    public Connection getOracleMSSConnection() throws Exception;
    public void logout()  throws Exception ;
}
