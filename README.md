## Task Tracker - Многопользовательский планировщик задач

Проект представляет собой многопользовательский планировщик задач с микросервисной архитектурой. Пользователи могут создавать, редактировать и отслеживать свои задачи с ежедневными email-оповещениями.

## 🏗️ Архитектура

Проект состоит из следующих сервисов:

- **task-tracker-backend** - основной REST API сервис (Spring Boot)
- **task-tracker-frontend** - веб-интерфейс (Nginx + статические файлы)  
- **task-tracker-scheduler** - сервис планировщика для генерации отчетов
- **task-tracker-email-sender** - сервис отправки email-уведомлений
- **PostgreSQL** - база данных
- **Kafka** - брокер сообщений

**Особенности архитектуры:**
- Общение между backend и scheduler осуществляется через gRPC
- Асинхронная отправка email через Kafka
- JWT аутентификация
- Docker-оркестрация

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

## 📋Функциональность

Для пользователей

- **Регистрация и авторизация**
- **Создание, редактирование, удаление задач**
- **Отметка задач как выполненных и обратно**
- **Приветственное письмо после регистрации**
- **Ежедневные отчеты о выполненных задачах**


**Технические особенности**

JWT аутентификация
Микросервисная архитектура
Асинхронная обработка через Kafka
gRPC для межсервисного взаимодействия
Docker-контейнеризация

## 🛠️Tехнологический стек

Backend

Java 17+, Spring Boot 3+
Spring Security, Spring Data JPA, Spring Kafka
PostgreSQL, Hibernate
JWT, gRPC
Frontend

HTML5, CSS3, JavaScript
jQuery, Bootstrap
Nginx
Инфраструктура

Docker, Docker Compose, Docker Swarm
Kafka, PostgreSQL
GitHub Actions (CI/CD)

## 🤝 Контакты
**Oleg Zhvavyy**
- **Email**: [argumentoleg@gmail.com](mailto:argumentoleg@gmail.com)
- **Telegram**: [@ganst_13](https://t.me/ganst_13)
