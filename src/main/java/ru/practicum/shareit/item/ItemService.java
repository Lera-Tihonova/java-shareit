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

    public List<ItemResponseDto> findByOwner(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        return itemRepository.findByOwner(userId).stream()
                .map(ItemMapper::toItemResponseDto)
                .collect(Collectors.toList());
    }

    public ItemResponseDto findById(Long id, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        return itemRepository.findById(id)
                .map(ItemMapper::toItemResponseDto)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + id + " не найдена"));
    }

    public ItemResponseDto create(ItemCreateDto itemCreateDto, Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        Item item = ItemMapper.toItem(itemCreateDto);
        item.setOwner(owner);
        Item createdItem = itemRepository.create(item);
        return ItemMapper.toItemResponseDto(createdItem);
    }

    public ItemResponseDto update(Long itemId, ItemUpdateDto itemUpdateDto, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        Item existingItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id " + itemId + " не найдена"));

        if (!existingItem.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Пользователь не является владельцем вещи");
        }

        Item updatedItem = ItemMapper.updateItem(existingItem, itemUpdateDto);
        Item result = itemRepository.update(itemId, updatedItem);
        return ItemMapper.toItemResponseDto(result);
    }

    public List<ItemResponseDto> search(String text) {
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemResponseDto)
                .collect(Collectors.toList());
    }
}