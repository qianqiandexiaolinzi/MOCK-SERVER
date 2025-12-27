package com.zouq.repository;
import com.zouq.entity.MockService;
import com.zouq.entity.MockApi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import  java.util.Optional;

@Repository
public interface MockApiRepository extends JpaRepository<MockApi, Long> {
    List<MockApi> findByServiceId(Long serviceId);
    Optional<MockApi> findByServiceAndApiPathAndMethod(MockService service, String apiPath, String method);
}
