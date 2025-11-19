package com.pryabykh.yandex_praktikum_kafka_2.controller;

import com.pryabykh.yandex_praktikum_kafka_2.constant.FixedUsers;
import com.pryabykh.yandex_praktikum_kafka_2.dto.MessageDto;
import com.pryabykh.yandex_praktikum_kafka_2.producer.BlockedUsersProducer;
import com.pryabykh.yandex_praktikum_kafka_2.producer.MessageProducer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("message")
@Tag(name = "Управление сообщениями")
public class MessageController {
    private final MessageProducer messageProducer;
    private final BlockedUsersProducer blockedUsersProducer;

    public MessageController(MessageProducer messageProducer, BlockedUsersProducer blockedUsersProducer) {
        this.messageProducer = messageProducer;
        this.blockedUsersProducer = blockedUsersProducer;
    }

    @PostMapping(value = "/send-message")
    @Operation(summary = "Отправить сообщение")
    public ResponseEntity<?> sendMessage(@RequestParam FixedUsers.User from, @RequestParam FixedUsers.User to, String content) {
        MessageDto messageDto = new MessageDto();
        messageDto.setFrom(from);
        messageDto.setTo(to);
        messageDto.setContent(content);
        messageProducer.sendMessage(messageDto);
        return ResponseEntity.ok("Сообщение принято к обработке");
    }

    @PostMapping(value = "/block-user")
    @Operation(summary = "Заблокировать пользователя")
    public ResponseEntity<?> sendMessage(@RequestParam FixedUsers.User blocker, @RequestParam FixedUsers.User blocked) {
        blockedUsersProducer.blockUser(blocker.name(), blocked.name());
        return ResponseEntity.ok("Сообщение принято к обработке");
    }
}
