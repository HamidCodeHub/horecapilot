package com.hamid.horecapilot.sales.service;

import com.hamid.horecapilot.common.EntityNotFoundException;
import com.hamid.horecapilot.sales.dto.DailySalesResponse;
import com.hamid.horecapilot.sales.dto.DailySalesUpsertRequest;
import com.hamid.horecapilot.sales.model.DailySales;
import com.hamid.horecapilot.sales.repository.DailySalesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DailySalesServiceTest {

    @Mock
    DailySalesRepository repository;

    @InjectMocks
    DailySalesService service;

    private static final LocalDate DATE = LocalDate.of(2024, 6, 15);

    @Test
    void upsert_absentDate_createsNewRow() {
        when(repository.findByRestaurantIdAndData(any(), any())).thenReturn(Optional.empty());
        when(repository.save(any())).thenReturn(fixture(1L, DATE, "1500.00", 40));

        DailySalesUpsertResult result = service.upsert(DATE, new DailySalesUpsertRequest(
            new BigDecimal("1500.00"), 40));

        assertThat(result.created()).isTrue();
        assertThat(result.sales().id()).isEqualTo(1L);
        assertThat(result.sales().data()).isEqualTo(DATE);
    }

    @Test
    void upsert_existingDate_updatesValuesNotCreatesNewRow() {
        DailySales existing = fixture(1L, DATE, "1000.00", 30);
        when(repository.findByRestaurantIdAndData(any(), any())).thenReturn(Optional.of(existing));

        DailySalesUpsertResult result = service.upsert(DATE, new DailySalesUpsertRequest(
            new BigDecimal("1800.00"), 50));

        assertThat(result.created()).isFalse();
        assertThat(result.sales().fatturato()).isEqualByComparingTo(new BigDecimal("1800.00"));
        assertThat(result.sales().coperti()).isEqualTo(50);
    }

    @Test
    void getByDate_absentDate_throwsEntityNotFoundException() {
        when(repository.findByRestaurantIdAndData(any(), any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByDate(DATE))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining(DATE.toString());
    }

    private DailySales fixture(Long id, LocalDate data, String fatturato, int coperti) {
        DailySales ds = new DailySales();
        ds.setId(id);
        ds.setRestaurantId(1L);
        ds.setData(data);
        ds.setFatturato(new BigDecimal(fatturato));
        ds.setCoperti(coperti);
        return ds;
    }
}
