Ниже ваше ДЗ. Кто не сделает - покусаю.

---

# ТЗ: мини-проект Todo List на Spring Boot

## 1. Цель

Реализовать небольшой backend-сервис для управления списком задач (Todo List) на `Spring Boot 4`.

Проект нужен для практики базовых вещей:

* REST API
* слоистая архитектура
* DTO
* валидация входных данных
* глобальный `exception handler`
* работа с `ConfigurationProperties`
* обязательно настроить Jackson на snake_case

---

## 2. Стек

Обязательный стек:

* Java 21+
* Spring Boot 4
* Spring Web
* Spring Validation
* Lombok
* Maven или Gradle
* Хранение данных — in-memory (`Map`) без БД

Дополнительно по желанию:

* Spring Boot Actuator
* springdoc-openapi

---

## 3. Функциональные требования

Сервис должен позволять:

### 3.1. Создать задачу

Поля задачи:

* `id`
* `title`
* `description`
* `status`
* `priority`
* `deadline`
* `created_at`

### 3.2. Получить список задач

Поддержать:

* получение всех задач
* фильтрацию по `status`
* фильтрацию по `priority`

### 3.3. Получить задачу по id

### 3.4. Обновить задачу по id

### 3.5. Удалить задачу по id

### 3.6. Отметить задачу как выполненную

Отдельный endpoint, который меняет статус на `DONE`.

---

## 4. Модель данных

### 4.1. Status

Enum:

* `NEW`
* `IN_PROGRESS`
* `DONE`

### 4.2. Priority

Enum:

* `LOW`
* `MEDIUM`
* `HIGH`

### 4.3. Todo

Пример полей:

* `Long id`
* `String title`
* `String description`
* `Status status`
* `Priority priority`
* `LocalDate deadline`
* `LocalDateTime createdAt`

---

## 5. API

## 5.1. Создать задачу

`POST /api/todos`

Request:

```json
{
  "title": "Купить продукты",
  "description": "Молоко, хлеб, сыр",
  "priority": "HIGH",
  "deadline": "2026-04-10"
}
```

Response:

```json
{
  "id": 1,
  "title": "Купить продукты",
  "description": "Молоко, хлеб, сыр",
  "status": "NEW",
  "priority": "HIGH",
  "deadline": "2026-04-10",
  "created_at": "2026-04-05T18:00:00"
}
```

## 5.2. Получить все задачи

`GET /api/todos`

С query params:

* `status`
* `priority`

Примеры:

* `GET /api/todos`
* `GET /api/todos?status=NEW`
* `GET /api/todos?priority=HIGH`
* `GET /api/todos?status=IN_PROGRESS&priority=MEDIUM`

## 5.3. Получить задачу по id

`GET /api/todos/{id}`

## 5.4. Обновить задачу

`PUT /api/todos/{id}`

## 5.5. Удалить задачу

`DELETE /api/todos/{id}`

## 5.6. Завершить задачу

`PATCH /api/todos/{id}/done`

---

## 6. Валидация

Использовать `jakarta.validation`.

### Для создания и обновления задачи:

* `title`:

    * не `null`
    * не blank
    * длина от 3 до 100
* `description`:

    * не больше 500 символов
* `priority`:

    * обязательное поле
* `deadline`:

    * не может быть в прошлом

### Пример:

```java
@NotBlank
@Size(min = 3, max = 100)
private String title;
```

---

## 7. Обработка ошибок

Нужен глобальный `@RestControllerAdvice`.

Обрабатывать:

* ошибки валидации (`MethodArgumentNotValidException`)
* задача не найдена (`TodoNotFoundException`)
* некорректный enum/параметр запроса
* прочие неожиданные ошибки

### Формат ошибки

```json
{
  "timestamp": "2026-04-05T18:10:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/todos"
}
```

Для ошибок валидации желательно добавить список полей:

```json
{
  "timestamp": "2026-04-05T18:10:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "fieldErrors": {
    "title": "must not be blank",
    "deadline": "must be today or in the future"
  },
  "path": "/api/todos"
}
```

---

## 8. Configuration Properties

Нужно вынести настройки приложения в `application.yml` и читать их через `@ConfigurationProperties`.

### Пример настроек:

```yaml
app:
  todo:
    default-priority: MEDIUM
    max-tasks-per-user: 100
    allow-empty-description: false
```

### Где использовать:

* если при создании задачи `priority` не передан, подставлять `defaultPriority`
* проверять лимит задач через `maxTasksPerUser`
* если `allowEmptyDescription=false`, запрещать пустое описание

---

## 9. Архитектура

Сделать обычную слоистую структуру:

* `controller`
* `service`
* `repository`
* `dto`
* `exception`
* `config`
* `model`

### Требования по слоям

* Controller работает только с DTO
* Service содержит бизнес-логику
* Repository хранит задачи в памяти
* Исключения свои, не кидать `RuntimeException("not found")`

---

## 10. Repository

Без БД.

Реализовать repository на `ConcurrentHashMap<Long, Todo>`.

Отдельно сделать генерацию id, например через `AtomicLong`.

---

## 11. DTO

Минимум:

* `CreateTodoRequest`
* `UpdateTodoRequest`
* `TodoResponse`

---

## 12. Нефункциональные требования

* Код должен запускаться без внешних зависимостей
* Проект должен собираться одной командой
* Все endpoint’ы должны корректно обрабатывать ошибки
* Ответы API должны быть в JSON
* Имена полей и endpoint’ов должны быть консистентными

---

## 13. Что должно получиться в итоге

На выходе должен быть проект, в котором есть:

* CRUD для todo
* валидация через annotations
* глобальный exception handler
* настройки через `ConfigurationProperties`
* аккуратная структура по пакетам

---

## 14. Дополнительные задания

Если захочешь усложнить:

* добавить сортировку по deadline/created_at
* добавить поиск по подстроке в title
* добавить endpoint `GET /api/todos/overdue`
* покрыть сервис unit-тестами

---

## 15. Критерии готовности

Проект считается готовым, если:

* приложение стартует
* все 6 endpoint’ов работают
* валидация реально срабатывает
* ошибки возвращаются в едином формате
* настройки из `application.yml` реально используются в логике
