package ru.nikita.lab2.application.dto;

import com.fasterxml.jackson.annotation.JsonSetter;

import io.swagger.v3.oas.annotations.media.Schema;

import ru.nikita.lab2.application.dto.enumeration.*;
import ru.nikita.lab2.domain.FieldPatch;

/** Setter вызывается только для присутствующего в JSON поля, в том числе при null. */
public class UserPatchRequest {
    private FieldPatch<String> name = FieldPatch.absent();
    private FieldPatch<Integer> age = FieldPatch.absent();
    private FieldPatch<Gender> gender = FieldPatch.absent();
    private FieldPatch<HairColor> hairColor = FieldPatch.absent();

    @Schema(
            description = "Новое имя; пропуск сохраняет значение, null запрещён",
            example = "Nikita")
    @JsonSetter("name")
    public void setName(String value) {
        name = FieldPatch.of(value);
    }

    @Schema(description = "Новый положительный возраст; null запрещён", example = "21")
    @JsonSetter("age")
    public void setAge(Integer value) {
        age = FieldPatch.of(value);
    }

    @Schema(description = "Новый пол; null очищает значение")
    @JsonSetter("gender")
    public void setGender(Gender value) {
        gender = FieldPatch.of(value);
    }

    @Schema(description = "Новый цвет волос; null очищает значение")
    @JsonSetter("hairColor")
    public void setHairColor(HairColor value) {
        hairColor = FieldPatch.of(value);
    }

    public FieldPatch<String> name() {
        return name;
    }

    public FieldPatch<Integer> age() {
        return age;
    }

    public FieldPatch<Gender> gender() {
        return gender;
    }

    public FieldPatch<HairColor> hairColor() {
        return hairColor;
    }
}
