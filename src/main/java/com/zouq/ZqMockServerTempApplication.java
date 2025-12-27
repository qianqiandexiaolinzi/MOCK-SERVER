package com.zouq;

import com.zouq.repository.MockServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ZqMockServerTempApplication implements CommandLineRunner {

    @Autowired
    private MockServiceRepository repository;

    public static void main(String[] args) {
        SpringApplication.run(ZqMockServerTempApplication.class, args);
    }

    @Override
    public void run(final String... args) throws Exception {
        System.out.println(111);
    }
}
