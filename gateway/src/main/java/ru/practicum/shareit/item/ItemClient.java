package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.HashMap;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/items"))
                        .requestFactory(HttpComponentsClientHttpRequestFactory.class)
                        .build()
        );
    }

    public ResponseEntity<Object> findAll(Long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> findById(Long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> create(Long userId, ItemCreateDto itemCreateDto) {
        return post("", userId, itemCreateDto);
    }

    public ResponseEntity<Object> update(Long userId, Long itemId, ItemUpdateDto itemUpdateDto) {
        return patch("/" + itemId, userId, itemUpdateDto);
    }

    public ResponseEntity<Object> search(String text) {
        Map<String, Object> params = new HashMap<>();
        params.put("text", text != null ? text : "");
        return get("/search?text={text}", null, params);
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentCreateDto commentCreateDto) {
        return post("/" + itemId + "/comment", userId, commentCreateDto);
    }
}