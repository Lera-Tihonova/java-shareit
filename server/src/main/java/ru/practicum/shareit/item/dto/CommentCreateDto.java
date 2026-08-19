package ru.practicum.shareit.item.dto;

public class CommentCreateDto {
    private String text;

    public CommentCreateDto() {}

    public CommentCreateDto(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}