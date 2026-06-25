package com.hamid.horecapilot.menu.controller;

import com.hamid.horecapilot.common.EntityNotFoundException;
import com.hamid.horecapilot.menu.dto.MenuItemResponse;
import com.hamid.horecapilot.menu.service.MenuItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MenuItemController.class)
class MenuItemControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    MenuItemService service;

    @Test
    void postValid_returns201() throws Exception {
        when(service.create(any())).thenReturn(
            new MenuItemResponse(1L, "Pasta al Pomodoro", new BigDecimal("14.00"), "Primo", true));

        mvc.perform(post("/api/menu-items")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nome":"Pasta al Pomodoro","prezzoVendita":14.00,"categoria":"Primo"}
                        """))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void postInvalid_returns400() throws Exception {
        mvc.perform(post("/api/menu-items")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nome":"","prezzoVendita":null}
                        """))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getNotFound_returns404() throws Exception {
        when(service.getById(99L)).thenThrow(new EntityNotFoundException("MenuItem not found with id: 99"));

        mvc.perform(get("/api/menu-items/99"))
            .andExpect(status().isNotFound());
    }
}
