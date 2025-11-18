package com.pryabykh.yandex_praktikum_kafka_2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication
public class YandexPraktikumKafka2Application {

	public static void main(String[] args) {
		SpringApplication.run(YandexPraktikumKafka2Application.class, args);
	}

}
