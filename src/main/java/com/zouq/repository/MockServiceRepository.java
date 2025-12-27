package com.zouq.repository;

import com.zouq.entity.MockService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MockServiceRepository extends  JpaRepository<MockService, Long> {
    Optional<MockService> findByServiceNameIgnoreCase(String serviceName);
}
