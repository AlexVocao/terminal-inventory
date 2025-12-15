package com.alex.inventory.dao;

import com.alex.inventory.entity.Terminal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TerminalRepository extends JpaRepository<Terminal, Integer> {
    
}
