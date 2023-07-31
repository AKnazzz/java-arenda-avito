package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;


@Entity
@Table(name = "items")
@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name", length = 255, nullable = false)
    private String name;
    @Column(name = "description", length = 512, nullable = false)
    private String description;
    @Column(name = "available")
    private Boolean available;
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY)
    private User owner;
    private Long requestId;

}
