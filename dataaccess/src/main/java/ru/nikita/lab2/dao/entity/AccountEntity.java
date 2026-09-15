package ru.nikita.lab2.dao.entity;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

import org.hibernate.annotations.Audited;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@Access(AccessType.FIELD)
@Audited
@Audited.Table(name = "accounts_aud")
@NamedQuery(
        name = AccountEntity.FIND_BY_USER,
        query = "select a from AccountEntity a where a.user = :user order by a.id")
public class AccountEntity {

    public static final String FIND_BY_USER = "AccountEntity.findByUser";

    /** Количество знаков после запятой у денежных сумм (соответствует NUMERIC(10, 2) в БД). */
    public static final int MONEY_SCALE = 2;

    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private UserEntity user;

    @Column(name = "balance", nullable = false, precision = 10, scale = MONEY_SCALE)
    private BigDecimal balance;

    protected AccountEntity() {
        // for JPA only
    }

    public AccountEntity(UserEntity user) {
        this.user = user;
        this.balance = BigDecimal.ZERO.setScale(MONEY_SCALE, RoundingMode.UNNECESSARY);
    }

    public UUID getId() {
        return id;
    }

    public UserEntity getUser() {
        return user;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AccountEntity other)) {
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
        return "AccountEntity{id=" + id + ", balance=" + balance + "}";
    }
}
