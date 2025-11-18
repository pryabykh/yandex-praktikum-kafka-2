package com.pryabykh.yandex_praktikum_kafka_2.controller;

import com.pryabykh.yandex_praktikum_kafka_2.dto.MessageDto;
import com.pryabykh.yandex_praktikum_kafka_2.producer.MessageProducer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("message")
@Tag(name = "Управление сообщениями")
public class MessageController {
    private final MessageProducer messageProducer;

    public MessageController(MessageProducer messageProducer) {
        this.messageProducer = messageProducer;
    }

    @PostMapping(value = "/send-message")
    @Operation(summary = "Отправить сообщение")
    public ResponseEntity<?> sendMessage(@RequestBody MessageDto messageDto) {
        messageProducer.sendMessage(messageDto);
        return ResponseEntity.ok("Сообщение принято к обработке");
    }
}
