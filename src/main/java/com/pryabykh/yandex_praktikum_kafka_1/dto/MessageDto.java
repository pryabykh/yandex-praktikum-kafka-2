package com.pryabykh.yandex_praktikum_kafka_1.dto;

import com.pryabykh.yandex_praktikum_kafka_1.constant.FixedUsers;

public class MessageDto {

    private FixedUsers.User from;

    private FixedUsers.User to;

    private String content;

    public MessageDto() {
    }

    public MessageDto(FixedUsers.User from, FixedUsers.User to, String content) {
        this.from = from;
        this.to = to;
        this.content = content;
    }

    public FixedUsers.User getFrom() {
        return from;
    }

    public void setFrom(FixedUsers.User from) {
        this.from = from;
    }

    public FixedUsers.User getTo() {
        return to;
    }

    public void setTo(FixedUsers.User to) {
        this.to = to;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "MessageDto{" +
                "from=" + from +
                ", to=" + to +
                ", content='" + content + '\'' +
                '}';
    }
}
