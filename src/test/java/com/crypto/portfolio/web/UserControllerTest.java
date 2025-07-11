package com.crypto.portfolio.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.crypto.portfolio.PortfolioApplication;
import com.crypto.portfolio.repositories.UserRepository;
import com.crypto.portfolio.entities.User;

import com.crypto.portfolio.utils.user.UserRole;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(classes = PortfolioApplication.class, webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class UserControllerTest {

    @MockitoBean
    UserRepository repository;

    @Autowired
    MockMvc mvc;

    private User user;
    private User anotherUser;
    private List holders;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(20);
        user.setName("Richard");
        user.setComment("The Cat");

        anotherUser = new User();
        anotherUser.setId(13);
        anotherUser.setName("Levi");
        anotherUser.setComment("Strauss");
        anotherUser.setTelegramId(1234567l);

        holders = new ArrayList();
        holders.add(user);
        holders.add(anotherUser);

        when(repository.save(user)).thenReturn(user);
        when(repository.findById(20)).thenReturn(Optional.of(user));
        when(repository.findByName("Levi")).thenReturn(Optional.of(anotherUser));
        when(repository.findByTelegramId(1234567l)).thenReturn(Optional.of(anotherUser));
        when(repository.findAll()).thenReturn(holders);
    }

    @Test
    public void testGetUserById() throws Exception {
        mvc.perform(get("/users/{id}", 20))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("user_name", Matchers.is("Richard")));
    }

    @Test
    public void testGetUserByTelegramId() throws Exception {
        mvc.perform(get("/users/telegramId/{telegramId}", 1234567))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id", Matchers.is(13)));
    }

    @Test
    public void testGetHolderByUserName() throws Exception {
        mvc.perform(get("/users/userName/{userName}", "Levi"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("comment", Matchers.is("Strauss")));
    }

    @Test
    public void testAddUser() throws Exception {
        mvc.perform(post("/users/add")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id", Matchers.is(20)));
    }

    @Test
    public void testUpdateUser() throws Exception {
        user.setUserRole(UserRole.CLIENT);
        mvc.perform(put("/users/update/{id}", 20)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("user_role", Matchers.is(UserRole.CLIENT.toString())));
    }

    @Test
    public void testDeleteHolderById() throws Exception {
        mvc.perform(delete("/users/delete/20"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetUsers() throws Exception {
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[{\"user_name\":\"Richard\", " +
                        "\"comment\":\"The Cat\"}, {\"user_name\":\"Levi\", \"comment\":\"Strauss\"}]"));
    }

    @Test
    public void testGetUserByIdDoesNotExist() throws Exception {
        mvc.perform(get("/users/{id}", 21))
                .andExpect(status().isNotFound())
                .andExpect(content().string("UserNotFound"));
    }

    @Test
    public void testGetUserByTelegramIdDoesNotExist() throws Exception {
        mvc.perform(get("/users/telegramId/{telegramId}", 33))
                .andExpect(status().isNotFound())
                .andExpect(content().string("UserNotFound"));
    }

    @Test
    public void testGetUserByNameDoesNotExist() throws Exception {
        mvc.perform(get("/users/userName/{userName}", "John"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("UserNotFound"));
    }

    @Test
    public void testAddUserAlreadyExist() throws Exception {
        when(repository.findByTelegramId(1234567l)).thenReturn(Optional.of(anotherUser));
        mvc.perform(post("/users/add")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(anotherUser))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    public void testUpdateUserDoesNotExist() throws Exception {
        user.setTelegramId(-12345l);
        mvc.perform(put("/users/update/{id}", 24)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("UserNotFound"));
    }
}
