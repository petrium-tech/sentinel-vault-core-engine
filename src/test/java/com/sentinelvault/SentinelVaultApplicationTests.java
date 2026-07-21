package com.sentinelvault;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:testdb",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.flyway.enabled=false",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"sentinel.security.crypto-key=YTM0N2M5ZjgxYjIyYzM0ZDVkNmU3YThmOTBlYTIxY2Q=",
		"sentinel.security.jwt.secret=dGhpcy1pcy1hLXN1cGVyLXNlY3VyZS1oYXNoLWZvci1jaS10ZXN0aW5nLXB1cnBvc2VzLTI1Ni1iaXRz"
})
class SentinelVaultApplicationTests {

	@Test
	void contextLoads() {
		// Valida a compilação e carregamento do contexto do Spring sem expor segredos reais
	}
}