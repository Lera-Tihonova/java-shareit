package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;

public class ItemRequestDto {
    private Long id;

    @NotBlank(message = "Описание запроса не может быть пустым")
    private String description;

    private String created;

    public ItemRequestDto() {}

    public ItemRequestDto(Long id, String description, String created) {
        this.id = id;
        this.description = description;
        this.created = created;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }
}