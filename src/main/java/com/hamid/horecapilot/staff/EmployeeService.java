package com.hamid.horecapilot.staff;

import com.hamid.horecapilot.common.EntityNotFoundException;
import com.hamid.horecapilot.staff.dto.EmployeeCreateRequest;
import com.hamid.horecapilot.staff.dto.EmployeeResponse;
import com.hamid.horecapilot.staff.dto.EmployeeUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public EmployeeResponse create(EmployeeCreateRequest request) {
        // TODO: dal contesto utente quando ci sarà l'auth
        Employee employee = new Employee();
        employee.setRestaurantId(1L);
        employee.setNome(request.nome());
        employee.setRuolo(request.ruolo());
        employee.setCostoOrarioAziendale(request.costoOrarioAziendale());
        employee.setAttivo(true);
        return toResponse(repository.save(employee));
    }

    @Transactional
    public EmployeeResponse update(Long id, EmployeeUpdateRequest request) {
        Employee employee = findOrThrow(id);
        employee.setNome(request.nome());
        employee.setRuolo(request.ruolo());
        employee.setCostoOrarioAziendale(request.costoOrarioAziendale());
        return toResponse(employee);
    }

    public EmployeeResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public List<EmployeeResponse> list() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public void deactivate(Long id) {
        findOrThrow(id).setAttivo(false);
    }

    private Employee findOrThrow(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + id));
    }

    private EmployeeResponse toResponse(Employee e) {
        return new EmployeeResponse(e.getId(), e.getNome(), e.getRuolo(), e.getCostoOrarioAziendale(), e.isAttivo());
    }
}
