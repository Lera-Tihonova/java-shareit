package ru.practicum.shareit.item.comment;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public class CommentMapper {

    public static Comment toComment(CommentCreateDto dto, Item item, User author) {
        if (dto == null) return null;
        Comment comment = new Comment();
        comment.setText(dto.getText());  // <- getText() должен быть в CommentCreateDto
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        return comment;
    }

    public static CommentResponseDto toCommentResponseDto(Comment comment) {
        if (comment == null) return null;
        return new CommentResponseDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }
}