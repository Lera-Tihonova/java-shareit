package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;

public class CommentCreateDto {
    @NotBlank(message = "Текст комментария не может быть пустым")
    private String text;

    public CommentCreateDto() {}

    public CommentCreateDto(String text) {
        this.text = text;
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}