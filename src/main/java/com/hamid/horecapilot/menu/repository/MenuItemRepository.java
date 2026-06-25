package com.hamid.horecapilot.menu.repository;

import com.hamid.horecapilot.menu.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
}
