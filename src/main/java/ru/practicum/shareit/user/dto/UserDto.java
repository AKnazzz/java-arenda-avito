package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.util.ValidationGroup;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {

    private Long id;

    @NotBlank(groups = ValidationGroup.Create.class)
    private String name;

    @Email(groups = ValidationGroup.Create.class)
    @NotBlank(groups = ValidationGroup.Create.class)
    private String email;

}
