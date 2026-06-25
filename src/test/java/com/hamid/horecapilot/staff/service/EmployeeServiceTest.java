package com.hamid.horecapilot.staff.service;

import com.hamid.horecapilot.common.EntityNotFoundException;
import com.hamid.horecapilot.staff.model.Employee;
import com.hamid.horecapilot.staff.repository.EmployeeRepository;
import com.hamid.horecapilot.staff.dto.EmployeeCreateRequest;
import com.hamid.horecapilot.staff.dto.EmployeeResponse;
import com.hamid.horecapilot.staff.dto.EmployeeUpdateRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    EmployeeRepository repository;

    @InjectMocks
    EmployeeService service;

    @Test
    void create_savesAndReturnsResponse() {
        EmployeeCreateRequest request = new EmployeeCreateRequest("Mario", "Chef", new BigDecimal("25.0000"));
        when(repository.save(any())).thenReturn(fixture(1L, "Mario", "Chef", new BigDecimal("25.0000"), true));

        EmployeeResponse response = service.create(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.nome()).isEqualTo("Mario");
        assertThat(response.attivo()).isTrue();
    }

    @Test
    void getById_returnsResponse_whenFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(fixture(1L, "Mario", "Chef", new BigDecimal("25.0000"), true)));

        EmployeeResponse response = service.getById(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.nome()).isEqualTo("Mario");
    }

    @Test
    void getById_throwsEntityNotFoundException_whenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("99");
    }

    @Test
    void update_updatesFieldsAndReturnsResponse() {
        when(repository.findById(1L)).thenReturn(Optional.of(fixture(1L, "Mario", "Chef", new BigDecimal("25.0000"), true)));

        EmployeeResponse response = service.update(1L, new EmployeeUpdateRequest("Luigi", "Cameriere", new BigDecimal("20.0000")));

        assertThat(response.nome()).isEqualTo("Luigi");
        assertThat(response.ruolo()).isEqualTo("Cameriere");
        assertThat(response.costoOrarioAziendale()).isEqualByComparingTo(new BigDecimal("20.0000"));
    }

    @Test
    void deactivate_setsAttivoFalse() {
        Employee employee = fixture(1L, "Mario", "Chef", new BigDecimal("25.0000"), true);
        when(repository.findById(1L)).thenReturn(Optional.of(employee));

        service.deactivate(1L);

        assertThat(employee.isAttivo()).isFalse();
    }

    private Employee fixture(Long id, String nome, String ruolo, BigDecimal costo, boolean attivo) {
        Employee e = new Employee();
        e.setId(id);
        e.setRestaurantId(1L);
        e.setNome(nome);
        e.setRuolo(ruolo);
        e.setCostoOrarioAziendale(costo);
        e.setAttivo(attivo);
        return e;
    }
}
