package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public List<ItemDto> findByOwner(Long userId) {
        userRepository.findById(userId);
        return itemRepository.findByOwner(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public ItemDto findById(Long id, Long userId) {
        userRepository.findById(userId);
        return ItemMapper.toItemDto(itemRepository.findById(id));
    }

    public ItemDto create(ItemDto itemDto, Long userId) {
        User owner = userRepository.findById(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        Item createdItem = itemRepository.create(item);
        return ItemMapper.toItemDto(createdItem);
    }

    public ItemDto update(Long itemId, ItemDto itemDto, Long userId) {
        userRepository.findById(userId);

        Item existingItem = itemRepository.findById(itemId);

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем вещи");
        }

        Item updatedItem = ItemMapper.toItem(itemDto);
        updatedItem.setOwner(existingItem.getOwner());
        updatedItem.setRequest(existingItem.getRequest());
        Item result = itemRepository.update(itemId, updatedItem);
        return ItemMapper.toItemDto(result);
    }

    public List<ItemDto> search(String text) {
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}