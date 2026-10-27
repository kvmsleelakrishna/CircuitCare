package com.circuitcare;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ComponentRepository
        extends JpaRepository<Component, Integer> {

    @Query("SELECT c FROM Component c "
         + "WHERE c.quantity < c.minimumStock")
    List<Component> findLowStockComponents();
}

