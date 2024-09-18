package com.company.tathminiv2.rest.dto;

import com.company.tathminiv2.entity.InvestorStatusEnum;
import com.company.tathminiv2.entity.RiskProfileEnum;

import java.util.Date;
import java.util.UUID;

public class UserResponseDto {
    private UUID id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Date dob;
    private String phoneNumber;
    private RiskProfileEnum riskProfile;
    private InvestorStatusEnum investorStatus;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public RiskProfileEnum getRiskProfile() {
        return riskProfile;
    }

    public void setRiskProfile(RiskProfileEnum riskProfile) {
        this.riskProfile = riskProfile;
    }

    public InvestorStatusEnum getInvestorStatus() {
        return investorStatus;
    }

    public void setInvestorStatus(InvestorStatusEnum investorStatus) {
        this.investorStatus = investorStatus;
    }
}