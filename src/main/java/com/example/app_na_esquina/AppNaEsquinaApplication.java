package com.example.app_na_esquina;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync       // envio de e-mail em segundo plano
@EnableScheduling  // limpeza agendada de contas nao confirmadas
public class AppNaEsquinaApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppNaEsquinaApplication.class, args);
	}

}