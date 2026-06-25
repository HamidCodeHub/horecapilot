package com.hamid.horecapilot.menu.controller;

import com.hamid.horecapilot.common.EntityNotFoundException;
import com.hamid.horecapilot.menu.dto.IngredientResponse;
import com.hamid.horecapilot.menu.service.IngredientService;
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

@WebMvcTest(IngredientController.class)
class IngredientControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    IngredientService service;

    @Test
    void postValid_returns201() throws Exception {
        when(service.create(any())).thenReturn(
            new IngredientResponse(1L, "Farina", "kg", new BigDecimal("1.8000"), true));

        mvc.perform(post("/api/ingredients")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nome":"Farina","unita":"kg","costoUnitario":1.8}
                        """))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void postInvalid_returns400() throws Exception {
        mvc.perform(post("/api/ingredients")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nome":"","unita":"","costoUnitario":null}
                        """))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getNotFound_returns404() throws Exception {
        when(service.getById(99L)).thenThrow(new EntityNotFoundException("Ingredient not found with id: 99"));

        mvc.perform(get("/api/ingredients/99"))
            .andExpect(status().isNotFound());
    }
}
