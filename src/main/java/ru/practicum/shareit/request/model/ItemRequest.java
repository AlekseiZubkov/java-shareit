package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.User;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Entity
@Table(name = "requests", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @NotBlank
    private String description;
    @ManyToOne
    @JoinColumn(name = "requester_id", referencedColumnName = "id")
    private User requester;

    @Column(name = "create_date", nullable = false)
    private LocalDateTime created;
}
