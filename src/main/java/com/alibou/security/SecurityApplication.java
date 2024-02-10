package com.alibou.security;

import com.alibou.security.auth.AuthenticationService;
import com.alibou.security.auth.RegisterRequest;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import static com.alibou.security.user.Role.ADMIN;
import static com.alibou.security.user.Role.MANAGER;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class SecurityApplication {

	public static void main(String[] args) {

		SpringApplication.run(SecurityApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(AuthenticationService service) {

		return args -> {
			var admin = RegisterRequest.builder()
					.firstname("Tom")
					.lastname("Robinson")
					.email("admin@mail.com")
					.password("password")
					.role(ADMIN)
					.build();
			String adminToken = service.register(admin).getAccessToken();
			System.out.println("Admin token: " + adminToken);

			var manager = RegisterRequest.builder()
					.firstname("Mary")
					.lastname("Synthie")
					.email("manager@mail.com")
					.password("password")
					.role(MANAGER)
					.build();
			String managerToken = service.register(manager).getAccessToken();
			System.out.println("Manager token: " + managerToken);

		};
	}
}
