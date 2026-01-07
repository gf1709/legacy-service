package it.allitude.legacyserviceweb.models;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppConfig {
    @Value("${server.name}")
    private String _server;

    public String getServerName() {
        return _server;
    }

    @Value("${server.port}")
    private String _port;

    public String getServerPort() {
        return _port;
    }

    @Value("${iseries.name:#{null}}")
    private String _iseries_name;

    public String getISeriesName() {
        String result = _iseries_name;
        if (result == null || result.length() < 1)
            result = "localhost";
        return result;
    }
    
    @Value("${routes.topic}")
    String _topic;
    public String getTopic() {
        return _topic;
    }

    @Value("${kafka.streams.application-id}")
    String _application_id;
    public String getApplicationId() {
        return _application_id;
    }

    @Value("${kafka.streams.bootstrap-servers}")
    String _bootstrap_servers;
    public String getBootstrapServers() {
        return _bootstrap_servers;
    }

    @Value("${kafka.streams.default-key-serde-class}")
    String _default_key_serde_class;
    public String getKafkaKeyDeserializerClass() {
        return _default_key_serde_class;
    }
    @Value("${kafka.streams.default-value-serde-class}")
    String _default_value_serde_class;
    public String getKafkaValueDeserializerClass() {
        return _default_value_serde_class;
    }

    @Value("${kafka.username}")
    String _kafka_username;
    public String getKafkaUserName() {
        return _kafka_username;
    }
    @Value("${kafka.password}")
    String _kafka_password;
    public String getKafkaPassword() {
        return _kafka_password;
    }

    @Value("${kafka.schema-registry-url}")
    String _kafka_schema_registry_url;
    public String getKafkaSchemaRegistryUrl() {
        return _kafka_schema_registry_url;
    }

    @Value("${kafka.basic-auth-credentials-source}")
    String _kafka_basic_auth_credentials_source;
    public String getKafkaBasicAuthCredentialsSource() {
        return _kafka_basic_auth_credentials_source;
    }
}
