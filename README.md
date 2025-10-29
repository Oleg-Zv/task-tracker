## 📝 Task Tracker – Многопользовательский планировщик задач
<br>
**Task Tracker** — это современный многопользовательский планировщик задач с микросервисной архитектурой.  


---
## 📋Функциональность

Для пользователей:

- **Регистрация и авторизация**
- **Создание, редактирование, удаление задач**
- **Отметка задач как выполненных и обратно**
- **Приветственное письмо после регистрации**
- **Ежедневные отчеты о выполненных задачах**
---

## 🏗️ Архитектура проекта

Проект состоит из следующих компонентов:

| Сервис | Назначение |
|--------|------------|
| **task-tracker-backend** | Основной REST API сервис (Spring Boot) |
| **task-tracker-frontend** | Веб-интерфейс (Nginx + статические файлы) |
| **task-tracker-scheduler** | Планировщик задач и генерация отчетов |
| **task-tracker-email-sender** | Отправка email-уведомлений |
| **PostgreSQL** | Хранение данных |
| **Kafka** | Асинхронный обмен сообщениями между сервисами |

---

## ⚙️ Технические особенности

- 🔑 **JWT аутентификация** для безопасного доступа  
- 🏢 **Микросервисная архитектура** для масштабируемости  
- ⚡ **Асинхронная обработка** через Kafka  
- 🔗 **gRPC** для межсервисного взаимодействия  
- 🐳 **Docker-контейнеризация** для простого деплоя


## 🚀 Локальный запуск

### Предварительные требования
- Docker & Docker Compose
- Git

### 1. Клонирование репозитория
```bash
git clone <repository-url>
cd task-tracker

### 2. Инициализация Docker Swarm
docker swarm init

### 3. Создание секретов

Для базы данных:
echo "your_db_user" | docker secret create db_user -
echo "your_db_password" | docker secret create db_password -
echo "your_db_name" | docker secret create db_name -

Для JWT ключей:
# Генерация RSA ключей
openssl genrsa -out jwt_private.pem 2048
openssl rsa -in jwt_private.pem -pubout -out jwt_public.pem

# Создание секретов из файлов
docker secret create jwt_private_key jwt_private.pem
docker secret create jwt_public_key jwt_public.pem

Для Kafka:
echo "your_kafka_cluster_id" | docker secret create kafka_cluster_id -

Для email сервиса:
echo "your_mailjet_api_key" | docker secret create api_key -
echo "your_mailjet_secret_key" | docker secret create secret_key -

4. Запуск приложения
docker stack deploy -c docker-compose.yml "ваше название"


```
##

Вы можете протестировать **Task Tracker** прямо в браузере по следующей ссылке:

[🌐 Перейти к приложению](http://217.114.14.212/login.html)


## 🛠️ Технологический стек

### ☕ Backend
- <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/java/java-original.svg" width="20"/> **Java 21+**
- <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/spring/spring-original.svg" width="20"/> **Spring Boot 3+**
- <img src="https://img.icons8.com/ios-filled/50/000000/lock-2.png" width="20"/> **Spring Security / JWT (RSA-ключи)**
- <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/hibernate/hibernate-original.svg" width="20"/> **Spring Data JPA / Hibernate**
- <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/apachekafka/apachekafka-original.svg" width="20"/> **Spring Kafka**
- ⚡ **gRPC (backend ↔ scheduler)**
- 📜 **Liquibase**
- 🧪 **Testcontainers**
- <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/postgresql/postgresql-original.svg" width="20"/> **PostgreSQL**

### 💻 Frontend
- <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/html5/html5-original.svg" width="20"/> **HTML5 / CSS3 / JS (ES6+)**
- <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/jquery/jquery-original.svg" width="20"/> **jQuery + AJAX**
- <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/bootstrap/bootstrap-plain.svg" width="20"/> **Bootstrap 5**
- <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/nginx/nginx-original.svg" width="20"/> **Nginx**

### ☁️ Инфраструктура
- <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/docker/docker-original.svg" width="20"/> **Docker / Docker Compose**
- <img src="https://www.docker.com/wp-content/uploads/2022/03/Moby-logo.png" width="20"/> **Docker Swarm**
- <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/apachekafka/apachekafka-original.svg" width="20"/> **Kafka (message broker)**
- <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/postgresql/postgresql-original.svg" width="20"/> **PostgreSQL**
- <img src="https://img.icons8.com/ios-filled/50/000000/github.png" width="20"/> **GitHub Actions (CI/CD)**
- <img src="https://www.docker.com/wp-content/uploads/2022/03/Moby-logo.png" width="20"/> **Docker Hub**
- <img src="https://img.icons8.com/color/48/000000/email.png" width="20"/> **Mailjet (SMTP-сервис)**


## 🤝 Контакты
**Oleg Zhvavyy**
- **Email**: [argumentoleg@gmail.com](mailto:argumentoleg@gmail.com)
- **Telegram**: [@ganst_13](https://t.me/ganst_13)
