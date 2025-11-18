# Todo List API

API de gerenciamento de tarefas desenvolvida com **Java 21**, **Spring Boot 3** e **Docker**.

O objetivo deste projeto foi ir além do básico e construir uma aplicação backend pronta para produção, com foco em boas práticas de segurança, testes automatizados e DevOps.

## Demo Online

A API está rodando no Railway. Você pode testar os endpoints e ver a documentação via Swagger UI aqui:

**[Acessar Documentação (Swagger)](https://SUA-URL-DO-RAILWAY.up.railway.app/swagger-ui.html)**

> **Login para teste:**
> Para acessar as rotas protegidas, use o endpoint /auth/login com um destes usuários:
> - **User:** testA / **Senha:** 12456
> - **User:** testB / **Senha:** 12457

## Tech Stack

* **Core:** Java 21, Spring Boot 3
* **Banco de Dados:** PostgreSQL (Prod), H2 (Dev), Spring Data JPA
* **Segurança:** Spring Security, JWT (Tokens)
* **Testes:** JUnit 5, Mockito, Testcontainers (Testes de integração reais)
* **DevOps:** Docker, Docker Compose, GitHub Actions (CI/CD)
* **Extra:** Spring Doc (Swagger), Actuator (Monitoramento)

## O que tem no projeto?

* **Segurança Real:** Autenticação via Token JWT. Nada de basic auth.
* **Isolamento de Dados:** Um usuário não consegue acessar ou alterar as tarefas de outro.
* **CRUD Completo:** Criar, listar, atualizar e deletar tarefas com validações de entrada.
* **Observabilidade:** Logs estruturados e endpoints de health check monitorando o banco de dados.
* **Pipeline CI/CD:** A cada push na main, o código é testado, compilado e a imagem Docker é atualizada automaticamente.

## Estrutura do Projeto

O código está organizado seguindo os padrões do Spring Boot:

```text
.
├── docker-compose.yml      # Orquestração dos containers (App + DB)
├── Dockerfile              # Definição da imagem Docker da aplicação
├── pom.xml                 # Dependências do Maven
├── .github/workflows       # Configuração do Pipeline de CI/CD
└── src
    ├── main
    │   ├── java/com/example/demo
    │   │   ├── config          # Configurações de Segurança e Inicialização
    │   │   ├── controller      # Controladores REST (API)
    │   │   ├── dto             # Objetos de Transferência (Requests/Responses)
    │   │   ├── exception       # Tratamento global de erros
    │   │   ├── mapper          # Conversão DTO <-> Entidade
    │   │   ├── model           # Entidades do banco (JPA)
    │   │   ├── repository      # Acesso a dados
    │   │   └── service         # Regras de negócio
    │   └── resources           # Configurações (application.properties)
    └── test                    # Testes Unitários e de Integração

```

## Como rodar localmente

Se você tem o **Docker** instalado, é só rodar um comando.

1. Clone o repositório.
2. Na raiz do projeto, execute:

```bash
docker-compose up -d --build