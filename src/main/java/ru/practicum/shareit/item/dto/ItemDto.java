package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import org.apache.coyote.Request;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;

@Data
@Builder
public class ItemDto {

    private Long id;

    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private Boolean available;
    @JsonIgnore
    private User owner;
    @JsonIgnore
    private Request request;

}
