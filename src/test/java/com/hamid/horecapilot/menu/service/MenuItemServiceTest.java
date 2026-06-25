package com.hamid.horecapilot.menu.service;

import com.hamid.horecapilot.common.EntityNotFoundException;
import com.hamid.horecapilot.menu.dto.MenuItemCreateRequest;
import com.hamid.horecapilot.menu.dto.MenuItemResponse;
import com.hamid.horecapilot.menu.dto.MenuItemUpdateRequest;
import com.hamid.horecapilot.menu.model.MenuItem;
import com.hamid.horecapilot.menu.repository.MenuItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MenuItemServiceTest {

    @Mock
    MenuItemRepository repository;

    @InjectMocks
    MenuItemService service;

    @Test
    void create_savesAndReturnsResponse() {
        MenuItemCreateRequest req = new MenuItemCreateRequest("Pasta al Pomodoro", new BigDecimal("14.00"), "Primo");
        when(repository.save(any())).thenReturn(fixture(1L, "Pasta al Pomodoro", "14.00", "Primo", true));

        MenuItemResponse response = service.create(req);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.nome()).isEqualTo("Pasta al Pomodoro");
        assertThat(response.prezzoVendita()).isEqualByComparingTo(new BigDecimal("14.00"));
        assertThat(response.categoria()).isEqualTo("Primo");
        assertThat(response.attivo()).isTrue();
    }

    @Test
    void create_withNullCategoria_savesCorrectly() {
        MenuItemCreateRequest req = new MenuItemCreateRequest("Acqua", new BigDecimal("3.00"), null);
        when(repository.save(any())).thenReturn(fixture(2L, "Acqua", "3.00", null, true));

        MenuItemResponse response = service.create(req);

        assertThat(response.categoria()).isNull();
    }

    @Test
    void getById_returnsResponse_whenFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(fixture(1L, "Pasta al Pomodoro", "14.00", "Primo", true)));

        MenuItemResponse response = service.getById(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.nome()).isEqualTo("Pasta al Pomodoro");
    }

    @Test
    void getById_throwsEntityNotFoundException_whenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("99");
    }

    @Test
    void list_returnsAllMenuItems() {
        when(repository.findAll()).thenReturn(List.of(
            fixture(1L, "Pasta al Pomodoro", "14.00", "Primo", true),
            fixture(2L, "Pizza Margherita", "12.00", "Secondo", true)
        ));

        List<MenuItemResponse> result = service.list();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(MenuItemResponse::nome)
            .containsExactly("Pasta al Pomodoro", "Pizza Margherita");
    }

    @Test
    void update_updatesFieldsAndReturnsResponse() {
        when(repository.findById(1L)).thenReturn(Optional.of(fixture(1L, "Pasta al Pomodoro", "14.00", "Primo", true)));

        MenuItemResponse response = service.update(1L,
            new MenuItemUpdateRequest("Pasta Carbonara", new BigDecimal("16.00"), "Primo"));

        assertThat(response.nome()).isEqualTo("Pasta Carbonara");
        assertThat(response.prezzoVendita()).isEqualByComparingTo(new BigDecimal("16.00"));
    }

    @Test
    void update_throwsEntityNotFoundException_whenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L,
            new MenuItemUpdateRequest("X", new BigDecimal("10.00"), null)))
            .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deactivate_setsAttivoFalse() {
        MenuItem item = fixture(1L, "Pasta al Pomodoro", "14.00", "Primo", true);
        when(repository.findById(1L)).thenReturn(Optional.of(item));

        service.deactivate(1L);

        assertThat(item.isAttivo()).isFalse();
    }

    @Test
    void deactivate_throwsEntityNotFoundException_whenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deactivate(99L))
            .isInstanceOf(EntityNotFoundException.class);
    }

    private MenuItem fixture(Long id, String nome, String prezzoVendita, String categoria, boolean attivo) {
        MenuItem item = new MenuItem();
        item.setId(id);
        item.setRestaurantId(1L);
        item.setNome(nome);
        item.setPrezzoVendita(new BigDecimal(prezzoVendita));
        item.setCategoria(categoria);
        item.setAttivo(attivo);
        return item;
    }
}
