# DebtMap 💳

Sistema de controle financeiro pessoal com gerenciamento de usuários, transações e dívidas.

## 📋 Sobre o projeto

O DebtMap é uma API REST desenvolvida em Java 17 com Spring Boot, seguindo os princípios de Clean Architecture. O sistema permite que usuários controlem suas finanças pessoais, registrando transações, categorizando gastos e gerenciando dívidas com cálculo automático de juros e parcelas.

## 🏗️ Arquitetura

O projeto segue uma arquitetura em camadas inspirada no Clean Architecture:

    src/main/java/com/debtmap/
    ├── controller/        # Camada de apresentação — endpoints REST
    ├── usecase/           # Camada de aplicação — casos de uso e regras de negócio
    ├── domain/            # Camada de domínio — entidades, enums e interfaces
    ├── infrastructure/    # Camada de infraestrutura — JPA, Security, JWT, Config
    └── shared/            # DTOs, exceptions e respostas padronizadas

## ✅ Funcionalidades

- [x] Setup do projeto — Módulo 1
- [x] Domínio — entidades e repositórios
- [x] Autenticação — registro, login e JWT (access + refresh token)g
- [x] Transações — CRUD de entradas e saídas
- [x] Categorias — gerenciamento de categorias de gastos
- [x] Dívidas — controle de dívidas com parcelas e cálculo de juros
- [ ] Dashboard — resumo financeiro do usuário

## 🛠️ Tecnologias

| Tecnologia | Versão | Uso |
|---|---|---|
| Java | 17 | Linguagem principal |
| Spring Boot | 3.3.4 | Framework base |
| Spring Security | 6 | Autenticação e autorização |
| Spring Data JPA | 3.3.4 | Persistência |
| PostgreSQL | 16 | Banco de dados |
| Flyway | — | Migrations SQL |
| JJWT | 0.12.6 | Geração e validação de tokens JWT |
| MapStruct | 1.6.3 | Mapeamento entre camadas |
| Lombok | 1.18.34 | Redução de boilerplate |
| SpringDoc OpenAPI | 2.5.0 | Documentação Swagger |
| Testcontainers | 1.19.8 | Testes de integração |
| Docker | — | Containerização |

## 🔐 Segurança

- Autenticação via JWT com access token (15 min) e refresh token (7 dias)
- Senhas criptografadas com BCrypt
- Controle de acesso por roles: `USER` e `ADMIN`
- Filtro JWT em todas as rotas protegidas

## 🗄️ Banco de dados

### Tabelas

- `users` — usuários do sistema
- `refresh_tokens` — tokens de refresh ativos
- `categories` — categorias de gastos por usuário
- `transactions` — transações de entrada e saída
- `debts` — dívidas com taxa de juros
- `installments` — parcelas de cada dívida

## 🚀 Como rodar localmente

### Pré-requisitos

- Java 17+
- Maven 3.8+
- Docker e Docker Compose

### Passo a passo

**1. Clone o repositório**

    git clone https://github.com/GabrielVeras-dev/debtmap.git
    cd debtmap

**2. Configure as variáveis de ambiente**

    cp .env.example .env
    # Edite o .env com seus valores

**3. Suba o banco de dados**

    docker compose up -d postgres

**4. Rode a aplicação**

    mvn spring-boot:run

A aplicação estará disponível em `http://localhost:8083`

### Com Docker completo

    docker compose up -d

## 📖 Documentação da API

Com a aplicação rodando, acesse:

- **Swagger UI:** http://localhost:8083/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8083/v3/api-docs

## 🧪 Testes

**Testes unitários**

    mvn test

**Testes de integração (requer Docker)**

    mvn verify

## 📁 Estrutura de pacotes completa

    src/
    ├── main/
    │   ├── java/com/debtmap/
    │   │   ├── DebtmapApplication.java
    │   │   ├── controller/
    │   │   ├── usecase/
    │   │   │   ├── auth/
    │   │   │   ├── transaction/
    │   │   │   ├── debt/
    │   │   │   ├── category/
    │   │   │   └── dashboard/
    │   │   ├── domain/
    │   │   │   ├── entity/
    │   │   │   ├── enums/
    │   │   │   └── repository/
    │   │   ├── infrastructure/
    │   │   │   ├── persistence/
    │   │   │   ├── security/
    │   │   │   ├── mapper/
    │   │   │   └── config/
    │   │   └── shared/
    │   │       ├── dto/
    │   │       ├── exception/
    │   │       └── response/
    │   └── resources/
    │       ├── application.yml
    │       ├── logback-spring.xml
    │       └── db/migration/
    │           └── V1__create_initial_schema.sql
    └── test/
        └── java/com/debtmap/

## 📂 Arquivos de configuração

| Arquivo | Descrição |
|---|---|
| `pom.xml` | Dependências e plugins Maven |
| `application.yml` | Configurações da aplicação com profiles dev/prod |
| `logback-spring.xml` | Logs coloridos no dev, JSON estruturado no prod |
| `docker-compose.yml` | PostgreSQL + aplicação containerizados |
| `Dockerfile` | Build multi-stage otimizado para produção |
| `.env.example` | Modelo de variáveis de ambiente |
| `.gitignore` | Arquivos ignorados pelo git |
| `V1__create_initial_schema.sql` | Schema inicial do banco com todas as tabelas |

## 👨‍💻 Autor

**Gabriel Veras**  
[GitHub](https://github.com/GabrielVeras-dev)