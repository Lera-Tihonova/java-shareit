package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.BookingShortDto;
import ru.practicum.shareit.item.comment.CommentResponseDto;
import ru.practicum.shareit.user.User;

import java.util.List;

public class ItemMapper {

    public static ItemResponseDto toItemResponseDto(Item item) {
        if (item == null) {
            return null;
        }
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
        if (item == null) {
            return null;
        }
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

    public static Item toItem(ItemCreateDto itemCreateDto, User owner) {
        if (itemCreateDto == null) {
            return null;
        }
        Item item = new Item();
        item.setName(itemCreateDto.getName());
        item.setDescription(itemCreateDto.getDescription());
        item.setAvailable(itemCreateDto.getAvailable());
        item.setOwner(owner);
        return item;
    }

    public static Item updateItem(Item existingItem, ItemUpdateDto itemUpdateDto) {
        if (itemUpdateDto == null) {
            return existingItem;
        }

        if (itemUpdateDto.getName() != null) {
            existingItem.setName(itemUpdateDto.getName());
        }
        if (itemUpdateDto.getDescription() != null) {
            existingItem.setDescription(itemUpdateDto.getDescription());
        }
        if (itemUpdateDto.getAvailable() != null) {
            existingItem.setAvailable(itemUpdateDto.getAvailable());
        }

        return existingItem;
    }
}