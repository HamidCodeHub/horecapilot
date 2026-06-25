package com.hamid.horecapilot.menu.service;

import com.hamid.horecapilot.common.EntityNotFoundException;
import com.hamid.horecapilot.menu.dto.IngredientCreateRequest;
import com.hamid.horecapilot.menu.dto.IngredientResponse;
import com.hamid.horecapilot.menu.dto.IngredientUpdateRequest;
import com.hamid.horecapilot.menu.model.Ingredient;
import com.hamid.horecapilot.menu.repository.IngredientRepository;
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
class IngredientServiceTest {

    @Mock
    IngredientRepository repository;

    @InjectMocks
    IngredientService service;

    @Test
    void create_savesAndReturnsResponse() {
        IngredientCreateRequest req = new IngredientCreateRequest("Farina 00", "kg", new BigDecimal("1.8000"));
        when(repository.save(any())).thenReturn(fixture(1L, "Farina 00", "kg", "1.8000", true));

        IngredientResponse response = service.create(req);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.nome()).isEqualTo("Farina 00");
        assertThat(response.unita()).isEqualTo("kg");
        assertThat(response.costoUnitario()).isEqualByComparingTo(new BigDecimal("1.8000"));
        assertThat(response.attivo()).isTrue();
    }

    @Test
    void getById_returnsResponse_whenFound() {
        when(repository.findById(1L)).thenReturn(Optional.of(fixture(1L, "Farina 00", "kg", "1.8000", true)));

        IngredientResponse response = service.getById(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.nome()).isEqualTo("Farina 00");
    }

    @Test
    void getById_throwsEntityNotFoundException_whenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99L))
            .isInstanceOf(EntityNotFoundException.class)
            .hasMessageContaining("99");
    }

    @Test
    void list_returnsAllIngredients() {
        when(repository.findAll()).thenReturn(List.of(
            fixture(1L, "Farina 00", "kg", "1.8000", true),
            fixture(2L, "Pomodoro", "kg", "2.5000", true)
        ));

        List<IngredientResponse> result = service.list();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(IngredientResponse::nome)
            .containsExactly("Farina 00", "Pomodoro");
    }

    @Test
    void update_updatesFieldsAndReturnsResponse() {
        when(repository.findById(1L)).thenReturn(Optional.of(fixture(1L, "Farina 00", "kg", "1.8000", true)));

        IngredientResponse response = service.update(1L,
            new IngredientUpdateRequest("Farina Manitoba", "kg", new BigDecimal("2.2000")));

        assertThat(response.nome()).isEqualTo("Farina Manitoba");
        assertThat(response.costoUnitario()).isEqualByComparingTo(new BigDecimal("2.2000"));
    }

    @Test
    void update_throwsEntityNotFoundException_whenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(99L,
            new IngredientUpdateRequest("X", "kg", new BigDecimal("1.0000"))))
            .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deactivate_setsAttivoFalse() {
        Ingredient ingredient = fixture(1L, "Farina 00", "kg", "1.8000", true);
        when(repository.findById(1L)).thenReturn(Optional.of(ingredient));

        service.deactivate(1L);

        assertThat(ingredient.isAttivo()).isFalse();
    }

    @Test
    void deactivate_throwsEntityNotFoundException_whenNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.deactivate(99L))
            .isInstanceOf(EntityNotFoundException.class);
    }

    private Ingredient fixture(Long id, String nome, String unita, String costo, boolean attivo) {
        Ingredient i = new Ingredient();
        i.setId(id);
        i.setRestaurantId(1L);
        i.setNome(nome);
        i.setUnita(unita);
        i.setCostoUnitario(new BigDecimal(costo));
        i.setAttivo(attivo);
        return i;
    }
}
