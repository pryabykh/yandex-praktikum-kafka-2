package com.pryabykh.yandex_praktikum_kafka_2.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pryabykh.yandex_praktikum_kafka_2.constant.FixedUsers;
import io.swagger.v3.oas.annotations.Hidden;

import java.util.HashSet;
import java.util.Set;

public class MessageDto {

    private FixedUsers.User from;

    private FixedUsers.User to;

    private String content;

    @Hidden
    private Set<String> blockedUsers = new HashSet<>();

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
        return "{" +
                "from=" + from +
                ", to=" + to +
                ", content='" + content + '\'' +
                '}';
    }

    public Set<String> getBlockedUsers() {
        return blockedUsers;
    }

    public void setBlockedUsers(Set<String> blockedUsers) {
        this.blockedUsers = blockedUsers;
    }
}
