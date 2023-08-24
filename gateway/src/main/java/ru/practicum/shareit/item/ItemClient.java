package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

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

    public ResponseEntity<Object> itemCreate(ItemDto itemDto, Long userOwnerId) {
        return post("", userOwnerId, itemDto);
    }

    public ResponseEntity<Object> getById(Long id, Long userId) {
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> getAllItemsByOwner(int from, int size, Long userOwnerId) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userOwnerId, parameters);
    }

    public ResponseEntity<Object> deleteItem(Long id, Long userOwnerId) {
        return delete("/" + id, userOwnerId);
    }

    public ResponseEntity<Object> updateItem(Long id, ItemDto itemDto, Long userOwnerId) {
        return patch("/" + id, userOwnerId, itemDto);
    }

    public ResponseEntity<Object> searchItem(int from, int size, String text1, Long userId) {
        Map<String, Object> parameters = Map.of(
                "text", text1,
                "from", from,
                "size", size
        );

        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> addComment(long itemId, Long userId, CommentDto commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }

}
