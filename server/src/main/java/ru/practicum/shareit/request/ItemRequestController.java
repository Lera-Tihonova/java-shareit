package ru.practicum.shareit.request;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@RestController
@RequestMapping("/requests")
public class ItemRequestController {
    private final ItemRequestService requestService;

    public ItemRequestController(ItemRequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ItemRequestDto create(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody ItemRequestDto requestDto
    ) {
        return requestService.create(userId, requestDto.getDescription());
    }

    @GetMapping
    public List<ItemRequestDto> findByRequestor(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return requestService.findByRequestor(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> findAllOther(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") Long from,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        return requestService.findAllOther(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto findById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId
    ) {
        return requestService.findById(requestId, userId);
    }
}