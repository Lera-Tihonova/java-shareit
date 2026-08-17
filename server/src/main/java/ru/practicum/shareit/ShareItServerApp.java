package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShareItServerApp {
    public static void main(String[] args) {
        System.setProperty("server.port", "9090");

        SpringApplication.run(ShareItServerApp.class, args);
    }
}