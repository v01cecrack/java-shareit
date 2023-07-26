package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.error.NotAvailableException;
import ru.practicum.shareit.error.ObjectNotFoundException;
import ru.practicum.shareit.error.ValidationException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.dto.ItemMapper.toItem;
import static ru.practicum.shareit.item.dto.ItemMapper.toItemDto;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;


    @Override
    public ItemDto addItem(ItemDto itemDto, Integer userId) {
        validateItem(userId, itemDto);
        User user = userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден"));
        Item item = toItem(itemDto, user);
        itemRepository.save(item);
        itemDto = toItemDto(item);
        return itemDto;
    }

    @Override
    public ItemDto editItem(ItemDto itemDto, Integer userId, Integer itemId) {
        itemDto.setId(itemId);
        Item oldItem = itemRepository.findById(itemId).orElseThrow(() -> new ObjectNotFoundException("Вещь не найдена"));
        if (!(oldItem.getOwner().getId() == userId)) {
            throw new ObjectNotFoundException("User is not found");
        }
        if (itemDto.getName() == null) {
            itemDto.setName(oldItem.getName());
        }
        if (itemDto.getDescription() == null) {
            itemDto.setDescription(oldItem.getDescription());
        }
        if (itemDto.getAvailable() == null) {
            itemDto.setAvailable(oldItem.getAvailable());
        }
        Item item = toItem(itemDto, userRepository.findById(userId).orElseThrow(() -> new ObjectNotFoundException("Пользователь не найден")));
        itemRepository.save(item);
        return itemDto;
    }


    @Override
    public ItemDto viewItem(Integer itemId, Integer userId) {
        var item = itemRepository.findById(itemId).orElseThrow(() ->
                new ObjectNotFoundException("Вещь не найдена!"));
        List<Comment> comments = commentRepository.findCommentsByItem_Id(itemId);
        var itemDto = ItemMapper.toItemDto(item);
        List<CommentDto> commentDto = new ArrayList<>();
        for (Comment comment : comments) {
            commentDto.add(CommentMapper.tocommentDto(comment));
        }
        itemDto.setComments(commentDto);
        if (userId == item.getOwner().getId()) {
            var bookings = bookingRepository.findBookingByItem_IdAndStatus(item.getId(), Status.APPROVED);

            if (bookings.size() != 0) {
                Optional<BookingDto> lastBookingDto = bookings.stream()
                        .sorted(Comparator.comparing(Booking::getStart).reversed())
                        .filter(booking -> booking.getStart().isBefore(LocalDateTime.now()))
                        .map(BookingMapper::toBookingDto)
                        .findFirst();
                lastBookingDto.ifPresent(itemDto::setLastBooking);
                Optional<BookingDto> nextBookingDto = bookings.stream()
                        .sorted(Comparator.comparing(Booking::getStart))
                        .filter(booking -> booking.getStart().isAfter(LocalDateTime.now()))
                        .map(BookingMapper::toBookingDto)
                        .findFirst();
                nextBookingDto.ifPresent(itemDto::setNextBooking);
            }
        }
        return itemDto;
    }

    @Override
    public List<ItemDto> getItems(Integer userId) {
        List<ItemDto> itemDtos = new ArrayList<>();
        List<Item> items = itemRepository.findItemByOwnerId(userId);

        items = items.stream()
                .sorted(Comparator.comparingInt(Item::getId))
                .collect(Collectors.toList());
        List<Booking> bookingsList = bookingRepository.findBookingByStatus(Status.APPROVED);
        for (Item item : items) {
            var itemDto = ItemMapper.toItemDto(item);
            List<Booking> bookings = new ArrayList<>();
            if (userId == item.getOwner().getId()) {
                for (Booking booking : bookingsList) {
                    if (booking.getItem().getId() == item.getId()) {
                        bookings.add(booking);
                    }
                }
                if (bookings.size() > 0) {
                    bookings = bookings.stream()
                            .sorted(Comparator.comparing(Booking::getStart).reversed())
                            .collect(Collectors.toList());

                    for (Booking booking : bookings) {
                        if (booking.getStart().isBefore(LocalDateTime.now())

                        ) {
                            itemDto.setLastBooking(BookingMapper.toBookingDto(booking));
                            break;
                        }
                    }
                    bookings = bookings.stream()
                            .sorted(Comparator.comparing(Booking::getStart))
                            .collect(Collectors.toList());
                    for (Booking booking : bookings) {
                        if (booking.getStart().isAfter(LocalDateTime.now())) {
                            itemDto.setNextBooking(BookingMapper.toBookingDto(booking));
                            break;
                        }
                    }
                }
            }

            List<Comment> comments = commentRepository.findCommentsByItem_Id(item.getId());
            List<CommentDto> commenstDto = new ArrayList<>();
            for (Comment comment : comments) {
                commenstDto.add(CommentMapper.tocommentDto(comment));
            }
            itemDto.setComments(commenstDto);

            itemDtos.add(itemDto);
        }
        return itemDtos;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isEmpty()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(int userId, int itemId, CommentDto commentDto) {
        var itemOptional = itemRepository.findById(itemId);

        if (itemOptional.isEmpty()) {
            throw new ObjectNotFoundException("Такой вещи нет");
        }
        var item = itemOptional.get();

        var userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new ObjectNotFoundException("Такого пользователя нет");
        }
        var user = userOptional.get();

        List<Booking> bookings = bookingRepository.findBookingByItem_Id(itemId);
        boolean isExist = false;
        for (Booking booking : bookings) {
            if (booking.getBooker().getId() == userId
                    && booking.getStart().isBefore(LocalDateTime.now())
                    && booking.getStatus().equals(Status.APPROVED)) {
                isExist = true;
                break;
            }
        }
        if (!isExist) {
            throw new ValidationException("Этой вещью не пользовался данный пользователь!");
        }

        Comment comment = CommentMapper.toComment(commentDto);
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        var result = CommentMapper.tocommentDto(commentRepository.save(comment));
        return result;
    }

    private void validateItem(Integer userId, ItemDto itemDto) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new ObjectNotFoundException("Пользователь не найден");
        }
        if ((!itemDto.getAvailable())) {
            throw new NotAvailableException("Эта вещь недоступна");
        }
    }

}
