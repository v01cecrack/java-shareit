package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.error.ObjectNotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final Sort sort = Sort.by(Sort.Direction.DESC, "start");

    @Override
    public BookingDto createBooking(BookingDto bookingDto, Integer userId) {
        validateTimeBooking(bookingDto);
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new ObjectNotFoundException("Вещь не существует"));
        if (!item.getAvailable()) {
            throw new ValidationException("Данная вещь недоступна");
        }
        if (item.getOwner().getId() == userId) {
            throw new ObjectNotFoundException("Вы владелец этой вещи");
        }
        List<Booking> bookings = bookingRepository.findBookingByItem_Id(item.getId());
        for (Booking booking : bookings) {
            if (booking.getStart() == bookingDto.getStart() || booking.getEnd() == bookingDto.getEnd()) {
                throw new ValidationException("Данная вещь уже забронирована");
            }
            if (bookingDto.getStart().isAfter(booking.getStart()) && bookingDto.getEnd().isBefore(booking.getEnd())) {
                throw new ValidationException("Данная вещь уже забронирована");
            }
            if (bookingDto.getStart().isBefore(booking.getStart()) && bookingDto.getEnd().isAfter(booking.getEnd())) {
                throw new ValidationException("Данная вещь уже забронирована");
            }
            if (bookingDto.getStart().isBefore(booking.getStart()) && bookingDto.getEnd().isAfter(booking.getStart())
                    && bookingDto.getEnd().isBefore(booking.getEnd())) {
                throw new ValidationException("Данная вещь уже забронирована");
            }
            if (bookingDto.getStart().isAfter(booking.getStart()) && bookingDto.getStart().isBefore(booking.getEnd())
                    && bookingDto.getEnd().isAfter(booking.getEnd())) {
                throw new ValidationException("Данная вещь уже забронирована");
            }

        }
        Booking booking = BookingMapper.toBooking(bookingDto, item);
        booking.setBooker(user);
        booking.setStatus(Status.WAITING);
        bookingRepository.save(booking);
        bookingDto = BookingMapper.toBookingDto(booking);
        return bookingDto;
    }

    @Override
    public BookingDto getBooking(Integer bookingId, Integer userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ObjectNotFoundException("Такой брони нет"));

        if (booking.getBooker().getId() != userId &&
                booking.getItem().getOwner().getId() != userId) {
            throw new ObjectNotFoundException("Данные бронирования позволяет выполнять автору бронирования или владельцу вещи!");
        }

        var result = BookingMapper.toBookingDto(bookingRepository.findById(bookingId).get());

        result.setItem(ItemMapper.toItemDto(booking.getItem()));
        result.setBooker(UserMapper.toUserDto(booking.getBooker()));

        return result;

    }

    @Override
    public BookingDto approveBooking(Integer bookingId, Integer userId, boolean response) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Владелец не найден"));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ObjectNotFoundException("Бронь не найдена"));
        if (booking.getItem().getOwner().getId() != (userId)) {
            throw new ObjectNotFoundException("id вещи пользователя не совпадают с id владельца вещи");
        }
        Status status = booking.getStatus();
        if (!status.equals(Status.WAITING)) {
            throw new ValidationException("Статус нельзя изменить!");
        }
        if (response) {
            booking.setStatus(Status.APPROVED);
        }
        if (!response) {
            booking.setStatus(Status.REJECTED);
        }
        BookingDto bookingDto = BookingMapper.toBookingDto(bookingRepository.save(booking));
        bookingDto.setItem(ItemMapper.toItemDto(booking.getItem()));
        bookingDto.setBooker(UserMapper.toUserDto(booking.getBooker()));
        return bookingDto;
    }

    @Override
    public List<BookingDto> getBookingList(State state, Integer userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));

        List<Booking> bookings;
        LocalDateTime time = LocalDateTime.now();
        switch (state) {
            case PAST:
                bookings = bookingRepository.findByBooker_IdAndEndIsBefore(userId, time, sort);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBooker_IdAndStartIsAfter(userId, time, sort);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBooker_IdAndStartIsBeforeAndEndIsAfter(userId,
                        time, time, sort);
                break;
            case WAITING:
                bookings = bookingRepository.findByBooker_IdAndStatus(userId, Status.WAITING, sort);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBooker_IdAndStatus(userId, Status.REJECTED, sort);
                break;
            default:
                bookings = bookingRepository.findByBooker_Id(userId);
                Collections.sort(bookings, (booking1, booking2) -> booking2.getStart().compareTo(booking1.getStart()));
                break;
        }
        return getBookingDtos(bookings);
    }

    @Override
    public List<BookingDto> getBookingByItemOwner(Integer userId, State state) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));
        List<Booking> bookings;
        LocalDateTime time = LocalDateTime.now();
        switch (state) {
            case PAST:
                bookings = bookingRepository.findByItemOwnerIdAndEndIsBefore(userId, time, sort);
                break;
            case FUTURE:

                bookings = bookingRepository.findByItemOwnerIdAndStartIsAfter(userId, time, sort);
                break;
            case CURRENT:
                bookings = bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfter(userId,
                        time, time, sort);
                break;
            case WAITING:
                bookings = bookingRepository.findByItemOwnerIdAndStatus(userId, Status.WAITING, sort);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerIdAndStatus(userId, Status.REJECTED, sort);
                break;
            default:
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
                break;
        }
        return getBookingDtos(bookings);
    }

    private List<BookingDto> getBookingDtos(List<Booking> bookings) {
        List<BookingDto> result = new ArrayList<>();
        for (Booking booking : bookings) {
            var bookingDto = BookingMapper.toBookingDto(booking);
            bookingDto.setItem(ItemMapper.toItemDto(booking.getItem()));
            bookingDto.setBooker(UserMapper.toUserDto(booking.getBooker()));
            result.add(bookingDto);
        }
        return result;
    }

    private void validateTimeBooking(BookingDto bookingDto) {
        if (bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new ValidationException("Дата начала бронирования не может совпадать с датой окончания!");
        }
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new ValidationException("Дата начала бронирования не может быть позднее даты окончания бронирования!");
        }
    }
}
