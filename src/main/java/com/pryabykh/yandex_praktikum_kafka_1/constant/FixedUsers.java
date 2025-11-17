package com.pryabykh.yandex_praktikum_kafka_1.constant;

import java.util.List;

import static com.pryabykh.yandex_praktikum_kafka_1.constant.FixedUsers.User.USER_1;
import static com.pryabykh.yandex_praktikum_kafka_1.constant.FixedUsers.User.USER_2;
import static com.pryabykh.yandex_praktikum_kafka_1.constant.FixedUsers.User.USER_3;
import static com.pryabykh.yandex_praktikum_kafka_1.constant.FixedUsers.User.USER_4;
import static com.pryabykh.yandex_praktikum_kafka_1.constant.FixedUsers.User.USER_5;

public class FixedUsers {

    public static final List<User> list = List.of(USER_1, USER_2, USER_3, USER_4, USER_5);


    public static enum User {
        USER_1, USER_2, USER_3, USER_4, USER_5
    }
}
