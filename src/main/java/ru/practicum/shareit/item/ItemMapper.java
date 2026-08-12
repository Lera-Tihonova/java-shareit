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

    public static Item updateItem(Item existingItem, ItemUpdateDto itemUpdateDto) {
        if (itemUpdateDto == null) {
            return existingItem;
        }

        Item item = new Item();
        item.setId(existingItem.getId());
        item.setOwner(existingItem.getOwner());
        item.setRequest(existingItem.getRequest());

        if (itemUpdateDto.getName() != null) {
            item.setName(itemUpdateDto.getName());
        } else {
            item.setName(existingItem.getName());
        }

        if (itemUpdateDto.getDescription() != null) {
            item.setDescription(itemUpdateDto.getDescription());
        } else {
            item.setDescription(existingItem.getDescription());
        }

        if (itemUpdateDto.getAvailable() != null) {
            item.setAvailable(itemUpdateDto.getAvailable());
        } else {
            item.setAvailable(existingItem.getAvailable());
        }

        return item;
    }
}