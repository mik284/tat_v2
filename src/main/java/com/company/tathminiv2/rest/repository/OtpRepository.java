package com.company.tathminiv2.rest.repository;

import com.company.tathminiv2.entity.OTP;
import io.jmix.core.repository.JmixDataRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OtpRepository extends JmixDataRepository<OTP, UUID> {

}