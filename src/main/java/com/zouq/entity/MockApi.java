package com.zouq.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="mock_api",uniqueConstraints = {@UniqueConstraint(columnNames = {"service_id","api_path","method"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MockApi {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) //多对一
    @JoinColumn(name = "service_id",nullable = false) //外键列
    private MockService service;

    @Column(name="api_path",nullable = false,length = 255)
    private String apiPath;

    @Column(name = "method", nullable = false,length = 10)
    private String method = "POST";

    @Column(name = "response_body" , columnDefinition = "TEXT")
    private  String responseBody;

    @Column(name = "status_code")
    private Integer statusCode = 200;

    @Column(name = "delay_ms")
    private Integer delayMs = 0;

    @Column(name = "is_mock_enable", columnDefinition = "boolean default true")
    private Boolean isMockEnable = true;

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
