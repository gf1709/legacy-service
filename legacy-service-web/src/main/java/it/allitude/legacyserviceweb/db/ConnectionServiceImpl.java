package it.allitude.legacyserviceweb.db;

import java.sql.Connection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibm.as400.access.AS400;

import it.allitude.legacyserviceweb.authentication.JwtTokenUtil;
import it.allitude.legacyserviceweb.models.AppConfig;
import it.allitude.legacyserviceweb.models.JSession;

@Service
public class ConnectionServiceImpl implements InitializingBean, DisposableBean, ConnectionService {

    @Autowired
    AppConfig _cfg;

    @Autowired
    JwtTokenUtil _jwtTokenUtil;

    @Autowired 
    it.allitude.legacyserviceweb.db.OracleDataSource _oracOracleDataSource;
    
    private static final Logger logger = LoggerFactory.getLogger(ConnectionServiceImpl.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("afterPropertiesSet....");
    }

    private final Map<String, ISeriesConnection> _as400Connections = new ConcurrentHashMap<>();

    public ConnectionServiceImpl() {      
    }

    @Override
    public Connection getOracleS2AConnection() throws Exception
    {
        return _oracOracleDataSource.getOracleS2AConnection();
    }
    @Override
    public Connection getOracleMSSConnection() throws Exception
    {
        return _oracOracleDataSource.getOracleMSSConnection();
    }
    @Override
    public Connection getAS400JdbcConnection() throws Exception {
        String iseries_user = JSession.getCurrentSession().getUser();
        String iseries_session = JSession.getCurrentSession().getTerminal();
        String authToken = JSession.getCurrentSession().getJwt();
        String iseries_password = _jwtTokenUtil.getPasswordFromToken(authToken);

        String key = getMapKey();
        ISeriesConnection as400ByUser = _as400Connections.get(key);

        if (as400ByUser == null) {
            as400ByUser = new ISeriesConnection(_cfg.getISeriesName(), iseries_user, iseries_password, iseries_session);
            _as400Connections.put(key, as400ByUser);
        }
        return as400ByUser.getAS400JdbcConnection();
    }

    @Override
    public AS400 getAS400Connection() throws Exception {

        String iseries_user = JSession.getCurrentSession().getUser();
        String iseries_session = JSession.getCurrentSession().getTerminal();
        String authToken = JSession.getCurrentSession().getJwt();
        String iseries_password = _jwtTokenUtil.getPasswordFromToken(authToken);

        String key = getMapKey();
        ISeriesConnection newISeriesConnection = _as400Connections.get(key);

        if (newISeriesConnection == null) {
            newISeriesConnection = new ISeriesConnection(_cfg.getISeriesName(), iseries_user, iseries_password,
                    iseries_session);
            _as400Connections.put(key, newISeriesConnection);
        }
        return newISeriesConnection.getAS400Connection();
    }

    @Override
    public void destroy() throws Exception {
        logger.info("destroying....");
    }

    @Override
    public void logout() throws Exception {
        String key = getMapKey();
        if (_as400Connections.containsKey(key)) {
            ISeriesConnection iSeriesConnection = _as400Connections.get(key);
            iSeriesConnection.close();
            _as400Connections.remove(key);
        }
    }

    String getMapKey() {
        // LocalDateTime now = LocalDateTime.now();
        // DateTimeFormatter dateTimeFormatter =
        // DateTimeFormatter.ofPattern("yyyy_MM_dd_HH");
        // String key = String.format("%s_%s_%s", now.format(dateTimeFormatter),
        // JSession.getCurrentSession().getUser(),
        // JSession.getCurrentSession().getTerminal() );
        // key = "fc0382_FCTXE_2024_01_17_09_57";
        String key = String.format("%s_%s", JSession.getCurrentSession().getUser(),
                JSession.getCurrentSession().getTerminal());
        return key;
    }
}
