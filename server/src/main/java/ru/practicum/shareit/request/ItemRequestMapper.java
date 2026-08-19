package ru.practicum.shareit.request;

import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest request) {
        if (request == null) return null;
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setCreated(request.getCreated());
        dto.setItems(List.of());
        return dto;
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest request, List<ItemResponseDto> items) {
        if (request == null) return null;
        ItemRequestDto dto = toItemRequestDto(request);
        dto.setItems(items != null ? items : List.of());
        return dto;
    }

    public static ItemRequest toItemRequest(ItemRequestDto dto, User requestor) {
        if (dto == null) return null;
        ItemRequest request = new ItemRequest();
        request.setDescription(dto.getDescription());
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());
        return request;
    }
}