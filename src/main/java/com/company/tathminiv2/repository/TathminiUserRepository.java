package com.company.tathminiv2.repository;

import com.company.tathminiv2.entity.TathminiUser;
import io.jmix.core.repository.JmixDataRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface TathminiUserRepository extends JmixDataRepository<TathminiUser, UUID> {
}