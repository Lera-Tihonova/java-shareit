package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.CommentCreateDto;
import ru.practicum.shareit.item.comment.CommentResponseDto;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemResponseWithBookingDto> findAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.findByOwner(userId);
    }

    @GetMapping("/{itemId}")
    public ItemResponseWithBookingDto findById(
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return itemService.findById(itemId, userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponseDto create(
            @Valid @RequestBody ItemCreateDto itemCreateDto,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return itemService.create(itemCreateDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemResponseDto update(
            @PathVariable Long itemId,
            @RequestBody ItemUpdateDto itemUpdateDto,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return itemService.update(itemId, itemUpdateDto, userId);
    }

    @GetMapping("/search")
    public List<ItemResponseDto> search(@RequestParam(required = false) String text) {
        if (text == null || text.isBlank()) {
            return List.of();
        }
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto addComment(
            @PathVariable Long itemId,
            @Valid @RequestBody CommentCreateDto commentCreateDto,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return itemService.addComment(itemId, userId, commentCreateDto);
    }
}