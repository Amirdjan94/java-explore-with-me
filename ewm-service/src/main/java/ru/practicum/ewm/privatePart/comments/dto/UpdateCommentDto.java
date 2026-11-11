package ru.practicum.ewm.privatePart.comments.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.stereotype.Component;

@Data
@Component
public class UpdateCommentDto {

    @Length(max = 7000, min = 20)
    @NotBlank
    String text;

}
