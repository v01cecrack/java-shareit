package ru.practicum.shareit.booking.dto;

public enum BookingState {
    // Все
    ALL,
    // Текущие
    CURRENT,
    // Будущие
    FUTURE,
    // Завершенные
    PAST,
    // Отклоненные
    REJECTED,
    // Ожидающие подтверждения
    WAITING;

    public static BookingState getState(String state) {
        BookingState stateEnum;
        try {
            stateEnum = BookingState.valueOf(state);

        } catch (Exception ex) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }
        return stateEnum;
    }
}
