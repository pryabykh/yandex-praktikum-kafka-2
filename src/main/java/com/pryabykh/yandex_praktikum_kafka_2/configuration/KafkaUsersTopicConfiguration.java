package com.pryabykh.yandex_praktikum_kafka_2.configuration;

import com.pryabykh.yandex_praktikum_kafka_2.constant.FixedUsers;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static com.pryabykh.yandex_praktikum_kafka_2.constant.KafkaConstants.USERS_TOPIC_NAME;

@Component
public class KafkaUsersTopicConfiguration implements CommandLineRunner {
    @Value("${bootstrap.servers}")
    private String bootstrapServers;
    private static final Logger log = LoggerFactory.getLogger(KafkaUsersTopicConfiguration.class);

    @Override
    public void run(String... args) {
        try (Admin admin = Admin.create(Collections.singletonMap("bootstrap.servers", bootstrapServers))) {
            NewTopic newTopic = new NewTopic(USERS_TOPIC_NAME, 3, (short) 2);
            admin.createTopics(Collections.singleton(newTopic)).all().get();
            log.info("Топик {} успешно создан!", USERS_TOPIC_NAME);
            loadUsers();
        } catch (ExecutionException | InterruptedException e) {
            if (e instanceof ExecutionException && ((ExecutionException) e).getCause() instanceof TopicExistsException) {
                log.info("Топик уже существует. Создание будет проигнорировано");
            } else {
                log.error("Ошибка при создании топика", e);
            }
        }
    }

    private void loadUsers() {
        Properties producerProps = new Properties();
        producerProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(producerProps)) {

            for (FixedUsers.User user : FixedUsers.list) {
                producer.send(new ProducerRecord<>(USERS_TOPIC_NAME, user.name(), ""));
            }
            producer.flush();
            System.out.println("Данные о пользователях загружены в топик");
        }
    }
}
