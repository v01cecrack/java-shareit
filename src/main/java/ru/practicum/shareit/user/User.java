package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * TODO Sprint add-controllers.
 */
@Getter
@Setter
@Builder
public class User {
    private int id;
    private String name;
    private String email;
}
