package com.company.tathminiv2.rest.repository;

import com.company.tathminiv2.entity.TathminiUser;
import io.jmix.core.repository.JmixDataRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TathminiUserRepository extends JmixDataRepository<TathminiUser, UUID> {
    @Override
    Page<TathminiUser> findAll(Pageable pageable);
}