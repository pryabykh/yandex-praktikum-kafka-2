package com.pryabykh.yandex_praktikum_kafka_2.stream;

import com.pryabykh.yandex_praktikum_kafka_2.component.CensorComponent;
import com.pryabykh.yandex_praktikum_kafka_2.dto.MessageDto;
import com.pryabykh.yandex_praktikum_kafka_2.mapper.BlockedUsersMapper;
import com.pryabykh.yandex_praktikum_kafka_2.mapper.MessageMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.Set;

import static com.pryabykh.yandex_praktikum_kafka_2.constant.KafkaConstants.MESSAGES_TOPIC_NAME;
import static com.pryabykh.yandex_praktikum_kafka_2.constant.KafkaConstants.BLOCKED_USERS_STORE_NAME;
import static com.pryabykh.yandex_praktikum_kafka_2.constant.KafkaConstants.BLOCKED_USERS_TOPIC_NAME;
import static com.pryabykh.yandex_praktikum_kafka_2.constant.KafkaConstants.FILTERED_MESSAGES_TOPIC_NAME;

@Component
public class MessagesStream {
    private static final Logger log = LoggerFactory.getLogger(MessagesStream.class);
    private static final String streamName = "messages-stream";
    private final BlockedUsersMapper blockedUsersMapper;
    private final MessageMapper messageMapper;
    private final CensorComponent censorComponent;
    private KafkaStreams streams;

    @Value("${bootstrap.servers}")
    private String bootstrapServers;

    public MessagesStream(BlockedUsersMapper blockedUsersMapper,
                          MessageMapper messageMapper,
                          CensorComponent censorComponent) {
        this.blockedUsersMapper = blockedUsersMapper;
        this.messageMapper = messageMapper;
        this.censorComponent = censorComponent;
    }

    @PostConstruct
    public void run() {
        // Создаем конфигурацию стрима
        Properties config = new Properties();
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, streamName);
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        StreamsBuilder builder = new StreamsBuilder();

        // Подключаемся к топику с сообщениями
        KStream<String, String> stream = builder.stream(MESSAGES_TOPIC_NAME);

        // Создаем KTable: по login пользователя можно получить список заблокированных для этого пользователя
        KTable<String, String> blockedUsersTable = createBlockedUsersTable(builder);

        // Описываем действия внутри стрима
        stream
                // Логируем все входящие сообщения
                .peek((k, v) -> log.info("\uD83D\uDCA4Обработка сообщения : {}", v))

                // Джоиним заблокированных юзеров к сообщению
                .leftJoin(blockedUsersTable,
                        (message, blockedUsers) -> {
                            MessageDto messageDto = messageMapper.deserialize(message);
                            messageDto.setBlockedUsers(blockedUsersMapper.deserialize(blockedUsers));
                            return messageMapper.serialize(messageDto);
                        })

                // Отфильтровываем сообщения, которые пришли от юзеров из списка заблокированных
                .filter((k, v) -> {
                    MessageDto messageDto = messageMapper.deserialize(v);
                    if (messageDto.getBlockedUsers().contains(messageDto.getFrom().name())) {
                        log.info("\uD83D\uDEABСообщение {} не будет отправлено, так как пользователь заблокирован!!!", v);
                        return false;
                    }
                    return true;
                })

                // Применяем цензуру
                .map((k, v) -> KeyValue.pair(k, censorComponent.apply(v)))

                // Логируем сообщение перед отправкой в топик отфильтрованных сообщения
                .peek((k, v) -> log.info("\uD83E\uDD73Сообщение {} передано пользователю!!!", v))

                // Передаем сообещние в топик отфильтрованных
                .to(FILTERED_MESSAGES_TOPIC_NAME);

        // Создаем описанный сконфигурированных стрим и запускаем его
        streams = new KafkaStreams(builder.build(), config);
        streams.start();

        log.info("Stream {} успешно запущен", streamName);
    }

    @PreDestroy
    void closeStream() {
        log.info("Закрываем stream {}", streamName);
        streams.close();
    }

    private KTable<String, String> createBlockedUsersTable(StreamsBuilder builder) {
        // Подключаемся к топику с заблокированными юзерами
        KStream<String, String> blockEvents = builder.stream(BLOCKED_USERS_TOPIC_NAME);

        // Агрегируем сообщения из топика таким образом,
        // чтобы в персистентом хранилище мы имели заблокированных юезров по login пользователя
        return blockEvents
                .groupByKey()
                .aggregate(
                        () -> "[]",
                        (blockerId, blockedId, currentState) -> {
                            Set<String> currentSet = blockedUsersMapper.deserialize(currentState);
                            currentSet.add(blockedId);
                            return blockedUsersMapper.serialize(currentSet);
                        },
                        Materialized.<String, String, KeyValueStore<Bytes, byte[]>>as(BLOCKED_USERS_STORE_NAME)
                                .withKeySerde(Serdes.String())
                                .withValueSerde(Serdes.String())
                );
    }
}
