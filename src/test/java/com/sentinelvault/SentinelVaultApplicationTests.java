package com.sentinelvault;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:testdb",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.flyway.enabled=false"
})
class SentinelVaultApplicationTests {

	@Test
	void contextLoads() {
		// Teste de fumaça padrão apenas para validar se o esqueleto do ecossistema compila
	}
}