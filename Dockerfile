# --- Этап сборки (builder) ---
FROM amazoncorretto:21-alpine AS builder

# Лучше использовать абсолютный путь для надёжности
WORKDIR /application

# Копируем собранный jar-файл из папки target
COPY target/*.jar app.jar

# Распаковываем jar на слои с помощью layertools
RUN java -Djarmode=layertools -jar app.jar extract


# --- Финальный (runtime) этап ---
FROM amazoncorretto:21-alpine
WORKDIR /application

# Копируем распакованные слои из builder
COPY --from=builder /application/dependencies/ ./
COPY --from=builder /application/spring-boot-loader/ ./
COPY --from=builder /application/snapshot-dependencies/ ./
COPY --from=builder /application/application ./

# Указываем команду запуска Spring Boot
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
