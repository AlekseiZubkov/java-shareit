package ru.practicum.shareit.shareit.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {

	private Integer itemId;

	@FutureOrPresent
	private LocalDateTime start;

	@Future
	private LocalDateTime end;
}
