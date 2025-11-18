package com.pryabykh.yandex_praktikum_kafka_2.configuration;

import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.TopicExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.concurrent.ExecutionException;

import static com.pryabykh.yandex_praktikum_kafka_2.constant.KafkaConstants.MESSAGES_TOPIC_NAME;

@Component
public class KafkaMessagesTopicConfiguration implements CommandLineRunner {
    @Value("${bootstrap.servers}")
    private String bootstrapServers;
    private static final Logger log = LoggerFactory.getLogger(KafkaMessagesTopicConfiguration.class);

    @Override
    public void run(String... args) {
        try (Admin admin = Admin.create(Collections.singletonMap("bootstrap.servers", bootstrapServers))) {
            NewTopic newTopic = new NewTopic(MESSAGES_TOPIC_NAME, 3, (short) 2);
            admin.createTopics(Collections.singleton(newTopic)).all().get();
            log.info("Топик {} успешно создан!", MESSAGES_TOPIC_NAME);
        } catch (ExecutionException | InterruptedException e) {
            if (e instanceof ExecutionException && ((ExecutionException) e).getCause() instanceof TopicExistsException) {
                log.info("Топик уже существует. Создание будет проигнорировано");
            } else {
                log.error("Ошибка при создании топика", e);
            }
        }
    }
}
