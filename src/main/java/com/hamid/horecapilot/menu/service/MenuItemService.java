package com.hamid.horecapilot.menu.service;

import com.hamid.horecapilot.common.EntityNotFoundException;
import com.hamid.horecapilot.common.Tenant;
import com.hamid.horecapilot.menu.dto.MenuItemCreateRequest;
import com.hamid.horecapilot.menu.dto.MenuItemResponse;
import com.hamid.horecapilot.menu.dto.MenuItemUpdateRequest;
import com.hamid.horecapilot.menu.model.MenuItem;
import com.hamid.horecapilot.menu.repository.MenuItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MenuItemService {

    private final MenuItemRepository repository;

    public MenuItemService(MenuItemRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public MenuItemResponse create(MenuItemCreateRequest req) {
        MenuItem item = new MenuItem();
        item.setRestaurantId(Tenant.DEFAULT_RESTAURANT_ID);
        item.setNome(req.nome());
        item.setPrezzoVendita(req.prezzoVendita());
        item.setCategoria(req.categoria());
        item.setAttivo(true);
        return toResponse(repository.save(item));
    }

    @Transactional
    public MenuItemResponse update(Long id, MenuItemUpdateRequest req) {
        MenuItem item = findOrThrow(id);
        item.setNome(req.nome());
        item.setPrezzoVendita(req.prezzoVendita());
        item.setCategoria(req.categoria());
        return toResponse(item);
    }

    public MenuItemResponse getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public List<MenuItemResponse> list() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public void deactivate(Long id) {
        findOrThrow(id).setAttivo(false);
    }

    private MenuItem findOrThrow(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("MenuItem not found with id: " + id));
    }

    private MenuItemResponse toResponse(MenuItem i) {
        return new MenuItemResponse(i.getId(), i.getNome(), i.getPrezzoVendita(), i.getCategoria(), i.isAttivo());
    }
}
