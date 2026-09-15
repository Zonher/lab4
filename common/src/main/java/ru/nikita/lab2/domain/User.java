package ru.nikita.lab2.domain;

import ru.nikita.lab2.domain.enumeration.Gender;
import ru.nikita.lab2.domain.enumeration.HairColor;

import java.util.UUID;

public record User(
        UUID id, String login, String name, int age, Gender gender, HairColor hairColor) {}
