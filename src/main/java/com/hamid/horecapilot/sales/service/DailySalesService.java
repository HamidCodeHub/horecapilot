package com.hamid.horecapilot.sales.service;

import com.hamid.horecapilot.common.EntityNotFoundException;
import com.hamid.horecapilot.common.Tenant;
import com.hamid.horecapilot.sales.dto.DailySalesResponse;
import com.hamid.horecapilot.sales.dto.DailySalesUpsertRequest;
import com.hamid.horecapilot.sales.model.DailySales;
import com.hamid.horecapilot.sales.repository.DailySalesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class DailySalesService {

    private final DailySalesRepository repository;

    public DailySalesService(DailySalesRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public DailySalesUpsertResult upsert(LocalDate data, DailySalesUpsertRequest req) {
        return repository.findByRestaurantIdAndData(Tenant.DEFAULT_RESTAURANT_ID, data)
            .map(existing -> {
                existing.setFatturato(req.fatturato());
                existing.setCoperti(req.coperti());
                return new DailySalesUpsertResult(false, toResponse(existing));
            })
            .orElseGet(() -> {
                DailySales ds = new DailySales();
                ds.setRestaurantId(Tenant.DEFAULT_RESTAURANT_ID);
                ds.setData(data);
                ds.setFatturato(req.fatturato());
                ds.setCoperti(req.coperti());
                return new DailySalesUpsertResult(true, toResponse(repository.save(ds)));
            });
    }

    public DailySalesResponse getByDate(LocalDate data) {
        return repository.findByRestaurantIdAndData(Tenant.DEFAULT_RESTAURANT_ID, data)
            .map(this::toResponse)
            .orElseThrow(() -> new EntityNotFoundException("DailySales not found for date: " + data));
    }

    public List<DailySalesResponse> search(LocalDate from, LocalDate to) {
        return repository.search(Tenant.DEFAULT_RESTAURANT_ID, from, to).stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional
    public void delete(LocalDate data) {
        DailySales ds = repository.findByRestaurantIdAndData(Tenant.DEFAULT_RESTAURANT_ID, data)
            .orElseThrow(() -> new EntityNotFoundException("DailySales not found for date: " + data));
        repository.delete(ds);
    }

    private DailySalesResponse toResponse(DailySales ds) {
        return new DailySalesResponse(ds.getId(), ds.getData(), ds.getFatturato(), ds.getCoperti());
    }
}
