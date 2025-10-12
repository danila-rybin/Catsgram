package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> findAll() {
        return users.values();
    }

    public User create (User user) {
        // Проверка обязательного email
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }

        if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        // Генерация ID и даты регистрации
        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        users.put(user.getId(), user);
        return user;
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        User existingUser = users.get(newUser.getId());
        if (existingUser == null) {
            throw new NotFoundException("Пост с id = " + newUser.getId() + " не найден");
        }

        // Проверка на дубликат email при обновлении
        if (newUser.getEmail() != null &&
                !newUser.getEmail().equals(existingUser.getEmail()) &&
                users.values().stream().anyMatch(u -> u.getEmail().equals(newUser.getEmail()))) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        // Обновление только непустых полей
        if (newUser.getEmail() != null) existingUser.setEmail(newUser.getEmail());
        if (newUser.getUsername() != null) existingUser.setUsername(newUser.getUsername());
        if (newUser.getPassword() != null) existingUser.setPassword(newUser.getPassword());

        return existingUser;
    }

    // Генерация следующего ID
    private long getNextId() {
        return users.keySet().stream().mapToLong(id -> id).max().orElse(0) + 1;
    }
}