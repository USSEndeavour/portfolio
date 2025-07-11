package com.crypto.portfolio.entities;

import com.crypto.portfolio.utils.cashofficeoperation.OperationStatus;
import com.crypto.portfolio.utils.cashofficeoperation.OperationType;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "CASH_OFFICE_OPERATIONS")
public class CashOfficeOperation {
    @Id
    @GeneratedValue
    @Column(name = "cash_operation_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cash_office_id", foreignKey = @ForeignKey(name = "cash_office_id_fk"))
    private CashOffice cashOffice;

    @Enumerated(EnumType.ORDINAL)
    private OperationType operationType;

    // a value, provided by the cashier
    @Column(name = "operation_passcode", unique = true)
    @JsonProperty("operation_passcode")
    private String operationPasscode;

    @ManyToOne
    @JoinColumn(name = "holder_id", foreignKey = @ForeignKey(name = "client_id_fk"))
    private User client;

    @ManyToOne
    @JoinColumn(name = "currency_id", foreignKey = @ForeignKey(name = "currency_id_fk"))
    private Currency currency;

    // the amount of money to cash in/out
    @Column(name = "operation_quantity")
    private BigDecimal operationQuantity;

    public Long getRequestMessageId() {
        return requestMessageId;
    }

    @Enumerated(EnumType.STRING)
    private OperationStatus operationStatus;

    @CreationTimestamp
    private LocalDateTime creationTime;

    @Column(name = "request_message_id")
    private Long requestMessageId;

    @JsonProperty("request_group_id")
    @Column(name = "request_group_id")
    private Long requestMessageGroupId;

    @JsonProperty("sender_id")
    @Column(name = "sender_id")
    private Long requestSenderTelegramId;

    private LocalDateTime completionTime;

    public Long getRequestSenderTelegramId() {
        return requestSenderTelegramId;
    }

    public void setRequestSenderTelegramId(Long requestSenderTelegramId) {
        this.requestSenderTelegramId = requestSenderTelegramId;
    }

    public Long getRequestMessageGroupId() {
        return requestMessageGroupId;
    }

    public void setRequestMessageGroupId(Long requestMessageGroupId) {
        this.requestMessageGroupId = requestMessageGroupId;
    }

    public void setRequestMessageId(Long requestMessageId) {
        this.requestMessageId = requestMessageId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CashOffice getCashOffice() {
        return cashOffice;
    }

    public void setCashOffice(CashOffice cashOffice) {
        this.cashOffice = cashOffice;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public String getOperationPasscode() {
        return operationPasscode;
    }

    public void setOperationPasscode(String operationPasscode) {
        this.operationPasscode = operationPasscode;
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getOperationQuantity() {
        return operationQuantity;
    }

    public void setOperationQuantity(BigDecimal operationQuantity) {
        this.operationQuantity = operationQuantity;
    }

    public OperationStatus getOperationStatus() {
        return operationStatus;
    }

    public void setOperationStatus(OperationStatus operationStatus) {
        this.operationStatus = operationStatus;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public LocalDateTime getCompletionTime() {
        return completionTime;
    }

    public void setCompletionTime(LocalDateTime completionTime) {
        this.completionTime = completionTime;
    }
}
