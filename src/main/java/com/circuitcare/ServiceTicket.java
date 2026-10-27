package com.circuitcare;


import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "service_tickets")
public class ServiceTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Integer ticketId;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "device_name", nullable = false)
    private String deviceName;

    @Column(name = "issue_description", nullable = false)
    private String issueDescription;

    private String status;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    public ServiceTicket() {
    }

    public Integer getTicketId() {
        return ticketId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getIssueDescription() {
        return issueDescription;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setTicketId(Integer ticketId) {
        this.ticketId = ticketId;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setIssueDescription(String issueDescription) {
        this.issueDescription = issueDescription;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

