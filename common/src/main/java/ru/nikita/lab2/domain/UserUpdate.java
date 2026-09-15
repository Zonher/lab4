package ru.nikita.lab2.domain;

import ru.nikita.lab2.domain.enumeration.Gender;
import ru.nikita.lab2.domain.enumeration.HairColor;

import java.util.UUID;

public record UserUpdate(
        UUID id,
        FieldPatch<String> name,
        FieldPatch<Integer> age,
        FieldPatch<Gender> gender,
        FieldPatch<HairColor> hairColor) {}
