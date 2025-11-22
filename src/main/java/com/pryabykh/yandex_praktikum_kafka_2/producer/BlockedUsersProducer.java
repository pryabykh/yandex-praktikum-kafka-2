package com.pryabykh.yandex_praktikum_kafka_2.producer;

import com.pryabykh.yandex_praktikum_kafka_2.mapper.MessageMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;

import static com.pryabykh.yandex_praktikum_kafka_2.constant.KafkaConstants.BLOCKED_USERS_TOPIC_NAME;

@Component
public class BlockedUsersProducer {
    private static final Logger log = LoggerFactory.getLogger(BlockedUsersProducer.class);
    @Autowired
    private MessageMapper messageMapper;
    private KafkaProducer producer;
    @Value("${bootstrap.servers}")
    private String bootstrapServers;

    @PostConstruct
    void initProducer() {
        Properties properties = new Properties();
        // подключаемся к брокеру
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        // при отправке сообщения ожидаем подтверждения всех брокеров
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        // при неудаче повторяем 3 раза
        properties.put(ProducerConfig.RETRIES_CONFIG, 3);
        // сериализация строковая
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        this.producer = new KafkaProducer<>(properties);
    }

    public void blockUser(String blocker, String blocked) {
        ProducerRecord<String, String> record = new ProducerRecord<>(
                BLOCKED_USERS_TOPIC_NAME,
                blocker,
                blocked
        );
        producer.send(record);
        log.info("Действие пользователь {} блокирует {} поставлено в очередь", blocker, blocked);
    }

    @PreDestroy
    void closeProducer() {
        log.info("Закрываем продьюсера");
        producer.close();
    }
}
