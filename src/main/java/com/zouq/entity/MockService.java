package com.zouq.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/*对应数据库mock_service表的实体类*/
@Entity
@Table(name="mock_service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MockService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //自增主键
    private Long id;

    @Column(name="service_name", nullable = false,unique = true,length = 100)
    private String serviceName;

    @Column(name="real_url", length = 255)
    private String realUrl;

    @Column(name="enabled", columnDefinition = "boolean default true")
    private boolean enabled =true;

    @Column(name="create_date", updatable = false)
    private LocalDateTime createDate;

    @Column(name="update_date")
    private LocalDateTime updateDate;

    //自动填充创建时间和更新时间
    @PrePersist
    protected  void onCreate() {
        this.createDate = LocalDateTime.now();
        this.updateDate = LocalDateTime.now();
    }
    @PreUpdate
    protected void onUpdate() {
        this.updateDate = LocalDateTime.now();
    }

}
