package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest request) {
        if (request == null) return null;
        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getCreated(),
                List.of()
        );
    }
}