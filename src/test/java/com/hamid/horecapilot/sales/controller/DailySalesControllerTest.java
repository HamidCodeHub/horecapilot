package com.hamid.horecapilot.sales.controller;

import com.hamid.horecapilot.common.EntityNotFoundException;
import com.hamid.horecapilot.sales.dto.DailySalesResponse;
import com.hamid.horecapilot.sales.service.DailySalesService;
import com.hamid.horecapilot.sales.service.DailySalesUpsertResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DailySalesController.class)
class DailySalesControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    DailySalesService service;

    private static final LocalDate DATE = LocalDate.of(2024, 6, 15);
    private static final DailySalesResponse RESPONSE =
        new DailySalesResponse(1L, DATE, new BigDecimal("1500.00"), 40);

    @Test
    void putNewDate_returns201WithLocation() throws Exception {
        when(service.upsert(any(), any())).thenReturn(new DailySalesUpsertResult(true, RESPONSE));

        mvc.perform(put("/api/daily-sales/2024-06-15")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"fatturato":1500.00,"coperti":40}
                        """))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void putExistingDate_returns200() throws Exception {
        when(service.upsert(any(), any())).thenReturn(new DailySalesUpsertResult(false, RESPONSE));

        mvc.perform(put("/api/daily-sales/2024-06-15")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"fatturato":1500.00,"coperti":40}
                        """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fatturato").value(1500.00));
    }

    @Test
    void putNegativeFatturato_returns400() throws Exception {
        mvc.perform(put("/api/daily-sales/2024-06-15")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"fatturato":-1.00,"coperti":40}
                        """))
            .andExpect(status().isBadRequest());
    }

    @Test
    void putMissingCoperti_returns400() throws Exception {
        mvc.perform(put("/api/daily-sales/2024-06-15")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"fatturato":1500.00}
                        """))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getAbsentDate_returns404() throws Exception {
        when(service.getByDate(any())).thenThrow(new EntityNotFoundException("DailySales not found for date: 2024-06-15"));

        mvc.perform(get("/api/daily-sales/2024-06-15"))
            .andExpect(status().isNotFound());
    }
}
