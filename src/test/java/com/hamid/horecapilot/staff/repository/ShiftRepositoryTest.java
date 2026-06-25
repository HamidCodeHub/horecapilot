package com.hamid.horecapilot.staff.repository;

import com.hamid.horecapilot.staff.model.Employee;
import com.hamid.horecapilot.staff.model.Shift;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.AutoConfigureTestEntityManager;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestEntityManager
@TestPropertySource(properties = {
    "spring.flyway.enabled=false",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class ShiftRepositoryTest {

    @Autowired TestEntityManager em;
    @Autowired ShiftRepository repository;

    private Employee emp1;
    private Employee emp2;

    @BeforeEach
    void setup() {
        emp1 = saveEmployee("Mario Rossi", "Chef");
        emp2 = saveEmployee("Luigi Bianchi", "Cameriere");
        em.flush();
    }

    @Test
    void search_withNullEmployeeId_returnsAllShiftsInRange() {
        saveShift(emp1, LocalDate.of(2024, 6, 15));
        saveShift(emp2, LocalDate.of(2024, 6, 20));
        em.flush();
        em.clear();

        List<Shift> result = repository.search(
            LocalDate.of(2024, 6, 1),
            LocalDate.of(2024, 6, 30),
            null   // was causing PSQLException: could not determine data type of parameter
        );

        assertThat(result).hasSize(2);
    }

    @Test
    void search_withAllNullParams_returnsAll() {
        saveShift(emp1, LocalDate.of(2024, 6, 15));
        saveShift(emp2, LocalDate.of(2024, 7, 10));
        em.flush();
        em.clear();

        List<Shift> result = repository.search(null, null, null);

        assertThat(result).hasSize(2);
    }

    @Test
    void search_withDateRange_excludesShiftsOutsideRange() {
        saveShift(emp1, LocalDate.of(2024, 6, 5));   // before
        saveShift(emp1, LocalDate.of(2024, 6, 15));  // inside
        saveShift(emp1, LocalDate.of(2024, 6, 25));  // after
        em.flush();
        em.clear();

        List<Shift> result = repository.search(
            LocalDate.of(2024, 6, 10),
            LocalDate.of(2024, 6, 20),
            null
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getData()).isEqualTo(LocalDate.of(2024, 6, 15));
    }

    @Test
    void search_withFromOnly_includesFromDateInclusive() {
        saveShift(emp1, LocalDate.of(2024, 6, 9));
        saveShift(emp1, LocalDate.of(2024, 6, 10));
        saveShift(emp1, LocalDate.of(2024, 6, 20));
        em.flush();
        em.clear();

        List<Shift> result = repository.search(LocalDate.of(2024, 6, 10), null, null);

        assertThat(result).hasSize(2)
            .allMatch(s -> !s.getData().isBefore(LocalDate.of(2024, 6, 10)));
    }

    @Test
    void search_withEmployeeId_returnsOnlyThatEmployee() {
        saveShift(emp1, LocalDate.of(2024, 6, 15));
        saveShift(emp2, LocalDate.of(2024, 6, 15));
        em.flush();
        em.clear();

        List<Shift> result = repository.search(null, null, emp1.getId());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmployee().getId()).isEqualTo(emp1.getId());
    }

    @Test
    void search_orderedByDateThenOraInizio() {
        saveShift(emp1, LocalDate.of(2024, 6, 20), LocalTime.of(9, 0));
        saveShift(emp1, LocalDate.of(2024, 6, 15), LocalTime.of(17, 0));
        saveShift(emp1, LocalDate.of(2024, 6, 15), LocalTime.of(9, 0));
        em.flush();
        em.clear();

        List<Shift> result = repository.search(null, null, null);

        assertThat(result.get(0).getData()).isEqualTo(LocalDate.of(2024, 6, 15));
        assertThat(result.get(0).getOraInizio()).isEqualTo(LocalTime.of(9, 0));
        assertThat(result.get(1).getOraInizio()).isEqualTo(LocalTime.of(17, 0));
        assertThat(result.get(2).getData()).isEqualTo(LocalDate.of(2024, 6, 20));
    }

    private Employee saveEmployee(String nome, String ruolo) {
        Employee e = new Employee();
        e.setRestaurantId(1L);
        e.setNome(nome);
        e.setRuolo(ruolo);
        e.setCostoOrarioAziendale(new BigDecimal("20.0000"));
        e.setAttivo(true);
        return em.persist(e);
    }

    private void saveShift(Employee emp, LocalDate data) {
        saveShift(emp, data, LocalTime.of(17, 0));
    }

    private void saveShift(Employee emp, LocalDate data, LocalTime oraInizio) {
        Shift s = new Shift();
        s.setRestaurantId(1L);
        s.setEmployee(emp);
        s.setData(data);
        s.setOraInizio(oraInizio);
        s.setOraFine(oraInizio.plusHours(6));
        s.setRuolo("Chef");
        em.persist(s);
    }
}
