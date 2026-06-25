package com.hamid.horecapilot.sales.repository;

import com.hamid.horecapilot.common.Tenant;
import com.hamid.horecapilot.sales.model.DailySales;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.AutoConfigureTestEntityManager;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestEntityManager
@TestPropertySource(properties = {
    "spring.flyway.enabled=false",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class DailySalesRepositoryTest {

    @Autowired TestEntityManager em;
    @Autowired DailySalesRepository repository;

    private static final Long RESTAURANT_ID = Tenant.DEFAULT_RESTAURANT_ID;

    @Test
    void findByRestaurantIdAndData_returnsCorrectRecord() {
        save(LocalDate.of(2024, 6, 15), "1500.00", 45);
        em.flush();
        em.clear();

        Optional<DailySales> found = repository.findByRestaurantIdAndData(RESTAURANT_ID, LocalDate.of(2024, 6, 15));

        assertThat(found).isPresent();
        assertThat(found.get().getFatturato()).isEqualByComparingTo(new BigDecimal("1500.00"));
        assertThat(found.get().getCoperti()).isEqualTo(45);
    }

    @Test
    void findByRestaurantIdAndData_returnsEmpty_whenNoRecord() {
        Optional<DailySales> found = repository.findByRestaurantIdAndData(RESTAURANT_ID, LocalDate.of(2024, 1, 1));
        assertThat(found).isEmpty();
    }

    @Test
    void search_withNullDates_returnsAll() {
        save(LocalDate.of(2024, 6, 10), "1000.00", 30);
        save(LocalDate.of(2024, 6, 20), "2000.00", 60);
        em.flush();
        em.clear();

        List<DailySales> result = repository.search(RESTAURANT_ID, null, null);

        assertThat(result).hasSize(2);
    }

    @Test
    void search_withDateRange_filtersCorrectly() {
        save(LocalDate.of(2024, 6, 5), "800.00", 25);
        save(LocalDate.of(2024, 6, 15), "1500.00", 45);
        save(LocalDate.of(2024, 6, 25), "1200.00", 35);
        em.flush();
        em.clear();

        List<DailySales> result = repository.search(
            RESTAURANT_ID,
            LocalDate.of(2024, 6, 10),
            LocalDate.of(2024, 6, 20)
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getData()).isEqualTo(LocalDate.of(2024, 6, 15));
        assertThat(result.get(0).getFatturato()).isEqualByComparingTo(new BigDecimal("1500.00"));
    }

    @Test
    void search_withFromOnly_returnsFromDateInclusive() {
        save(LocalDate.of(2024, 6, 5), "800.00", 25);
        save(LocalDate.of(2024, 6, 15), "1500.00", 45);
        save(LocalDate.of(2024, 6, 25), "1200.00", 35);
        em.flush();
        em.clear();

        List<DailySales> result = repository.search(RESTAURANT_ID, LocalDate.of(2024, 6, 15), null);

        assertThat(result).hasSize(2)
            .allMatch(d -> !d.getData().isBefore(LocalDate.of(2024, 6, 15)));
    }

    @Test
    void search_withToOnly_returnsToDateInclusive() {
        save(LocalDate.of(2024, 6, 5), "800.00", 25);
        save(LocalDate.of(2024, 6, 15), "1500.00", 45);
        save(LocalDate.of(2024, 6, 25), "1200.00", 35);
        em.flush();
        em.clear();

        List<DailySales> result = repository.search(RESTAURANT_ID, null, LocalDate.of(2024, 6, 15));

        assertThat(result).hasSize(2)
            .allMatch(d -> !d.getData().isAfter(LocalDate.of(2024, 6, 15)));
    }

    @Test
    void search_orderedByDate() {
        save(LocalDate.of(2024, 6, 20), "1200.00", 35);
        save(LocalDate.of(2024, 6, 5), "800.00", 25);
        save(LocalDate.of(2024, 6, 15), "1500.00", 45);
        em.flush();
        em.clear();

        List<DailySales> result = repository.search(RESTAURANT_ID, null, null);

        assertThat(result.get(0).getData()).isEqualTo(LocalDate.of(2024, 6, 5));
        assertThat(result.get(1).getData()).isEqualTo(LocalDate.of(2024, 6, 15));
        assertThat(result.get(2).getData()).isEqualTo(LocalDate.of(2024, 6, 20));
    }

    private void save(LocalDate data, String fatturato, int coperti) {
        DailySales ds = new DailySales();
        ds.setRestaurantId(RESTAURANT_ID);
        ds.setData(data);
        ds.setFatturato(new BigDecimal(fatturato));
        ds.setCoperti(coperti);
        em.persist(ds);
    }
}
