package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemResponseWithBookingDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.user.User;

import java.util.List;

public class ItemMapper {

    public static ItemResponseDto toItemResponseDto(Item item) {
        if (item == null) return null;
        return new ItemResponseDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static ItemResponseWithBookingDto toItemResponseWithBookingDto(Item item, BookingShortDto lastBooking,
                                                                          BookingShortDto nextBooking,
                                                                          List<CommentResponseDto> comments) {
        if (item == null) return null;
        return new ItemResponseWithBookingDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                lastBooking,
                nextBooking,
                comments != null ? comments : List.of()
        );
    }

    public static Item toItem(ItemCreateDto dto, User owner) {
        if (dto == null) return null;
        Item item = new Item();
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailable(dto.getAvailable());
        item.setOwner(owner);
        return item;
    }

    public static Item updateItem(Item existingItem, ItemUpdateDto dto) {
        if (dto == null) return existingItem;
        if (dto.getName() != null) existingItem.setName(dto.getName());
        if (dto.getDescription() != null) existingItem.setDescription(dto.getDescription());
        if (dto.getAvailable() != null) existingItem.setAvailable(dto.getAvailable());
        return existingItem;
    }
}