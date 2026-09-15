package ru.nikita.lab2.dao;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan("ru.nikita.lab2.dao.entity")
@EnableJpaRepositories("ru.nikita.lab2.dao.repository")
public class PersistenceConfiguration {}
