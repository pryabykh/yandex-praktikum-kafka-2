package com.pryabykh.yandex_praktikum_kafka_2.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pryabykh.yandex_praktikum_kafka_2.dto.MessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {
    private static final Logger log = LoggerFactory.getLogger(MessageMapper.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String serialize(MessageDto dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            log.error("Возникла ошибка сериализации сообщения", e);
            throw new RuntimeException(e);
        }
    }

    public MessageDto deserialize(String json) {
        try {
            return objectMapper.readValue(json, MessageDto.class);
        } catch (JsonProcessingException e) {
            log.error("Возникла ошибка десериализации сообщения", e);
            throw new RuntimeException(e);
        }
    }
}
