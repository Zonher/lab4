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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import org.hibernate.annotations.Audited;
import org.hibernate.annotations.UuidGenerator;

import ru.nikita.lab2.dao.entity.enumeration.OpType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Операция, изменившая баланс счёта.
 *
 * <p>Перевод хранится одной записью: {@code account} — счёт списания, {@code destination} — счёт
 * зачисления. Для пополнения и снятия {@code destination} равен {@code null}. {@code amount} всегда
 * положительна; {@code commission} удерживается со счёта {@code account} сверх суммы операции.
 */
@Entity
@Table(name = "operations")
@Access(AccessType.FIELD)
@Audited
@Audited.Table(name = "operations_aud")
public class OperationEntity {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false, updatable = false)
    private AccountEntity account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_id", updatable = false)
    private AccountEntity destination;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false, updatable = false)
    private OpType opType;

    @Column(
            name = "amount",
            nullable = false,
            updatable = false,
            precision = 10,
            scale = AccountEntity.MONEY_SCALE)
    private BigDecimal amount;

    @Column(
            name = "commission",
            nullable = false,
            updatable = false,
            precision = 10,
            scale = AccountEntity.MONEY_SCALE)
    private BigDecimal commission;

    @Column(name = "operation_at", nullable = false, updatable = false)
    private Instant operationAt;

    protected OperationEntity() {
        // for JPA only
    }

    private OperationEntity(
            AccountEntity account,
            AccountEntity destination,
            OpType opType,
            BigDecimal amount,
            BigDecimal commission) {
        this.account = account;
        this.destination = destination;
        this.opType = opType;
        this.amount = amount;
        this.commission = commission;
        this.operationAt = Instant.now();
    }

    public static OperationEntity deposit(AccountEntity account, BigDecimal amount) {
        return new OperationEntity(account, null, OpType.DEPOSIT, amount, BigDecimal.ZERO);
    }

    public static OperationEntity withdraw(AccountEntity account, BigDecimal amount) {
        return new OperationEntity(account, null, OpType.WITHDRAW, amount, BigDecimal.ZERO);
    }

    public static OperationEntity transfer(
            AccountEntity source,
            AccountEntity destination,
            BigDecimal amount,
            BigDecimal commission) {
        return new OperationEntity(source, destination, OpType.TRANSFER, amount, commission);
    }

    public UUID getId() {
        return id;
    }

    public AccountEntity getAccount() {
        return account;
    }

    public AccountEntity getDestination() {
        return destination;
    }

    public OpType getOpType() {
        return opType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public Instant getOperationAt() {
        return operationAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OperationEntity other)) {
            return false;
        }
        return getId() != null && getId().equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
