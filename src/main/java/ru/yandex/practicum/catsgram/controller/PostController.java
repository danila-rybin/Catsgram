package ru.yandex.practicum.catsgram.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.SortOrder;
import ru.yandex.practicum.catsgram.service.PostService;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/posts")
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public Collection<Post> findAll(
            @RequestParam(defaultValue = "desc") String sort,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size
    ) {
        SortOrder sortOrder = SortOrder.from(sort);
        if (sortOrder == null) {
            sortOrder = SortOrder.DESCENDING; // по умолчанию
        }

        if (size <= 0) {
            size = 10; // защита от отрицательного или нулевого размера
        }
        if (from < 0) {
            from = 0; // защита от отрицательного индекса
        }

        return postService.findAll(from, size, sortOrder);
    }

    @GetMapping("/{postId}")
    public Optional<Post> findById(@PathVariable long postId) {
        return postService.findById(postId);
    }

    @PostMapping
        @ResponseStatus(HttpStatus.CREATED)
        public Post create(@RequestBody Post post) {
            return postService.create(post);
    }

    @PutMapping
    public Post update(@RequestBody Post newPost) {
        return postService.update(newPost);
    }
}