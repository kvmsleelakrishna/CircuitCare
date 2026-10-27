package com.circuitcare;


import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceTicketRepository
        extends JpaRepository<ServiceTicket, Integer> {
}

