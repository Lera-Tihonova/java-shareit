package ru.practicum.shareit.item;

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

    public static Item toItem(ItemCreateDto itemCreateDto) {
        if (itemCreateDto == null) {
            return null;
        }
        Item item = new Item();
        item.setName(itemCreateDto.getName());
        item.setDescription(itemCreateDto.getDescription());
        item.setAvailable(itemCreateDto.getAvailable());
        return item;
    }

    public static Item toItem(ItemUpdateDto itemUpdateDto) {
        if (itemUpdateDto == null) {
            return null;
        }
        Item item = new Item();
        item.setName(itemUpdateDto.getName());
        item.setDescription(itemUpdateDto.getDescription());
        item.setAvailable(itemUpdateDto.getAvailable());
        return item;
    }
}