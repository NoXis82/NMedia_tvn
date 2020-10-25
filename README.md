# Домашняя работа к занятию «1.2. Ресурсы, View и ViewGroup»

## Задача Launcher Icon

Заменена иконка приложения из предыдущего ДЗ на [логотип Нетологии](assets/netology.svg).

## Задача Translations

Мы договорились, что всё будем делать на двух языках: русском и английском.

Добавлен файл переводов в своё приложение.

Переведены:
1. Название приложения (на русском будет "НМедиа")
1. Текст (на русском будет "НМедиа!")

# Домашняя работа к занятию «1.1. Android Studio, SDK, эмулятор и первое приложение»

### Задача
Проект, с текстовой надписью на экране `NMedia!`

При этом:
* groupId: ru.netology
* artifactId: nmedia
* version: 1.0-SNAPSHOT

Для проекта настроен GitHub Actions.
Содержимое для файла yaml следующее:

```yaml
name: CI

on:
  push:
    branches: [ master ]
  pull_request:
    branches: [ master ]

jobs:
  build:
    runs-on: ubuntu-20.04

    steps:
      - name: Checkout Code
        uses: actions/checkout@v2

      - name: Build
        run: |
          chmod +x ./gradlew
          ./gradlew build --info

      - name: Upload Build Artifact
        uses: actions/upload-artifact@v2
        with:
          name: app-debug.apk
          path: app/build/outputs/apk/debug/app-debug.apk
```


Готовый файл [.gitignore](../.gitignore)