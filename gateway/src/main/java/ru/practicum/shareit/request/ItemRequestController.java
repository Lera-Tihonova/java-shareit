package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestClient requestClient;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody ItemRequestDto requestDto
    ) {
        log.info("POST /requests, userId={}", userId);
        return requestClient.create(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> findByRequestor(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET /requests, userId={}", userId);
        return requestClient.findByRequestor(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> findAllOther(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(defaultValue = "0") Long from,
            @RequestParam(defaultValue = "10") Integer size
    ) {
        log.info("GET /requests/all, userId={}", userId);
        return requestClient.findAllOther(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> findById(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable Long requestId
    ) {
        log.info("GET /requests/{}", requestId);
        return requestClient.findById(userId, requestId);
    }
}