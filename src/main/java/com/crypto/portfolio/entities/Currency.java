package com.crypto.portfolio.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

import com.crypto.portfolio.utils.currencies.CurrencyTicker;

@Entity
@Table(name="CURRENCIES")
public class Currency {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(unique = true)
    @Enumerated(EnumType.STRING)
    private CurrencyTicker ticker;

    @CreationTimestamp
    private LocalDateTime creationTime;

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public Integer getId() {
        return id;
    }

    public void setTicker(CurrencyTicker ticker) {
        this.ticker = ticker;
    }

    public CurrencyTicker getTicker() {
        return ticker;
    }
}
