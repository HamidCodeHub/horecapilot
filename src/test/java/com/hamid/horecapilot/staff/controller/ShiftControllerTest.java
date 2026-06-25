package com.hamid.horecapilot.staff.controller;

import com.hamid.horecapilot.common.EntityNotFoundException;
import com.hamid.horecapilot.staff.dto.ShiftResponse;
import com.hamid.horecapilot.staff.service.ShiftService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShiftController.class)
class ShiftControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    ShiftService service;

    @Test
    void postValid_returns201() throws Exception {
        ShiftResponse response = new ShiftResponse(1L, 1L, "Mario",
            LocalDate.of(2024, 1, 15), LocalTime.of(17, 0), LocalTime.of(23, 0),
            "Chef", new BigDecimal("6.00"), new BigDecimal("74.07"));
        when(service.create(any())).thenReturn(response);

        mvc.perform(post("/api/shifts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"employeeId":1,"data":"2024-01-15","oraInizio":"17:00","oraFine":"23:00","ruolo":"Chef"}
                        """))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void postInvalid_returns400() throws Exception {
        mvc.perform(post("/api/shifts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"employeeId":null,"data":null,"oraInizio":null,"oraFine":null,"ruolo":""}
                        """))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getNotFound_returns404() throws Exception {
        when(service.getById(99L)).thenThrow(new EntityNotFoundException("Shift not found with id: 99"));

        mvc.perform(get("/api/shifts/99"))
            .andExpect(status().isNotFound());
    }
}
