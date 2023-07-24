package ru.practicum.shareit.user;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * TODO Sprint add-controllers.
 */
@ToString
@Getter
@Setter
@Builder
public class User {
    private int id;
    private String name;
    private String email;
}
