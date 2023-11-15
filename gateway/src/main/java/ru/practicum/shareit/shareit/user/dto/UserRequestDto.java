package ru.practicum.shareit.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class UserRequestDto {
    private String name;
    @Email
    private String email;
}