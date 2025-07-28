package com.crypto.portfolio.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

@Entity
@Table(name = "CASH_OFFICES")
public class CashOffice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @JsonProperty("office_location")
    @Column(unique = true)
    private String officeLocation;

    @CreationTimestamp
    private LocalDateTime creationTime;

    private String comment;

    public String getOfficeLocation() throws Exception {
        return officeLocation;
    }

    public void setOfficeLocation(String officeLocation) {
        this.officeLocation = officeLocation;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
