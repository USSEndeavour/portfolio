package com.crypto.portfolio.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NaturalId;

import com.crypto.portfolio.utils.user.UserRole;

@Entity
@Table(name="USERS")
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonProperty("user_name")
    @Column(name = "user_name")
    private String name;

    @CreationTimestamp
    @Column(name = "time_created")
    private LocalDateTime creationTime;

    @NaturalId
    @JsonProperty("telegram_id")
    @Column(name = "telegram_id", unique = true)
    private Long telegramId;

    @Column(name = "telegram_user_name", unique = true)
    private String telegramUserName;

    @Column(name = "notes")
    private String comment;

    @NotNull
    @JsonProperty("user_role")
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @NotNull
    @JsonProperty("telegram_group_id")
    @Column(name = "telegram_group_id")
    private Long telegramGroupId;

    public Long getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(Long telegramId) {
        this.telegramId = telegramId;
    }

    public String getTelegramUserName() {
        return telegramUserName;
    }

    public void setTelegramUserName(String telegramUserName) {
        this.telegramUserName = telegramUserName;
    }

    public UserRole getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole;
    }

    public Long getTelegramGroupId() {
        return telegramGroupId;
    }

    public void setTelegramGroupId(Long telegramGroupId) {
        this.telegramGroupId = telegramGroupId;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setComment(String newComment) {
        this.comment = newComment;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }
}
