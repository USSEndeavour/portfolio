package com.crypto.portfolio.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.crypto.portfolio.PortfolioApplication;
import com.crypto.portfolio.repositories.UserRepository;
import com.crypto.portfolio.entities.User;

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

        holders = new ArrayList();
        holders.add(user);
        holders.add(anotherUser);

        when(repository.save(user)).thenReturn(user);
        when(repository.findById(20)).thenReturn(Optional.of(user));
        when(repository.findAll()).thenReturn(holders);
    }

    @Test
    public void testAddHolder() throws Exception {
        mvc.perform(post("/users/add")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id", Matchers.is(20)));
    }

    @Test
    public void testDeleteHolderById() throws Exception {
        mvc.perform(delete("/users/delete/20"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void testGetHolders() throws Exception {
        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[{\"user_name\":\"Richard\", " +
                        "\"comment\":\"The Cat\"}, {\"user_name\":\"Levi\", \"comment\":\"Strauss\"}]"));
    }
}
