package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findItemByOwnerId(int ownerId);

    @Query("select ie from Item ie " +
            "where (upper(ie.name) like upper(concat('%', ?1, '%')) " +
            "or upper(ie.description) like upper(concat('%', ?1, '%')))" +
            "and ie.available = true")
    List<Item> search(String text);

}
