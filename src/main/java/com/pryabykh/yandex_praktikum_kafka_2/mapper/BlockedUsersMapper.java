package com.pryabykh.yandex_praktikum_kafka_2.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class BlockedUsersMapper {
    private static final Logger log = LoggerFactory.getLogger(BlockedUsersMapper.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String serialize(Set<String> set) {
        try {
            return objectMapper.writeValueAsString(set);
        } catch (JsonProcessingException e) {
            log.error("Возникла ошибка сериализации сообщения", e);
            throw new RuntimeException(e);
        }
    }

    public Set<String> deserialize(String json) {
        if (json == null) {
            return new HashSet<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            log.error("Возникла ошибка десериализации сообщения", e);
            throw new RuntimeException(e);
        }
    }
}
