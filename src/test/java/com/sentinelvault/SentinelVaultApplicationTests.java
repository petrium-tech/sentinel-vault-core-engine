package com.sentinelvault;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class SentinelVaultApplicationTests {

	@Test
	void contextLoads() {
		// Valida a compilação e carregamento completo do contexto Spring Boot sem expor segredos
	}
}
