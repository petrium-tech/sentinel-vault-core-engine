# Jornada de Engenharia e Decisões Arquiteturais

Este documento registra os desafios de engenharia reais, ciclos de refatoração e as escolhas de arquitetura solucionadas durante o desenvolvimento do Sentinel Vault Core Engine.

## 1. Estratégia de Git e Evolução do Ecossistema
O Sentinel Vault foi projetado sob um padrão de arquitetura evolutiva. A branch `main` não contém uma interface acoplada para o usuário final; em vez disso, ela serve como um **Core Engine** puro e hiper-otimizado. A partir desta base estável e conteinerizada, três verticais especializadas de produtos são ramificadas:
* **Vertical A (Injetor Dev):** Injeção automatizada de `.env` em tempo de execução local.
* **Vertical B (Guardião de Infra):** Gerenciamento seguro de metadados de Chaves SSH e Certificados SSL/TLS.
* **Vertical C (Plataforma de Compliance):** Telemetria de rede estendida e auditoria imutável de alta granularidade.

## 2. Resolvendo a Dependência Circular do Spring Security
Durante a fase de blindagem do Authorization Server, ocorreu um loop clássico de referência circular entre o `SecurityConfig` e o `UserService` devido ao requisito bidirecional do `PasswordEncoder`.
* **A Solução:** Aplicou-se o princípio de *Separation of Concerns (SoC)*, isolando o motor de codificação criptográfica em uma classe especialista dedicada chamada `PasswordEncoderConfig`. Isso quebrou o loop de instanciação, permitindo que o Spring resolvesse o gráfico de dependências de forma limpa no boot.

## 3. Unificação de Rede e Arquitetura Docker
Inicialmente, o ambiente local rodava o banco conteinerizado enquanto a aplicação executava nativamente no sistema operacional hospedeiro, forçando mapeamentos de portas manuais (`5439:5432`) e exposições externas desnecessárias.
* **A Solução:** Projetou-se um `docker-compose.yml` unificado operando em uma rede bridge interna isolada. A aplicação agora descobre o banco de dados nativamente via o alias do serviço (`postgres`) na porta interna padrão `5432`. Isso preserva o isolamento de rede, reduz o overhead de sockets e reforça práticas de *Mínimo Privilégio*.