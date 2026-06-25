package com.hamid.horecapilot.staff.service;

import com.hamid.horecapilot.common.BusinessRuleException;
import com.hamid.horecapilot.common.EntityNotFoundException;
import com.hamid.horecapilot.common.Tenant;
import com.hamid.horecapilot.staff.model.Employee;
import com.hamid.horecapilot.staff.model.Shift;
import com.hamid.horecapilot.staff.repository.EmployeeRepository;
import com.hamid.horecapilot.staff.repository.ShiftRepository;
import com.hamid.horecapilot.staff.dto.ShiftCreateRequest;
import com.hamid.horecapilot.staff.dto.ShiftResponse;
import com.hamid.horecapilot.staff.dto.ShiftUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class ShiftService {

    private final ShiftRepository shiftRepository;
    private final EmployeeRepository employeeRepository;

    public ShiftService(ShiftRepository shiftRepository, EmployeeRepository employeeRepository) {
        this.shiftRepository = shiftRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public ShiftResponse create(ShiftCreateRequest request) {
        Employee employee = loadActiveEmployee(request.employeeId());
        validateShiftDuration(request.oraInizio(), request.oraFine());

        Shift shift = new Shift();
        shift.setRestaurantId(Tenant.DEFAULT_RESTAURANT_ID);
        shift.setEmployee(employee);
        shift.setData(request.data());
        shift.setOraInizio(request.oraInizio());
        shift.setOraFine(request.oraFine());
        shift.setRuolo(request.ruolo());
        return toResponse(shiftRepository.save(shift));
    }

    @Transactional
    public ShiftResponse update(Long id, ShiftUpdateRequest request) {
        Shift shift = findOrThrow(id);
        Employee employee = loadActiveEmployee(request.employeeId());
        validateShiftDuration(request.oraInizio(), request.oraFine());

        shift.setEmployee(employee);
        shift.setData(request.data());
        shift.setOraInizio(request.oraInizio());
        shift.setOraFine(request.oraFine());
        shift.setRuolo(request.ruolo());
        return toResponse(shift);
    }

    @Transactional(readOnly = true)
    public ShiftResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<ShiftResponse> search(LocalDate from, LocalDate to, Long employeeId) {
        return shiftRepository.search(from, to, employeeId).stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional
    public void delete(Long id) {
        findOrThrow(id);
        shiftRepository.deleteById(id);
    }

    private Employee loadActiveEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new EntityNotFoundException("Employee not found with id: " + employeeId));
        if (!employee.isAttivo()) {
            throw new BusinessRuleException("Cannot assign shift to inactive employee with id: " + employeeId);
        }
        return employee;
    }

    private void validateShiftDuration(LocalTime oraInizio, LocalTime oraFine) {
        if (oraInizio.equals(oraFine)) {
            throw new BusinessRuleException("Shift duration cannot be zero: oraInizio and oraFine are equal");
        }
    }

    private Shift findOrThrow(Long id) {
        return shiftRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Shift not found with id: " + id));
    }

    private ShiftResponse toResponse(Shift shift) {
        BigDecimal ore = shift.oreLavorate();
        BigDecimal costoTurno = ore.multiply(shift.getEmployee().getCostoOrarioAziendale())
            .setScale(2, RoundingMode.HALF_UP);
        return new ShiftResponse(
            shift.getId(),
            shift.getEmployee().getId(),
            shift.getEmployee().getNome(),
            shift.getData(),
            shift.getOraInizio(),
            shift.getOraFine(),
            shift.getRuolo(),
            ore.setScale(2, RoundingMode.HALF_UP),
            costoTurno
        );
    }
}
