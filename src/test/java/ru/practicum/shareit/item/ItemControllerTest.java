package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;
    private ItemDto itemDto;

    @BeforeEach
    @Test
    void setItemDto() {
        itemDto = ItemDto.builder()
                .name("name")
                .description("description")
                .available(true)
                .build();
    }

    @SneakyThrows
    @Test
    void addItem() {
        var userId = 0;
        when(itemService.addItem(itemDto, userId)).thenReturn(itemDto);
        String contentAsString = mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(itemDto), contentAsString);
        verify(itemService).addItem(itemDto, userId);
    }

    @SneakyThrows
    @Test
    void updateItem() {
        var itemId = 0;
        var userId = 0;
        when(itemService.editItem(itemDto, userId)).thenReturn(itemDto);
        String contentAsString = mockMvc.perform(patch("/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(itemDto), contentAsString);
        verify(itemService, times(1)).editItem(itemDto, userId);
    }

    @SneakyThrows
    @Test
    void getItemById() {
        var itemId = 0;
        var userId = 0;
        when(itemService.viewItem(itemId, userId)).thenReturn(itemDto);
        String contentAsString = mockMvc.perform(get("/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), contentAsString);
        verify(itemService, atLeast(1)).viewItem(itemId, userId);
    }

    @Test
    void getItemsByUserId() {
    }


    @SneakyThrows
    @Test
    void addComment() {
        var userId = 1;
        var itemId = itemDto.getId();
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("text")
                .authorName("authorName")
                .created(LocalDateTime.now())
                .build();
        when(itemService.addComment(anyInt(), anyInt(), any(CommentDto.class))).thenReturn(commentDto);

        String contentAsString = mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(commentDto), contentAsString);

        verify(itemService, times(1)).addComment(userId, itemId, commentDto);
        verifyNoMoreInteractions(itemService);
    }

    @Test
    void getItems() throws Exception {
        int userId = 123;
        int from = 0;
        int size = 20;

        List<ItemDto> itemDtos = new ArrayList<>();
        itemDtos.add(ItemDto.builder().id(1).name("Item 1").description("This is item 1").available(true).build());
        itemDtos.add(ItemDto.builder().id(2).name("Item 2").description("This is item 2").available(true).build());

        when(itemService.getItems(userId, from, size)).thenReturn(itemDtos);

        mockMvc.perform(get("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());

        verify(itemService, atLeastOnce()).getItems(userId, from, size);
    }

    @Test
    void searchItems() throws Exception {
        int from = 0;
        int size = 20;
        String searchText = "item";

        List<ItemDto> itemDtos = new ArrayList<>();
        itemDtos.add(ItemDto.builder().id(1).name("Item 1").description("This is item 1").available(true).build());
        itemDtos.add(ItemDto.builder().id(2).name("Item 2").description("This is item 2").available(true).build());

        when(itemService.searchItems(searchText, from, size)).thenReturn(itemDtos);

        mockMvc.perform(get("/items/search")
                        .param("text", searchText)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemService, atLeastOnce()).searchItems(searchText, from, size);
    }


}
