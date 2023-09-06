package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    Page<Item> findItemByOwnerIdOrderById(int ownerId, Pageable pageable);

    List<Item> findItemByOwnerId(int ownerId);

    @Query("select ie from Item ie " +
            "where (upper(ie.name) like upper(concat('%', ?1, '%')) " +
            "or upper(ie.description) like upper(concat('%', ?1, '%')))" +
            "and ie.available = true")
    Page<Item> search(String text, Pageable pageable);

    List<Item> findItemsByRequestIdIn(Iterable requestId);

    List<Item> findItemsByRequestId(long requestId);

}
