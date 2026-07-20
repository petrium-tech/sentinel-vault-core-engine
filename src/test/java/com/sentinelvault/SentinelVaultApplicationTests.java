package com.sentinelvault;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:testdb",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.flyway.enabled=false",
		"spring.jpa.hibernate.ddl-auto=create-drop"
})
class SentinelVaultApplicationTests {

	@Test
	void contextLoads() {
		// Valida se o esqueleto do ecossistema compila sem dependências de banco físico
	}
}