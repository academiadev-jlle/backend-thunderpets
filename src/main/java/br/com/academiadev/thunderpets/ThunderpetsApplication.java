package br.com.academiadev.thunderpets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@SpringBootApplication
public class ThunderpetsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThunderpetsApplication.class, args);
    }
}
