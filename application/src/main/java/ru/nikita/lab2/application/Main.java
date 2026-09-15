package ru.nikita.lab2.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ru.nikita.lab2")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
