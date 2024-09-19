package com.company.tathminiv2.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.OffsetDateTime;
import java.util.UUID;

@JmixEntity
@Table(name = "OTP", indexes = {
        @Index(name = "IDX_OTP_TATHMINI_USER", columnList = "TATHMINI_USER_ID")
})
@Entity
public class OTP {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @Column(name = "OTP_CODE")
    private Integer otpCode;

    @NotNull
    @Column(name = "EXPIRITATION_TIME")
    private OffsetDateTime expiritationTime;

    @Column(name = "PURPOSE", length = 100)
    private String purpose;

    @Column(name = "OTP_USED")
    private Boolean otpUsed;

    @JoinColumn(name = "TATHMINI_USER_ID")
    @OneToOne(fetch = FetchType.LAZY)
    private TathminiUser tathminiUser;


    @LastModifiedBy
    @Column(name = "LAST_MODIFIED_BY")
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "LAST_MODIFIED_DATE")
    private OffsetDateTime lastModifiedDate;

    @CreatedBy
    @Column(name = "CREATED_BY")
    private String createdBy;

    @CreatedDate
    @Column(name = "CREATED_DATE")
    private OffsetDateTime createdDate;

    public void setCreatedDate(OffsetDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public OffsetDateTime getCreatedDate() {
        return createdDate;
    }

    public void setExpiritationTime(OffsetDateTime expiritationTime) {
        this.expiritationTime = expiritationTime;
    }

    public OffsetDateTime getExpiritationTime() {
        return expiritationTime;
    }

    public TathminiUser getTathminiUser() {
        return tathminiUser;
    }

    public void setTathminiUser(TathminiUser tathminiUser) {
        this.tathminiUser = tathminiUser;
    }

    public Boolean getOtpUsed() {
        return otpUsed;
    }

    public void setOtpUsed(Boolean otpUsed) {
        this.otpUsed = otpUsed;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public Integer getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(Integer otpCode) {
        this.otpCode = otpCode;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public OffsetDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(OffsetDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}