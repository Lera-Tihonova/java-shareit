package ru.practicum.shareit.request;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    public ItemRequestService(ItemRequestRepository requestRepository,
                              UserRepository userRepository,
                              ItemRepository itemRepository) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    public ItemRequestDto create(Long userId, String description) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        ItemRequest request = new ItemRequest();
        request.setDescription(description);
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());

        ItemRequest saved = requestRepository.save(request);
        ItemRequestDto dto = ItemRequestMapper.toItemRequestDto(saved);
        dto.setItems(List.of());
        return dto;
    }

    public List<ItemRequestDto> findByRequestor(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequest> requests = requestRepository.findByRequestorId(userId, sort);

        return requests.stream()
                .map(request -> {
                    List<Item> items = itemRepository.findByRequestId(request.getId());
                    List<ru.practicum.shareit.item.dto.ItemResponseDto> itemDtos = items.stream()
                            .map(ItemMapper::toItemResponseDto)
                            .collect(Collectors.toList());
                    return ItemRequestMapper.toItemRequestDto(request, itemDtos);
                })
                .collect(Collectors.toList());
    }

    public List<ItemRequestDto> findAllOther(Long userId, Long from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        PageRequest pageRequest = PageRequest.of(from.intValue() / size, size, Sort.by(Sort.Direction.DESC, "created"));
        List<ItemRequest> requests = requestRepository.findAllOther(userId, pageRequest);

        return requests.stream()
                .map(request -> {
                    List<Item> items = itemRepository.findByRequestId(request.getId());
                    List<ru.practicum.shareit.item.dto.ItemResponseDto> itemDtos = items.stream()
                            .map(ItemMapper::toItemResponseDto)
                            .collect(Collectors.toList());
                    return ItemRequestMapper.toItemRequestDto(request, itemDtos);
                })
                .collect(Collectors.toList());
    }

    public ItemRequestDto findById(Long requestId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));

        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id " + requestId + " не найден"));

        List<Item> items = itemRepository.findByRequestId(requestId);
        List<ru.practicum.shareit.item.dto.ItemResponseDto> itemDtos = items.stream()
                .map(ItemMapper::toItemResponseDto)
                .collect(Collectors.toList());

        return ItemRequestMapper.toItemRequestDto(request, itemDtos);
    }
}