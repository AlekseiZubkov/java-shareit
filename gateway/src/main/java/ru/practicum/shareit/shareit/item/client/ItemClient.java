package ru.practicum.shareit.shareit.item.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.shareit.client.BaseClient;
import ru.practicum.shareit.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.shareit.constant.Constant;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> findItem(Long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> findItemsByOwner(Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                Constant.FROM, from,
                Constant.SIZE, size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> findItemsByText(Long userId, String text, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                Constant.TEXT, text,
                Constant.FROM, from,
                Constant.SIZE, size
        );
        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> createItem(Long userId, ItemRequestDto requestDto) {
        return post("", userId, requestDto);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemRequestDto requestDto) {
        return patch("/" + itemId, userId, requestDto);
    }

    public ResponseEntity<Object> deleteItem(Long userId, Long itemId) {
        return delete("/" + itemId, userId);
    }

    public ResponseEntity<Object> createComment(Long userId, Long itemId, CommentRequestDto commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}
