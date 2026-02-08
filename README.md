# Bank Authorization Service (Microservice)

Учебный микросервис авторизации пользователей в банковской системе. Часть распределённой архитектуры на основе Spring Cloud.

> **Технологии**: Java 17, Spring Boot 2.7, Spring Security, JWT, JPA/Hibernate, PostgreSQL, Liquibase, Eureka, Docker, Prometheus, Grafana

---

## Назначение сервиса

### Authorization Service решает следующие задачи:

- Регистрация и аутентификация пользователей
- Выдача и валидация JWT access и refresh токенов
- Управление ролями пользователей (ADMIN / USER)
- Аудит операций авторизации
- Интеграция с другими микросервисами через Service Discovery
- Сервис может использоваться как единая точка входа для проверки прав доступа в микросервисной банковской системе

---

## Основные возможности

- Регистрация и аутентификация пользователей
- Генерация JWT access/refresh токенов
- Ролевая модель (ADMIN / USER)
- Аудит операций
- Мониторинг через Prometheus + Grafana
- Service Discovery через Eureka

---

## Технические детали

### Архитектура
- **Микросервисная архитектура** (Authorization Service + Eureka Server)
- **Безопасность**: Spring Security + JWT
- **База данных**: PostgreSQL с миграциями Liquibase
- **Мониторинг**: Micrometer → Prometheus → Grafana
- **Контейнеризация**: Docker + Docker Compose

### Используемые технологии
| Категория      | Технологии                             |
|----------------|----------------------------------------|
| Backend        | Java 17, Spring Boot 2.7, Spring Cloud |
| Безопасность   | Spring Security, JWT (HS256)           |
| База данных    | PostgreSQL 17, JPA, Liquibase          |
| Инфраструктура | Docker, Docker Compose                 |
| Мониторинг     | Prometheus, Grafana                    |
| Тестирование   | JUnit 5, Mockito                       |

---

## Запуск проекта

### Требования
- Docker и Docker Compose
- JDK 17 (для локальной сборки)

### Локальный запуск

**Сборка проекта**:
```bash
  mvn package
```
**Запуск всех сервисов**:  
```bash
  docker-compose up -d
```
## Сервисы будут доступны по адресам:
- Authorization API: http://localhost:8087
- Eureka Dashboard: http://localhost:8761
- Grafana: http://localhost:3000 (admin/admin)
- Prometheus: http://localhost:9090
