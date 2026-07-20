# Sentinel Vault — Core Engine
> **Stateless Secrets Management & Auditing Platform**  
> _A high-performance security infrastructure engine built with Java 21, Spring Boot 3.5, and DevSecOps Automation._

---

## 🌐 Language / Idioma
Select your preferred language to navigate the documentation:
* [English Version](#-english-documentation)
* [Versão em Português](#-documentação-em-português)

---

# 🇺🇸 English Documentation

## 🎯 About the Project
The **Sentinel Vault Core Engine** is a professional-grade cryptographic infrastructure designed to store, organize, and audit highly sensitive data (such as API keys, SSH keys, and database tokens). Rather than acting as a simple end-user password manager, it operates as a secure, stateless backend API tailored for modern cloud ecosystems, deployment pipelines, and developer tooling.

### Core Architecture Highlights:
* **Stateless Token-Based Security:** Fully powered by OAuth 2.1 protocol with PKCE flow and asynchronous RSA-2048 public/private key verification.
* **Logical Multi-Tenancy & Isolation:** Rigid security constraints tying database lookups directly to the authenticated user's UUID to neutralize Broken Object Level Authorization (BOLA) risks.
* **Immutable Security Auditing:** Captures precise, non-repudiable logs of *who*, *when*, *from which IP*, and *what action* was taken against any secret.
> 💡 **Deep Dive:** For a complete breakdown of the development history, structural refactoring, and corporate design choices, read our full [Engineering Journey & Architectural Decisions Document](docs/engineering-journey.md).

---

## 🔒 Cryptographic Guardrails
Data protection is treated as a first-class citizen using industry-standard algorithms:
* **Data in Repose:** Symmetric Encryption utilizing **AES-256-GCM** with unique initialization vectors (IVs) to protect secret values before database persistence.
* **Password Hashing:** **BCrypt** with adaptive work factors, isolated into dedicated specialist configuration classes.
* **Data in Transit:** Enforced TLS transport layer binding across stateless communication nodes.

---

## 🛡️ DevSecOps Automated Pipeline
This project implements strict **Shift-Left Security** guidelines using GitHub Actions. Every `git push` triggers an uncompromised quality gate:

| Stage | Tooling | Purpose |
| :--- | :--- | :--- |
| **Secret Scanning** | Trufflehog / Gitleaks | Scans the commit history to block accidental plain-text credential leaks. |
| **Verification** | JUnit 5 & Mockito | Automatically executes the complete unit testing suite with 100% success required. |
| **SAST** | Semgrep (OWASP Rules) | Static Application Security Testing looking for injection or weak cryptography. |
| **SCA** | Snyk Dependency Check | Scans the `pom.xml` build matrix to block High/Critical vulnerabilities (CVEs). |

---

## 📊 Observability & Telemetry
Operational instrumentation is fully integrated into the Core JVM runtime:
* **Spring Boot Actuator:** Exposes structural endpoints `/actuator/health` and `/actuator/prometheus` under micro-privileged network policies.
* **Micrometer Registry:** Seamlessly translates internal indicators (such as Memory Heap allocation, active connections, and active thread states) into standardized Prometheus matrix metrics ready for Grafana visualizations.

---

## 🚀 Quick Start (Local Environment)
The local environment is engineered for instant developer onboarding via an optimized **Multi-stage Docker Build**.

### Prerequisites:
* Docker Desktop running.
* Bash / Git Bash terminal.

### Booting up the Vault:
Simply run the specialist development automation script located at the repository root:

```bash
bash reset-dev.sh
```
This command automatically wipes stale volumes, triggers the isolated multi-stage Maven build, instantiates the PostgreSQL database container with strict readiness health checks, and hooks the application logs into your console.

---

# 🇧🇷 Documentação em Português

## 🎯 Sobre o Projeto
O **Sentinel Vault Core Engine** é um motor de infraestrutura criptográfica de nível corporativo projetado para armazenar, organizar e auditar dados altamente sensíveis (como chaves de API, chaves SSH e tokens de banco de dados). Em vez de atuar como um simples gerenciador de senhas para o usuário final, ele opera como uma API REST stateless e segura, customizada para ecossistemas de nuvem, esteiras de deploy e ferramentas de desenvolvimento.

### Destaques Arquiteturais:
* **Segurança Stateless baseada em Tokens:** Alimentado pelo protocolo OAuth 2.1 com fluxo PKCE e verificação assíncrona de chaves públicas/privadas RSA-2048.
* **Isolamento Multi-Tenant Lógico:** Restrições rígidas atrelando consultas ao banco de dados diretamente ao UUID do usuário autenticado para neutralizar riscos de *Broken Object Level Authorization (BOLA)*.
* **Auditoria de Segurança Imutável:** Captura logs precisos e irrefutáveis de *quem*, *quando*, *a partir de qual IP* e *qual ação* foi executada contra qualquer segredo.
> 💡 **Mergulho Profundo:** Para uma análise completa do histórico de desenvolvimento, refatorações estruturais e escolhas corporativas de design, leia nossa [Documentação Completa de Jornada de Engenharia e Decisões Arquiteturais](docs/jornada-engenharia.md).

---

## 🔒 Salvaguardas Criptográficas
A proteção de dados é tratada como prioridade arquitetural utilizando algoritmos consolidados pelo mercado:
* **Dados em Repouso:** Criptografia Simétrica utilizando **AES-256-GCM** com vetores de inicialização (IVs) únicos para proteger os valores antes da persistência no banco de dados.
* **Hashing de Senhas:** **BCrypt** com fator de trabalho adaptativo, isolado em classe de configuração especialista neutra.
* **Dados em Trânsito:** Vinculação obrigatória de camada de transporte TLS entre os nós de comunicação.

---

## 🛡️ Esteira Automatizada DevSecOps
Este projeto implementa diretrizes estritas de **Shift-Left Security** através do GitHub Actions. Cada `git push` dispara um portão de qualidade inegociável:

| Estágio | Ferramenta | Propósito |
| :--- | :--- | :--- |
| **Secret Scanning** | Trufflehog / Gitleaks | Varre o histórico de commits para bloquear vazamentos acidentais de credenciais em texto puro. |
| **Verificação** | JUnit 5 & Mockito | Executa automaticamente a suíte completa de testes unitários exigindo 100% de sucesso. |
| **SAST** | Semgrep (OWASP Rules) | Teste Estático de Segurança de Aplicação procurando por falhas de injeção ou criptografia fraca. |
| **SCA** | Snyk Dependency Check | Escaneia a matriz do `pom.xml` para bloquear dependências com vulnerabilidades Altas ou Críticas (CVEs). |

---

## 📊 Observability & Telemetry
A instrumentação operacional é totalmente integrada ao tempo de execução da JVM Core:
* **Spring Boot Actuator:** Expõe os endpoints estruturais `/actuator/health` e `/actuator/prometheus` sob políticas de privilégio mínimo de rede.
* **Micrometer Registry:** Traduz perfeitamente indicadores internos (como alocação de Memória Heap, conexões com o banco e estados de threads ativas) no padrão do Prometheus pronto para raspagem e exibição no Grafana.

---

## 🚀 Inicialização Rápida (Ambiente Local)
O ambiente local foi projetado para onboarding instantâneo através de um **Build Docker Multi-stage** otimizado.

### Pré-requisitos:
* Docker Desktop ativo e rodando.
* Terminal Bash ou Git Bash.

### Subindo a Fortaleza:
Basta executar o script automatizado de desenvolvimento localizado na raiz do repositório:

```bash
bash reset-dev.sh
```
Este comando destrói automaticamente contêineres e volumes antigos, dispara o build Maven isolado em multi-stage, instancia o contêiner do PostgreSQL com checagem de saúde (*Healthcheck*) e acopla os logs da aplicação diretamente na sua tela.
