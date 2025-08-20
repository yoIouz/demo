## DEMO
Для заполнения индекса Elasticsearch использовался Debezium-embedded на уровне приложения, который через воркер процесс вычитывает PostgreSQL, собирает и индексирует общий документ (в текущей реализации debezium движка нет механизма ретраев).<br>  
Кеширование реализовывалось с помощью Redis, в котором также хранятся оффсеты для debezium и мапа с начальными балансами пользователей (для шедулера, увеличивающего баланс).<br> 
Использовался самописный PageDto<> для удобства.<br>  
Bcrypt для паролей не использовался для упрощения.<br>  
В приложении есть возможность выбора реализации поиска, регулируется параметром ENGINE(elastic/jpa - default) в .env.

## Стек:

- Java 21
- Spring Boot 3.4.2
- JPA
- Maven  
- PostgreSQL 17  
- Redis 3.21
- Elasticsearch 8.17
- Kibana 
- Debezium-embedded 
- JUnit + Testcontainers
- Swagger 
- Docker

---

## Запуск проекта:

### 1. Клонировать репозиторий
### 2. В корне проекта выполнить docker compose up --build

После полного запуска станет доступен Swagger-ui на http://localhost:8080/swagger-ui/index.html и Kibana на http://localhost:5601 для визуализации идексов Elasticsearch.<br>
Тестовые данные для базы накатываются со стартом приложения, лежат в src/resources/data.sql.<br>
Токен для запросов можно получить на /api/auth/login в Swagger, креды для токена находятся в .env (USER_1_LOGIN, PASS).<br>  
Тесты можно запустить из ide через mvn test.
