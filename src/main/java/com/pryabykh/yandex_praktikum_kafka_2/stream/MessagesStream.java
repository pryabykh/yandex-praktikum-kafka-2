package com.pryabykh.yandex_praktikum_kafka_2.stream;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

import static com.pryabykh.yandex_praktikum_kafka_2.constant.KafkaConstants.ALL_MESSAGES_TOPIC_NAME;
import static com.pryabykh.yandex_praktikum_kafka_2.constant.KafkaConstants.VALID_MESSAGES_TOPIC_NAME;

@Component
public class MessagesStream {
    private static final Logger log = LoggerFactory.getLogger(MessagesStream.class);
    private static final String streamName = "messages-stream";
    private KafkaStreams streams;

    @Value("${bootstrap.servers}")
    private String bootstrapServers;

    @PostConstruct
    public void run() throws Exception {
        ;
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, streamName);
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> stream = builder.stream(ALL_MESSAGES_TOPIC_NAME);

        stream.peek((key, value) -> log.info("Получено сообщение: {}", value))
                .to(VALID_MESSAGES_TOPIC_NAME);

        streams = new KafkaStreams(builder.build(), config);
        streams.start();

        log.info("Stream {} успешно запущен", streamName);
    }

    @PreDestroy
    void closeStream() {
        log.info("Закрываем stream {}", streamName);
        streams.close();
    }
}
