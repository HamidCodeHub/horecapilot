package com.hamid.horecapilot.staff;

import com.hamid.horecapilot.common.EntityNotFoundException;
import com.hamid.horecapilot.staff.dto.EmployeeResponse;
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

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    EmployeeService service;

    @Test
    void postValid_returns201() throws Exception {
        when(service.create(any())).thenReturn(new EmployeeResponse(1L, "Mario", "Chef", new BigDecimal("25.0000"), true));

        mvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nome":"Mario","ruolo":"Chef","costoOrarioAziendale":25.0}
                        """))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void postInvalid_returns400() throws Exception {
        mvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"nome":"","ruolo":"","costoOrarioAziendale":null}
                        """))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getNotFound_returns404() throws Exception {
        when(service.getById(99L)).thenThrow(new EntityNotFoundException("Employee not found with id: 99"));

        mvc.perform(get("/api/employees/99"))
            .andExpect(status().isNotFound());
    }
}
