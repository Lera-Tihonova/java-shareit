package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

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
        return itemService.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addComment(
            @PathVariable Long itemId,
            @Valid @RequestBody CommentCreateDto commentCreateDto,
            @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        return itemService.addComment(itemId, userId, commentCreateDto);
    }
}