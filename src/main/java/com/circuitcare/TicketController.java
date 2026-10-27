package com.circuitcare;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = {
    "http://localhost:5500",
    "http://127.0.0.1:5500"
})
@RequestMapping("/api/tickets")
public class TicketController {

    private final ServiceTicketRepository ticketRepository;

    public TicketController(ServiceTicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    // READ: Get all tickets
    @GetMapping
    public List<ServiceTicket> getAllTickets() {
        return ticketRepository.findAll();
    }

    // READ: Get a ticket by ID
    @GetMapping("/{id}")
    public ResponseEntity<ServiceTicket> getTicketById(
            @PathVariable Integer id) {

        return ticketRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // CREATE: Add a new ticket
    @PostMapping
    public ResponseEntity<?> createTicket(
            @RequestBody TicketRequest request) {

        if (isBlank(request.getCustomerName())
                || isBlank(request.getDeviceName())
                || isBlank(request.getIssueDescription())) {
            return ResponseEntity.badRequest()
                    .body("Customer, device and issue are required.");
        }

        ServiceTicket ticket = new ServiceTicket();
        ticket.setCustomerName(request.getCustomerName().trim());
        ticket.setDeviceName(request.getDeviceName().trim());
        ticket.setIssueDescription(request.getIssueDescription().trim());
        ticket.setStatus("Pending");

        ServiceTicket savedTicket = ticketRepository.save(ticket);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedTicket);
    }

    // UPDATE: Replace ticket details
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTicket(
            @PathVariable Integer id,
            @RequestBody TicketRequest request) {

        if (isBlank(request.getCustomerName())
                || isBlank(request.getDeviceName())
                || isBlank(request.getIssueDescription())) {
            return ResponseEntity.badRequest()
                    .body("Customer, device and issue are required.");
        }

        return ticketRepository.findById(id)
                .<ResponseEntity<?>>map(ticket -> {
                    ticket.setCustomerName(
                            request.getCustomerName().trim());
                    ticket.setDeviceName(
                            request.getDeviceName().trim());
                    ticket.setIssueDescription(
                            request.getIssueDescription().trim());

                    ServiceTicket updatedTicket =
                            ticketRepository.save(ticket);

                    return ResponseEntity.ok(updatedTicket);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // DELETE: Remove a ticket
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(
            @PathVariable Integer id) {

        if (!ticketRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        ticketRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateTicketStatus(
            @PathVariable Integer id,
            @RequestBody TicketStatusRequest request) {

        if (request == null || request.getStatus() == null) {
            return ResponseEntity.badRequest()
                    .body("Status is required.");
        }

        String newStatus = request.getStatus().trim();

        Set<String> allowedStatuses = Set.of(
                "Pending",
                "In Progress",
                "Completed"
        );

        if (!allowedStatuses.contains(newStatus)) {
            return ResponseEntity.badRequest()
                    .body("Status must be Pending, In Progress, or Completed.");
        }

        return ticketRepository.findById(id)
                .<ResponseEntity<?>>map(ticket -> {
                    ticket.setStatus(newStatus);

                    ServiceTicket updated = ticketRepository.save(ticket);

                    return ResponseEntity.ok(updated);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
