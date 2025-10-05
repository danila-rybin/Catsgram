package ru.yandex.practicum.catsgram.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.exception.*;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        // Проверка обязательного email
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }

        // Проверка на дубликат email
        if (users.values().stream().anyMatch(u -> u.getEmail().equals(user.getEmail()))) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }

        // Генерация ID и даты регистрации
        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
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
