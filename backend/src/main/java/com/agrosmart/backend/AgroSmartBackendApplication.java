package com.agrosmart.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AgroSmartBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(AgroSmartBackendApplication.class, args);
        System.out.println("=================================================");
        System.out.println("  AGROSMART REST BACKEND IS RUNNING ON PORT 8080 ");
        System.out.println("=================================================");
    }
}
