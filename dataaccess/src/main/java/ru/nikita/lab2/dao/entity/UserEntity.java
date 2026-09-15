package ru.nikita.lab2.dao.entity;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import org.hibernate.annotations.Audited;
import org.hibernate.annotations.UuidGenerator;

import ru.nikita.lab2.dao.entity.enumeration.Gender;
import ru.nikita.lab2.dao.entity.enumeration.HairColor;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "users")
@Access(AccessType.FIELD)
@Audited
@Audited.Table(name = "users_aud")
@NamedQuery(
        name = UserEntity.FIND_BY_LOGIN,
        query = "select u from UserEntity u where u.login = :login")
public class UserEntity {

    public static final String FIND_BY_LOGIN = "UserEntity.findByLogin";

    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private UUID id;

    @Column(name = "login", nullable = false, updatable = false, unique = true, length = 20)
    private String login;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "age", nullable = false)
    private int age;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "hair_color")
    private HairColor hairColor;

    /**
     * Друзья пользователя. Связь направленная: {@code A} добавил {@code B} в друзья не означает,
     * что {@code A} есть в друзьях у {@code B}. Изменения списка друзей не аудируются.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_friends",
            joinColumns = @JoinColumn(name = "owner_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id"))
    @Audited.Excluded
    private Set<UserEntity> friends = new HashSet<>();

    protected UserEntity() {
        // for JPA only
    }

    public UserEntity(String login, String name, int age, Gender gender, HairColor hairColor) {
        this.login = login;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.hairColor = hairColor;
    }

    public UUID getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public HairColor getHairColor() {
        return hairColor;
    }

    public void setHairColor(HairColor hairColor) {
        this.hairColor = hairColor;
    }

    /**
     * @return неизменяемое представление списка друзей
     */
    public Set<UserEntity> getFriends() {
        return Collections.unmodifiableSet(friends);
    }

    public boolean hasFriend(UserEntity user) {
        return friends.contains(user);
    }

    public boolean addFriend(UserEntity friend) {
        return friends.add(friend);
    }

    public boolean removeFriend(UserEntity friend) {
        return friends.remove(friend);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserEntity other)) {
            return false;
        }
        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "UserEntity{id=" + id + ", login='" + login + "'}";
    }
}
