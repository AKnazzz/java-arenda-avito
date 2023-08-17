package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Builder
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id; // уникальный идентификатор запроса;
    @Column
    private String description; // текст запроса, содержащий описание требуемой вещи;
    @JoinColumn(name = "requestor_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User requestor; // пользователь, создавший запрос;
    @Column
    private LocalDateTime created; // дата и время создания запроса.
}
