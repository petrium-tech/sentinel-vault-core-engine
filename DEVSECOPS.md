# 🛡️ Sentinel Vault — DevSecOps Architecture & CI/CD Pipeline

> **Automated Shift-Left Security & Application Protection Infrastructure** > *Infraestrutura Automatizada de Segurança Shift-Left e Proteção de Aplicações*

---

## 🌐 Language / Idioma
* [English Version](#-english-documentation)
* [Versão em Português](#-documentação-em-português)

---

# 🇺🇸 English Documentation

## 📐 Pipeline Architecture Flow

The **Sentinel Vault Core Engine** implements an uncompromised Shift-Left security pipeline using GitHub Actions. Every code push or pull request triggers an automated quality gate that prevents insecure code, leaked credentials, or vulnerable dependencies from reaching production.

```text
[ Git Push / Pull Request ]
            │
            ▼
 1. Checkout Source Code
            │
            ▼
 2. Setup Java 21 JDK (Temurin / Maven Cache)
            │
            ▼
 3. Maven Compile Application (`mvn clean compile`)
            │
            ▼
 4. Run Automated Unit Tests (`mvn test`)
            │
            ▼
 5. SAST - Static Code Analysis (Semgrep via Docker)
            │
            ▼
 6. SCA - Dependency Scanning (Snyk)
            │
            ▼
 7. Secret Scanning (TruffleHog)
            │
            ▼
 8. Multi-Stage Docker Image Build
            │
            ▼
 9. Container Vulnerability Scan (Trivy)
```

---

## 🔐 Zero Hardcoded Secrets Policy

To strictly follow security standards, no operational credentials, JWT secrets, or encryption keys are hardcoded in the source code or default configuration files (`application.yaml`).

### 1. Local Environment Setup
Developers must create a local `.env` file based on the template:
```bash
cp .env.example .env
```
* **Security Rule:** `.env` is explicitly listed in `.gitignore` and must **NEVER** be committed to the repository.

### 2. GitHub Actions Secrets
The pipeline dynamically injects runtime credentials using GitHub Repository Secrets. Configure the following variables under **Settings ➔ Secrets and variables ➔ Actions**:

| Secret Name | Purpose | Example / Format |
| :--- | :--- | :--- |
| `SENTINEL_JWT_SECRET` | 256-bit secret key for signing OAuth2/JWT tokens | Base64 String |
| `SNYK_TOKEN` | Authentication token for Snyk SCA Scanner | API Token |

---

## 📊 Security Tooling Breakdown

| Stage | Tool | Type | Description |
| :--- | :--- | :--- | :--- |
| **SAST** | **Semgrep** | Static Analysis | Scans Java code for OWASP Top 10 vulnerabilities, injection flaws, and unsafe cryptographic usage. |
| **SCA** | **Snyk** | Dependency Audit | Scans `pom.xml` dependency trees to detect and block known High/Critical CVEs. |
| **Secrets** | **TruffleHog** | Leak Prevention | Analyzes commit history to detect accidental plain-text API key or private key exposure. |
| **Container**| **Trivy** | Image Audit | Audits compiled Alpine Docker layers for OS-level and system package vulnerabilities. |

---

# 🇧🇷 Documentação em Português

## 📐 Fluxo da Arquitetura do Pipeline

O **Sentinel Vault Core Engine** implementa uma esteira de segurança *Shift-Left* usando GitHub Actions. Cada push de código ou Pull Request dispara um portão de qualidade automatizado que impede que código inseguro, credenciais vazadas ou dependências vulneráveis cheguem ao ambiente de produção.

```text
[ Git Push / Pull Request ]
            │
            ▼
 1. Checkout do Código-Fonte
            │
            ▼
 2. Configuração do Java 21 JDK (Temurin / Cache Maven)
            │
            ▼
 3. Compilação da Aplicação (`mvn clean compile`)
            │
            ▼
 4. Execução de Testes Unitários (`mvn test`)
            │
            ▼
 5. SAST - Análise Estática de Código (Semgrep via Docker)
            │
            ▼
 6. SCA - Varredura de Dependências (Snyk)
            │
            ▼
 7. Secret Scanning (TruffleHog)
            │
            ▼
 8. Build da Imagem Docker Multi-Stage
            │
            ▼
 9. Varredura de Vulnerabilidades do Contêiner (Trivy)
```

---

## 🔐 Política de Zero Segredos Hardcoded

Para seguir padrões rigorosos de segurança, nenhuma credencial operacional, segredo JWT ou chave criptográfica é armazenada em texto puro no código-fonte ou em arquivos de configuração padrão (`application.yaml`).

### 1. Configuração de Ambiente Local
Desenvolvedores devem criar um arquivo `.env` local baseado no modelo oficial:
```bash
cp .env.example .env
```
* **Regra de Segurança:** O arquivo `.env` está explicitamente listado no `.gitignore` e **NUNCA** deve ser enviado ao repositório.

### 2. Segredos no GitHub Actions
A esteira injeta dinamicamente as credenciais em tempo de execução usando o GitHub Repository Secrets. Configure as seguintes variáveis em **Settings ➔ Secrets and variables ➔ Actions**:

| Nome do Secret | Finalidade | Exemplo / Formato |
| :--- | :--- | :--- |
| `SENTINEL_JWT_SECRET` | Chave secreta de 256 bits para assinatura de tokens JWT | String Base64 |
| `SNYK_TOKEN` | Token de autenticação para o scanner SCA do Snyk | Token de API |

---

## 📊 Detalhamento das Ferramentas de Segurança

| Estágio | Ferramenta | Tipo | Descrição |
| :--- | :--- | :--- | :--- |
| **SAST** | **Semgrep** | Análise Estática | Varre o código Java procurando falhas do OWASP Top 10, injeções e criptografia fraca. |
| **SCA** | **Snyk** | Auditoria de Dependências | Escaneia a árvore do `pom.xml` para detectar e bloquear CVEs Altas/Críticas conhecidas. |
| **Secrets** | **TruffleHog** | Prevenção de Vazamento | Analisa o histórico de commits para detectar exposição acidental de chaves de API e segredos. |
| **Contêiner**| **Trivy** | Auditoria de Imagem | Audita as camadas da imagem Docker Alpine procurando vulnerabilidades no SO e pacotes. |