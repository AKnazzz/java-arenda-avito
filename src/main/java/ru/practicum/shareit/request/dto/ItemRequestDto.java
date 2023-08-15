package ru.practicum.shareit.request.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@Setter
public class ItemRequestDto {

    private Long id; // уникальный идентификатор запроса;
    @NotNull
    private String description; // текст запроса, содержащий описание требуемой вещи;
    private LocalDateTime created; // дата и время создания запроса.
}
