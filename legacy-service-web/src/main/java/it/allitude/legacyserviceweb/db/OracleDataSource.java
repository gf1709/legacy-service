package it.allitude.legacyserviceweb.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import oracle.jdbc.OracleConnection;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceFactory;

@Configuration
public class OracleDataSource {

	@Value("${spring.datasource.S2A.url}")
	String s2a_url;

	@Value("${spring.datasource.S2A.driver-class-name}")
	String s2a_driver_class_name;

	@Value("${spring.datasource.S2A.proxyUser}")
	String s2a_proxy_user;

	@Value("${spring.datasource.S2A.wallet.location}")
	String s2a_wallet_location;

	@Value("${spring.datasource.S2A.oracleucp.connection-pool-name}")
	String s2a_connection_pool_name;

	@Value("${spring.datasource.S2A.oracleucp.initial-pool-size}")
	int s2a_initial_pool_size;

	@Value("${spring.datasource.S2A.oracleucp.min-pool-size}")
	int s2a_min_pool_size;

	@Value("${spring.datasource.S2A.oracleucp.max-pool-size}")
	int s2a_max_pool_size;

	DataSource _s2a_oracleDataSource=null;


	@Value("${spring.datasource.MSS.url}")
	String mss_url;

	@Value("${spring.datasource.MSS.driver-class-name}")
	String mss_driver_class_name;

	@Value("${spring.datasource.MSS.proxyUser}")
	String mss_proxy_user;

	@Value("${spring.datasource.MSS.wallet.location}")
	String mss_wallet_location;

	@Value("${spring.datasource.MSS.oracleucp.connection-pool-name}")
	String mss_connection_pool_name;

	@Value("${spring.datasource.MSS.oracleucp.initial-pool-size}")
	int mss_initial_pool_size;

	@Value("${spring.datasource.MSS.oracleucp.min-pool-size}")
	int mss_min_pool_size;

	@Value("${spring.datasource.MSS.oracleucp.max-pool-size}")
	int mss_max_pool_size;

	DataSource _mss_oracleDataSource=null;


	DataSource getS2ADataSource() throws SQLException {

		System.setProperty("oracle.net.tns_admin", s2a_wallet_location);
		System.setProperty("oracle.net.wallet_location", s2a_wallet_location);
		PoolDataSource ds = PoolDataSourceFactory.getPoolDataSource();
		ds.setConnectionFactoryClassName(s2a_driver_class_name);
		ds.setURL(s2a_url);
		ds.setConnectionPoolName(s2a_connection_pool_name);
		// Default is 0. Set the initial number of connections to be created when UCP is started.
		ds.setInitialPoolSize(s2a_initial_pool_size);

		// Default is 0. Set the minimum number of connections that is maintained by UCP at runtime.
		ds.setMinPoolSize(s2a_min_pool_size);

		// Default is Integer.MAX_VALUE (2147483647). Set the maximum number of connections allowed on the connection pool.
		ds.setMaxPoolSize(s2a_max_pool_size);

		// Default is 30secs. Set the frequency in seconds to enforce the timeout properties. Applies to inactiveConnectionTimeout(int secs),
		// AbandonedConnectionTimeout(secs)& TimeToLiveConnectionTimeout(int secs).
		// Range of valid values is 0 to Integer.MAX_VALUE. .
		ds.setTimeoutCheckInterval(5);

		// Default is 0. Set the maximum time, in seconds, that a connection remains available in the connection pool.
		ds.setInactiveConnectionTimeout(10);

		Properties connProps = new Properties();
		connProps.setProperty("fixedString", "false");
		connProps.setProperty("remarksReporting", "false");
		connProps.setProperty("restrictGetTables", "false");
		connProps.setProperty("includeSynonyms", "false");
		connProps.setProperty("defaultNChar", "false");
		connProps.setProperty("AccumulateBatchResult", "false");
		connProps.setProperty("AccumulateBatchResult", "false");

		ds.getConnectionProperties().put(OracleConnection.CONNECTION_PROPERTY_PROXY_CLIENT_NAME, s2a_proxy_user);
		ds.setConnectionProperties(connProps);

		// for (int i = 0; i < 5; i++) {
		// 	Connection c = ds.getConnection();
		// 	try (ResultSet rs = c.createStatement().executeQuery("SELECT user  VAL from dual")) {
		// 		while (rs.next()) {
		// 			String res = " Oracle connected user is " + rs.getString("VAL");
		// 			System.out.println(res);
		// 		}
		// 	}
		// 	c.close();
		// }

		return ds;
	}

		DataSource getMSSDataSource() throws SQLException {

		System.setProperty("oracle.net.tns_admin", mss_wallet_location);
		System.setProperty("oracle.net.wallet_location", mss_wallet_location);
		PoolDataSource ds = PoolDataSourceFactory.getPoolDataSource();
		ds.setConnectionFactoryClassName(mss_driver_class_name);
		ds.setURL(mss_url);
		ds.setConnectionPoolName(mss_connection_pool_name);
		// Default is 0. Set the initial number of connections to be created when UCP is started.
		ds.setInitialPoolSize(mss_initial_pool_size);

		// Default is 0. Set the minimum number of connections that is maintained by UCP at runtime.
		ds.setMinPoolSize(mss_min_pool_size);

		// Default is Integer.MAX_VALUE (2147483647). Set the maximum number of connections allowed on the connection pool.
		ds.setMaxPoolSize(mss_max_pool_size);

		// Default is 30secs. Set the frequency in seconds to enforce the timeout properties. Applies to inactiveConnectionTimeout(int secs),
		// AbandonedConnectionTimeout(secs)& TimeToLiveConnectionTimeout(int secs).
		// Range of valid values is 0 to Integer.MAX_VALUE. .
		ds.setTimeoutCheckInterval(5);

		// Default is 0. Set the maximum time, in seconds, that a connection remains available in the connection pool.
		ds.setInactiveConnectionTimeout(10);

		Properties connProps = new Properties();
		connProps.setProperty("fixedString", "false");
		connProps.setProperty("remarksReporting", "false");
		connProps.setProperty("restrictGetTables", "false");
		connProps.setProperty("includeSynonyms", "false");
		connProps.setProperty("defaultNChar", "false");
		connProps.setProperty("AccumulateBatchResult", "false");
		connProps.setProperty("AccumulateBatchResult", "false");

		ds.getConnectionProperties().put(OracleConnection.CONNECTION_PROPERTY_PROXY_CLIENT_NAME, mss_proxy_user);
		ds.setConnectionProperties(connProps);

		// for (int i = 0; i < 5; i++) {
		// 	Connection c = ds.getConnection();
		// 	try (ResultSet rs = c.createStatement().executeQuery("SELECT user  VAL from dual")) {
		// 		while (rs.next()) {
		// 			String res = " Oracle connected user is " + rs.getString("VAL");
		// 			System.out.println(res);
		// 		}
		// 	}
		// 	c.close();
		// }

		return ds;
	}

	Connection getOracleS2AConnection() throws SQLException
	{
		if (_s2a_oracleDataSource==null)
			_s2a_oracleDataSource = getS2ADataSource();
		return _s2a_oracleDataSource.getConnection();
	}

	Connection getOracleMSSConnection() throws SQLException
	{
		if (_mss_oracleDataSource==null)
			_mss_oracleDataSource = getMSSDataSource();
		return _mss_oracleDataSource.getConnection();
	}

}
