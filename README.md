# 🚀 TaskFlow Manager

> Sistema de gerenciamento de férias corporativas com Clean Architecture e Domain-Driven Design

**Versão:** 0.0.1-SNAPSHOT  
**Status:** ✅ Em desenvolvimento ativo (API funcional com autenticação JWT)

---

## 📋 Índice

- [🚀 TaskFlow Manager](#-taskflow-manager)
  - [📋 Índice](#-índice)
  - [⚡ Quick Review (Docker Hub)](#-quick-review-docker-hub)
    - [Step by step](#step-by-step)
  - [Seed de utilizadores demo](#seed-de-utilizadores-demo)
  - [🎯 Visão Geral](#-visão-geral)
  - [🛠 Tecnologias](#-tecnologias)
  - [🏗️ Arquitetura](#️-arquitetura)
    - [📐 Estrutura atual (resumo)](#-estrutura-atual-resumo)
    - [🔄 Fluxo de dados](#-fluxo-de-dados)
    - [🔌 Regras de dependência](#-regras-de-dependência)
  - [🚀 Quick Start](#-quick-start)
    - [**Pré-requisitos**](#pré-requisitos)
    - [**1. Clonar o Repositório**](#1-clonar-o-repositório)
    - [**2. Subir Ambiente com Docker**](#2-subir-ambiente-com-docker)
    - [**3. Testar Endpoints**](#3-testar-endpoints)

---

## ⚡ Quick Review (Docker Hub)

As duas imagens publicas ja estao no Docker Hub (manifest **multi-arch** `linux/amd64` + `linux/arm64` na tag `latest`):
- `mateuslll/taskflow-frontend:latest`
- `mateuslll/taskflow-backend:latest`

### Step by step

1. Execute o comando abaixo na raiz de **qualquer um** dos dois repositorios (`task-flow-frontend` ou `task-flow-backend`), onde existe o ficheiro `docker-compose.review.yml`:

   ```bash
   docker compose -p task-flow-app -f docker-compose.review.yml up -d
   ```

2. O Docker vai iniciar todo o ambiente automaticamente (**frontend + backend + base de dados**).

3. Abra a aplicacao em: `http://localhost:3000`

**Acessos:**
- Frontend: http://localhost:3000
- API: http://localhost:8080/api/v1
- Swagger: http://localhost:8080/api/v1/swagger-ui/index.html
- PostgreSQL: localhost:5540 (user: `taskflow`, password: `taskflow123`, db: `taskflow`)

Para encerrar:

```bash
docker compose -p task-flow-app -f docker-compose.review.yml down
```

---

## Seed de utilizadores demo

Script `scripts/seed_demo_users.py` cria **3 gestores (MANAGER)** e **12 colaboradores (USER)** com **a mesma senha** para todos esses utilizadores, e associa **4 colaboradores** a cada gestor.

**Senha padrão dos 15 utilizadores** (sobrescreve com `DEMO_SEED_PASSWORD`):

| Variável | Valor por omissão | Notas |
|----------|-------------------|--------|
| `DEMO_SEED_PASSWORD` | `DemoTeam2026` | Cumpre as regras de senha da API (maiúscula, minúscula, dígitos). |
| `TASKFLOW_API_BASE` | `http://localhost:8080/api/v1` | URL base da API. |
| `ADMIN_EMAIL` | `admin@taskflow.com` | Conta admin para chamar a API. |
| `ADMIN_PASSWORD` | `Admin@123` | Igual ao *quick login* do frontend de desenvolvimento. |

O script chama `POST /bootstrap/create-admin` de forma idempotente (ignora conflito se o admin já existir), faz login e cria os utilizadores com emails `seed-m1@taskflow.demo` … `seed-m3@taskflow.demo` e `seed-u01@taskflow.demo` … `seed-u12@taskflow.demo`.

```bash
python3 scripts/seed_demo_users.py
```

Para republicar só o backend no Docker Hub (multi-arch), na raiz do repositório backend:

```bash
docker buildx use rail-builder

docker buildx build --platform linux/amd64,linux/arm64 \
  -t mateuslll/taskflow-backend:latest --push .
```

---

## 🎯 Visão Geral

O **TaskFlow Manager** é uma plataforma corporativa para gestão de férias e recursos humanos, construída com **Clean Architecture** e **Domain-Driven Design**. O sistema oferece uma API RESTful robusta e escalável, com foco em qualidade de código, testabilidade e manutenibilidade.

---

## 🛠 Tecnologias

**Backend:**
- ☕ Java 21
- 🍃 Spring Boot 3.5.6
- 🔒 Spring Security
- 🗄️ Spring Data JPA
- 🦅 Flyway (migrations)
- 🔄 MapStruct 1.5.5 (mapeamento Entity ↔ Domain)

**Banco de Dados:**
- 🐘 PostgreSQL 15.14

**Documentação:**
- 📖 Swagger/OpenAPI 3.0 (springdoc-openapi 2.8.7)

**Qualidade:**
- ✅ Lombok (redução de boilerplate)
- ✅ Bean Validation
- ✅ JUnit 5 + Mockito

**DevOps:**
- 🐳 Docker + Docker Compose
- 📦 Pipeline CI/CD (integração contínua)
---

## 🏗️ Arquitetura

O projeto segue **Clean Architecture + DDD**, com separação entre domínio, aplicação e infraestrutura.

### 📐 Estrutura atual (resumo)

```
src/main/java/com/mateuslll/taskflow
├── application/
│   ├── controllers/         # Endpoints REST (auth, users, vacations, bootstrap)
│   ├── persistence/         # Entities JPA, mappers e adapters de repositório
│   └── usecases/            # Casos de uso por domínio (auth, user, vacation)
├── common/
│   ├── exceptions/          # Exceções de negócio e handler global
│   └── messages/            # Mensagens centralizadas
├── domain/
│   ├── entities/            # Entidades de domínio
│   ├── enums/               # Enums de negócio
│   ├── repository/          # Portas (interfaces) de repositório
│   └── valueobject/         # Value objects (Email, Password, DateRange)
└── infrastructure/
    ├── config/              # OpenAPI e configuração geral
    └── security/            # JWT, filtros e configuração de segurança
```

### 🔄 Fluxo de dados

- **Entrada:** Controller → UseCase → Repositório (porta) → Adapter JPA → Banco
- **Saída:** Banco → Adapter JPA → Mapper → DTO/Response
- **Regras de negócio:** permanecem centralizadas no domínio e nos casos de uso

### 🔌 Regras de dependência

```
infrastructure -> application -> domain
```

- O `domain` não depende de frameworks.
- `application` orquestra casos de uso e contratos.
- `infrastructure` contém implementação técnica (Spring, JWT, JPA, OpenAPI).

---

## 🚀 Quick Start

### **Pré-requisitos**
- Java 21+ ([AdoptOpenJDK](https://adoptium.net/))
- Docker + Docker Compose
- Maven 3.9+

### **1. Clonar o Repositório**
```bash
git clone <repository-url>
cd task-flow-backend
```

### **2. Subir Ambiente com Docker**
```bash
docker compose up -d
```

**Serviços disponíveis:**
- 🌐 **API:** http://localhost:8080/api/v1
- 📖 **Swagger UI:** http://localhost:8080/api/v1/swagger-ui/index.html
- 🗄️ **PostgreSQL:** localhost:5432 (user: `taskflow`, password: `taskflow123`, db: `taskflow_db`)

### **3. Testar Endpoints**

**Criar Usuário:**
```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao.silva@company.com",
    "password": "SecurePass@123",
    "firstName": "João",
    "lastName": "Silva"
  }'
```

**Buscar Usuário:**
```bash
curl -X GET http://localhost:8080/api/v1/users/{id}
```

**Criar Solicitação de Férias:**
```bash
curl -X POST http://localhost:8080/api/v1/vacation-requests \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "{userId}",
    "startDate": "2025-12-20",
    "endDate": "2025-12-31"
  }'
```

**Swagger UI (Recomendado):**
Acesse http://localhost:8080/api/v1/swagger-ui/index.html para testar todos os endpoints interativamente.

---
