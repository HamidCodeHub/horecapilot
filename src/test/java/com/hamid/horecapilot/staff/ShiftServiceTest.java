package com.hamid.horecapilot.staff;

import com.hamid.horecapilot.common.BusinessRuleException;
import com.hamid.horecapilot.common.EntityNotFoundException;
import com.hamid.horecapilot.staff.dto.ShiftCreateRequest;
import com.hamid.horecapilot.staff.dto.ShiftResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShiftServiceTest {

    @Mock
    ShiftRepository shiftRepository;

    @Mock
    EmployeeRepository employeeRepository;

    @InjectMocks
    ShiftService service;

    @Test
    void create_computesCostoTurno_correctly() {
        // 6h × 12.3456 = 74.0736 → arrotondato a 74.07
        Employee emp = employee(1L, "12.3456", true);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(emp));

        Shift saved = shift(emp, LocalDate.of(2024, 1, 15), LocalTime.of(17, 0), LocalTime.of(23, 0));
        when(shiftRepository.save(any())).thenReturn(saved);

        ShiftCreateRequest request = new ShiftCreateRequest(1L, LocalDate.of(2024, 1, 15),
            LocalTime.of(17, 0), LocalTime.of(23, 0), "Chef");

        ShiftResponse response = service.create(request);

        assertThat(response.oreLavorate()).isEqualByComparingTo(new BigDecimal("6.00"));
        assertThat(response.costoTurno()).isEqualByComparingTo(new BigDecimal("74.07"));
    }

    @Test
    void create_throwsEntityNotFoundException_whenEmployeeNotFound() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        ShiftCreateRequest request = new ShiftCreateRequest(99L, LocalDate.of(2024, 1, 15),
            LocalTime.of(17, 0), LocalTime.of(23, 0), "Chef");

        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("99");
    }

    @Test
    void create_throwsBusinessRuleException_whenEmployeeInactive() {
        Employee emp = employee(1L, "12.3456", false);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(emp));

        ShiftCreateRequest request = new ShiftCreateRequest(1L, LocalDate.of(2024, 1, 15),
            LocalTime.of(17, 0), LocalTime.of(23, 0), "Chef");

        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    void create_throwsBusinessRuleException_whenOraInizioEqualsOraFine() {
        Employee emp = employee(1L, "12.3456", true);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(emp));

        ShiftCreateRequest request = new ShiftCreateRequest(1L, LocalDate.of(2024, 1, 15),
            LocalTime.of(17, 0), LocalTime.of(17, 0), "Chef");

        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(BusinessRuleException.class);
    }

    private Employee employee(Long id, String costo, boolean attivo) {
        Employee e = new Employee();
        e.setId(id);
        e.setRestaurantId(1L);
        e.setNome("Mario");
        e.setRuolo("Chef");
        e.setCostoOrarioAziendale(new BigDecimal(costo));
        e.setAttivo(attivo);
        return e;
    }

    private Shift shift(Employee emp, LocalDate data, LocalTime oraInizio, LocalTime oraFine) {
        Shift s = new Shift();
        s.setEmployee(emp);
        s.setData(data);
        s.setOraInizio(oraInizio);
        s.setOraFine(oraFine);
        s.setRuolo("Chef");
        s.setRestaurantId(1L);
        return s;
    }
}
