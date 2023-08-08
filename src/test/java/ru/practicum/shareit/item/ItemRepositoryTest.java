package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void search_ReturnsMatchingItems() {
        User owner = User.builder().id(1).name("kostya").email("kostya@mail.ru").build();
        Item item = Item.builder().id(1)
                .name("Item 1")
                .description("description")
                .available(true)
                .owner(owner)
                .build();
        itemRepository.save(item);
        String searchText = "Item";
        Pageable pageable = PageRequest.of(0, 20); // Настройка страницы

        Page<Item> searchResult = itemRepository.search(searchText, pageable);

        assertEquals(1, searchResult.getTotalElements());
    }
}
