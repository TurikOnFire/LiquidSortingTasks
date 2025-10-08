# LiquidSortingTasks

# Task №1

В проекте имеется 2 реализации 1-го таска. Task1 содержит в себе реализацию "сортировки жидкостей" реализуя принципы ООП и конфигурирования игры. Task11 содержит в себе реализацию выполненную в одном файле с одним решением в виде примера.

### 1. Собрать проект
	mvn clean package

### 2. Запустить (JAR со всеми зависимостями)
	java -jar target/SortingLiquids-1.0-SNAPSHOT-task1.jar
	java -jar target/SortingLiquids-1.0-SNAPSHOT-task11.jar

# Task №2

### 1. Собрать проект
	mvn clean package

### 2. Запустить проект (Таск1, Таск11)
	java -jar target/SortingLiquids-1.0-SNAPSHOT-task2.jar

После запуска:
	Server started at http://localhost:8080 (uploads -> data/uploads)

### API
| Метод               | Назначение                       |
| ------------------- | -------------------------------- |
| `POST /upload`      | загрузка файла (form-data: file) |
| `GET /d/{token}`    | скачать файл по токену           |
| `GET /meta/{token}` | получить JSON-метаданные         |
| `GET /admin/list`   | список всех файлов (админка)     |

# Task №3
	смотри отдельный репозиторий: https://github.com/TurikOnFire/LiquidSortingTask3
