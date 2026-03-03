package com.project.Multi_Tenant_SaaS_Backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MultiTenantSaaSBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MultiTenantSaaSBackendApplication.class, args);
	}

}
