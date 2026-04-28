# 🚀 TaskFlow Manager

> Sistema de gerenciamento de férias corporativas com Clean Architecture e Domain-Driven Design

**Versão:** 0.0.1-SNAPSHOT  
**Status:** ✅ Desenvolvimento Inicial Completo | ⚠️ Requer JWT Security

---

## 📋 Índice

- [🎯 Visão Geral](#-visão-geral)
- [🛠 Tecnologias](#-tecnologias)
- [🏗️ Arquitetura](#️-arquitetura)
  - [Estrutura de Camadas](#-estrutura-de-camadas)
  - [Fluxo de Dados](#-fluxo-de-dados)
  - [Dependency Rules](#-dependency-rules-clean-architecture)
- [🚀 Quick Start](#-quick-start)
- [⚡ Quick Review (Docker Hub)](#-quick-review-docker-hub)

---

## ⚡ Quick Review (Docker Hub)

As duas imagens publicas ja estao no Docker Hub:
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
- Swagger: http://localhost:8080/swagger-ui/index.html
- PostgreSQL: localhost:5540 (user: `taskflow`, password: `taskflow123`, db: `taskflow`)

Para encerrar:

```bash
docker compose -p task-flow-app -f docker-compose.review.yml down
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
- 📦Bitbucket pipelines 
---

## 🏗️ Arquitetura

O projeto segue os princípios de **Clean Architecture** e **Domain-Driven Design**, organizando o código em camadas bem definidas com separação clara de responsabilidades.

### 📐 Estrutura de Camadas

```
📦 com.mateuslll.taskflow
│
├── � domain/                          # Camada de Domínio (Core Business Logic)
│   ├── entities/                       # Entidades de Negócio (Agregados)
│   │   ├── User.java                   # Agregado Raiz: Usuário
│   │   ├── Role.java                   # Entidade: Papel/Perfil
│   │   └── VacationRequest.java        # Agregado Raiz: Solicitação de Férias
│   │
│   ├── valueobjects/                   # Value Objects (Imutáveis)
│   │   ├── Email.java                  # Encapsula validação de email
│   │   ├── Password.java               # Encapsula criptografia BCrypt
│   │   └── VacationPeriod.java         # Período de férias com validações
│   │
│   ├── enums/                          # Enumerações de Domínio
│   │   ├── UserStatus.java             # Estados do usuário
│   │   ├── VacationRequestStatus.java  # Estados da solicitação
│   │   └── PermissionType.java         # Tipos de permissões
│   │
│   ├── repositories/                   # Interfaces de Repositório (Portas)
│   │   ├── UserRepository.java         # Contrato para persistência de usuários
│   │   ├── RoleRepository.java         # Contrato para persistência de roles
│   │   └── VacationRequestRepository.java
│   │
│   └── exceptions/                     # Exceções de Domínio
│       ├── DomainException.java        # Exceção base
│       ├── InvalidEmailException.java  # Violação de regra de email
│       └── InvalidPasswordException.java
│
├── � application/                      # Camada de Aplicação (Use Cases)
│   ├── usecases/                       # Casos de Uso (CQRS-like)
│   │   ├── user/
│   │   │   ├── CreateUser.java         # UC: Criar usuário
│   │   │   ├── RetrieveUser.java       # UC: Buscar usuário por ID
│   │   │   ├── UpdateUser.java         # UC: Atualizar usuário
│   │   │   ├── ActivateUser.java       # UC: Ativar usuário
│   │   │   ├── DeactivateUser.java     # UC: Desativar usuário
│   │   │   └── ChangePassword.java     # UC: Alterar senha
│   │   │
│   │   └── vacation/
│   │       ├── CreateVacationRequest.java
│   │       ├── RetrieveVacationRequest.java
│   │       ├── ApproveVacationRequest.java
│   │       ├── RejectVacationRequest.java
│   │       └── CancelVacationRequest.java
│   │
│   ├── controllers/                    # Controladores REST
│   │   ├── UserController.java         # Endpoints de usuários
│   │   └── VacationRequestController.java
│   │
│   └── persistence/                    # Adapters para repositórios
│
└── � infrastructure/                   # Camada de Infraestrutura (Adaptadores)
    ├── config/                         # Configurações Spring
    │   ├── SecurityConfig.java         # Configuração de segurança
    │   └── OpenApiConfig.java          # Configuração Swagger
    │
    ├── security/                       # Implementações de segurança
    │   ├── JwtTokenProvider.java       # Geração/validação JWT
    │   └── CustomUserDetailsService.java
    │
    └── persistence/                    # Implementações JPA
        ├── entities/                   # Entidades JPA (Adaptadores)
        │   ├── UserJpaEntity.java      # Mapeamento ORM do User
        │   ├── RoleJpaEntity.java
        │   └── VacationRequestJpaEntity.java
        │
        ├── repositories/               # Repositórios JPA (Spring Data)
        │   ├── UserJpaRepositoryImpl.java  # Implementa UserRepository
        │   └── RoleJpaRepositoryImpl.java
        │
        └── mappers/                    # Mappers (MapStruct)
            ├── UserMapper.java         # Domain ↔ JPA Entity
            └── VacationRequestMapper.java
```

**Fluxo de Leitura:** Database → JPA Entity → Mapper → Domain Entity → Use Case → Controller → JSON Response  
**Fluxo de Escrita:** JSON Request → Controller → Use Case → Domain Entity (validações) → Repository → JPA Entity → Database

### 🔌 Dependency Rules (Clean Architecture)

```
Infrastructure → Application → Domain
    ↓               ↓            ↑
Frameworks      Use Cases    Pure Java
Spring Boot     DTOs         Business Logic
JPA/Hibernate   Controllers  Entities
MapStruct                    Value Objects
```

### 🎨 Princípios de Design

**✅ Separation of Concerns** - Cada camada tem responsabilidade única e bem definida  
**✅ Dependency Inversion** - Domain não conhece Infrastructure (depende de abstrações)  
**✅ Single Responsibility** - Cada Use Case executa uma operação de negócio  
**✅ DDD Tactical Patterns** - Aggregates, Value Objects, Repositories, Domain Events

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
