package it.allitude.legacyserviceweb.controllers;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.ForeachAction;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.allitude.legacyserviceweb.authentication.JwtTokenUtil;
import it.allitude.legacyserviceweb.db.ConnectionService;
import it.allitude.legacyserviceweb.models.AppConfig;

@RestController
@CrossOrigin(origins = { "*" })
@RequestMapping({ "/api" })

public class DepositCertificateConsumer {

    @Autowired
    AppConfig _cfg;

    @Autowired
    private ConnectionService _connectionService;

    @Autowired
    private JwtTokenUtil _jwtTokenUtil;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    KafkaStreams _streams = null;
    private static final String SQL_INSERT = "INSERT INTO LBFCBUDT.JO23T (JO23ID,JO23TL,JO23PL,JO23TR,JO23US,JO23DE,JO23OE,JO23DA) VALUES (?,?,?,?,?,?,?,?)";
    int _counter = 0;

    @GetMapping("/stop-consumer")
    public void end() {
        logger.info("Stopping consumer...");
        _streams.close();
    }

    @GetMapping("/start-consumer")
    public void start(@RequestHeader("Authorization") String token) throws Exception {
        logger.info("Starting consumer...");
        _counter = 0;
        java.sql.Connection con = _connectionService.getAS400JdbcConnection();
        String user = _jwtTokenUtil.getUsernameFromToken(token);
        String session = _jwtTokenUtil.getSessionFromToken(token);        

        String sql = "INSERT INTO LBFCBUDT.JO23T (JO23ID,JO23TL,JO23PL,JO23TR,JO23US,JO23DE,JO23OE,JO23DA) values (?,?,?,?,?,?,?,?)";
        PreparedStatement stmt = con.prepareStatement(sql);

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, _cfg.getApplicationId());
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, _cfg.getBootstrapServers());

        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        // props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG,
        // _cfg.getKafkaKeyDeserializerClass());
        // props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,
        // _cfg.getKafkaValueDeserializerClass());

        props.put("schema.registry.url", _cfg.getKafkaSchemaRegistryUrl());
        props.put("basic.auth.credentials.source", _cfg.getKafkaBasicAuthCredentialsSource());
        props.put("basic.auth.user.info", _cfg.getKafkaUserName() + ":" + _cfg.getKafkaPassword());

        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        props.put(SaslConfigs.SASL_JAAS_CONFIG,
                "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + _cfg.getKafkaUserName()
                        + "\" password=\"" + _cfg.getKafkaPassword() + "\";");

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> kStream = builder.stream(_cfg.getTopic());

        kStream.foreach(new ForeachAction<String, String>() {
            public void apply(String key, String value) {
                logger.info("Il valore letto e' : " + key + ": " + value);
                String val = "";
                for (int i = 0; i < value.length(); i++) {
                    if ((value.charAt(i) >= 'a' && value.charAt(i) <= 'z')
                            || (value.charAt(i) >= 'A' && value.charAt(i) <= 'Z')
                            || (value.charAt(i) >= '0' && value.charAt(i) <= '9')
                            || (value.charAt(i) == ':')
                            || (value.charAt(i) == '"')
                            || (value.charAt(i) == ' ')
                            || (value.charAt(i) == '{')
                            || (value.charAt(i) == '}')
                            || (value.charAt(i) == '/')
                            || (value.charAt(i) == '_')
                            || (value.charAt(i) == ',')
                            || (value.charAt(i) == ';')
                            || (value.charAt(i) == '.')

                    ) {
                        val = val + value.charAt(i);
                    }
                }

                val = val.substring(0, Integer.min(200, val.length()));

                try {
                    stmt.setString(1, LocalDateTime.now().toString());
                    stmt.setString(2, "KAFKA-STREAMS-TEST");
                    stmt.setInt(3, 1);
                    stmt.setString(4, session);
                    stmt.setString(5, user);
                    
                    DateFormat timeDateFormat = new SimpleDateFormat("yyyyMMdd");
                    stmt.setString(6, timeDateFormat.format(new Date()));
                    
                    timeDateFormat = new SimpleDateFormat("HHmmss");
                    stmt.setString(7, timeDateFormat.format(new Date()));
                    
                    stmt.setString(8, val);
                    int row = stmt.executeUpdate();
                    _counter += 1;
                    logger.info("----------------->>> Nr. Row inserted " + Integer.toString(row));
                    logger.info("Sleeping after insert...");
                    Thread.sleep(2000);
                    logger.info("Got Up !!!");

                    if (_counter > 1)
                    {
                        logger.info("Stopping streams...");
                        _streams.close();
                        logger.info("streams stopped");
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        logger.info("Kafka streams reading is starting....");
        _streams = new KafkaStreams(builder.build(), props);
        _streams.start();
        logger.info("Kafka streams reading started");

        // Alla chiusura del runtime chiudo lo stream
        Runtime.getRuntime().addShutdownHook(new Thread(_streams::close));

        logger.info("Main thread exiting....");
    }
}
