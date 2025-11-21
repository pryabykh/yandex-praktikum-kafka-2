package com.pryabykh.yandex_praktikum_kafka_2.component;

import com.pryabykh.yandex_praktikum_kafka_2.dto.MessageDto;
import com.pryabykh.yandex_praktikum_kafka_2.mapper.MessageMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class CensorComponent {
    private static final Logger log = LoggerFactory.getLogger(CensorComponent.class);

    private final Set<String> censuredStrings = new HashSet<>();
    private final MessageMapper messageMapper;

    public CensorComponent(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    public void add(String string) {
        if (string == null) {
            return;
        }
        log.info("Строка {} добавлена в список цензуры", string);
        censuredStrings.add(string);
    }

    public String apply(String json) {
        MessageDto message = messageMapper.deserialize(json);
        String content = message.getContent();
        for (String censuredString : censuredStrings) {
            if (content.toLowerCase().contains(censuredString.toLowerCase())) {
                content = content.replace(censuredString.toLowerCase(), "*".repeat(censuredString.length()));
            }
        }
        message.setContent(content);
        return messageMapper.serialize(message);
    }
}
