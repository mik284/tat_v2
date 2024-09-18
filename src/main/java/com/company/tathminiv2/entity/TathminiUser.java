package com.company.tathminiv2.entity;

import io.jmix.core.annotation.DeletedBy;
import io.jmix.core.annotation.DeletedDate;
import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.UUID;
@JmixEntity
@Table(name = "TATHMINI_USER")
@Entity
public class TathminiUser {


    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @Column(name = "USERNAME", nullable = false, length = 50)
    @NotNull
    private String username;

    @InstanceName
    @Column(name = "FIRST_NAME", nullable = false, length = 50)
    @NotNull
    private String firstName;

    @Column(name = "LAST_NAME", nullable = false, length = 50)
    @NotNull
    private String lastName;

    @Column(name = "DOB", nullable = false)
    @NotNull
    private Date dob;

    @Column(name = "PHONE_NUMBER", nullable = false, length = 15)
    @NotNull
    private String phoneNumber;

    @Column(name = "PASSWORD", nullable = false, length = 50)
    @NotNull
    private String password;

    @Column(name = "RISK_PROFILE", length = 50)
    private String riskProfile;

    @Column(name = "INVESTOR_STATUS", length = 50)
    private String investorStatus;

    @Email
    @Column(name = "EMAIL", nullable = false, length = 50)
    @NotNull
    private String email;

    @DeletedBy
    @Column(name = "DELETED_BY")
    private String deletedBy;

    @DeletedDate
    @Column(name = "DELETED_DATE")
    private OffsetDateTime deletedDate;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public InvestorStatusEnum getInvestorStatus() {
        return investorStatus == null ? null : InvestorStatusEnum.fromId(investorStatus);
    }

    public void setInvestorStatus(InvestorStatusEnum investorStatus) {
        this.investorStatus = investorStatus == null ? null : investorStatus.getId();
    }

    public RiskProfileEnum getRiskProfile() {
        return riskProfile == null ? null : RiskProfileEnum.fromId(riskProfile);
    }

    public void setRiskProfile(RiskProfileEnum riskProfile) {
        this.riskProfile = riskProfile == null ? null : riskProfile.getId();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public OffsetDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(OffsetDateTime createdDate) {
        this.createdDate = createdDate;
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

    public OffsetDateTime getDeletedDate() {
        return deletedDate;
    }

    public void setDeletedDate(OffsetDateTime deletedDate) {
        this.deletedDate = deletedDate;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}