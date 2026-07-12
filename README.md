# LinkTracker

LinkTracker – Telegram-бот, который отслеживает изменения на веб-страницах и оперативно информирует пользователя о них.

Это шаблон проекта, который вам необходимо взять за основу для разработки своей системы.
В данном файле должна находиться инструкция для ассистента по запуску и настройке бота.

Полезную для разработки проекта информацию вы можете найти в файле [HELP.md](./HELP.md).

`.env`-файл

```shell
TELEGRAM_TOKEN=...
GITHUB_TOKEN=github_pat_...

STACKOVERFLOW_KEY=...
STACKOVERFLOW_ACCESS_TOKEN=...

POSTGRES_USER=postgres
POSTGRES_PASSWORD=admin
```

Запуск:

```shell
mvn clean package
docker-compose up --build
```

