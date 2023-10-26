package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Entity
@Table(name = "users", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;
    @NotNull
    private String name;
    @NotNull
    @Email(message = "Неправильный адрес электронной почты")
    private String email;
}
