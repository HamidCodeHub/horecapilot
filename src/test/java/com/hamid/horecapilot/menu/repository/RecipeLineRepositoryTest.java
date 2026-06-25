package com.hamid.horecapilot.menu.repository;

import com.hamid.horecapilot.menu.model.Ingredient;
import com.hamid.horecapilot.menu.model.MenuItem;
import com.hamid.horecapilot.menu.model.RecipeLine;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.AutoConfigureTestEntityManager;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestEntityManager
@TestPropertySource(properties = {
    "spring.flyway.enabled=false",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class RecipeLineRepositoryTest {

    @Autowired TestEntityManager em;
    @Autowired RecipeLineRepository repository;

    @Test
    void findByMenuItemId_returnsOnlyLinesForThatItem() {
        MenuItem pasta = saveMenuItem("Pasta al Pomodoro", "14.00");
        MenuItem pizza = saveMenuItem("Pizza Margherita", "12.00");
        Ingredient farina = saveIngredient("Farina 00", "kg", "1.80");
        Ingredient pomodoro = saveIngredient("Pomodoro", "kg", "2.50");

        saveRecipeLine(pasta, farina, "0.08");
        saveRecipeLine(pasta, pomodoro, "0.10");
        saveRecipeLine(pizza, farina, "0.15");
        em.flush();
        em.clear();

        List<RecipeLine> result = repository.findByMenuItemId(pasta.getId());

        assertThat(result).hasSize(2)
            .allMatch(l -> l.getMenuItem().getId().equals(pasta.getId()));
    }

    @Test
    void findByMenuItemId_returnsEmpty_whenNoLines() {
        MenuItem pasta = saveMenuItem("Pasta al Pomodoro", "14.00");
        em.flush();
        em.clear();

        List<RecipeLine> result = repository.findByMenuItemId(pasta.getId());

        assertThat(result).isEmpty();
    }

    @Test
    void findByMenuItemIdAndIngredientId_returnsLine_whenExists() {
        MenuItem pasta = saveMenuItem("Pasta al Pomodoro", "14.00");
        Ingredient farina = saveIngredient("Farina 00", "kg", "1.80");
        saveRecipeLine(pasta, farina, "0.08");
        em.flush();
        em.clear();

        Optional<RecipeLine> found = repository.findByMenuItemIdAndIngredientId(pasta.getId(), farina.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getQuantita()).isEqualByComparingTo(new BigDecimal("0.08"));
    }

    @Test
    void findByMenuItemIdAndIngredientId_returnsEmpty_whenNotFound() {
        MenuItem pasta = saveMenuItem("Pasta al Pomodoro", "14.00");
        Ingredient farina = saveIngredient("Farina 00", "kg", "1.80");
        em.flush();
        em.clear();

        Optional<RecipeLine> found = repository.findByMenuItemIdAndIngredientId(pasta.getId(), farina.getId());

        assertThat(found).isEmpty();
    }

    @Test
    void findByMenuItemIdAndIngredientId_returnsCorrectLine_whenMultipleExist() {
        MenuItem pasta = saveMenuItem("Pasta al Pomodoro", "14.00");
        Ingredient farina = saveIngredient("Farina 00", "kg", "1.80");
        Ingredient pomodoro = saveIngredient("Pomodoro", "kg", "2.50");
        saveRecipeLine(pasta, farina, "0.08");
        saveRecipeLine(pasta, pomodoro, "0.10");
        em.flush();
        em.clear();

        Optional<RecipeLine> found = repository.findByMenuItemIdAndIngredientId(pasta.getId(), pomodoro.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getQuantita()).isEqualByComparingTo(new BigDecimal("0.10"));
    }

    private MenuItem saveMenuItem(String nome, String prezzoVendita) {
        MenuItem item = new MenuItem();
        item.setRestaurantId(1L);
        item.setNome(nome);
        item.setPrezzoVendita(new BigDecimal(prezzoVendita));
        item.setAttivo(true);
        return em.persist(item);
    }

    private Ingredient saveIngredient(String nome, String unita, String costo) {
        Ingredient i = new Ingredient();
        i.setRestaurantId(1L);
        i.setNome(nome);
        i.setUnita(unita);
        i.setCostoUnitario(new BigDecimal(costo));
        i.setAttivo(true);
        return em.persist(i);
    }

    private void saveRecipeLine(MenuItem item, Ingredient ingredient, String quantita) {
        RecipeLine rl = new RecipeLine();
        rl.setMenuItem(item);
        rl.setIngredient(ingredient);
        rl.setQuantita(new BigDecimal(quantita));
        em.persist(rl);
    }
}
