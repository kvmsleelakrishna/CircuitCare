
const API_BASE = "http://localhost:8080/api/tickets";

// Reusable helper for GET, POST, PATCH and other API requests.
async function apiRequest(url, options = {}) {
    const response = await fetch(url, options);

    if (!response.ok) {
        const message = await response.text();
        throw new Error(message || `HTTP error: ${response.status}`);
    }

    // Some successful responses, such as HTTP 204, have no body.
    if (response.status === 204) {
        return null;
    }

    const text = await response.text();
    return text ? JSON.parse(text) : null;
}

let tickets = [];

const COMPONENTS_API = "http://localhost:8080/api/components";

let components = [];

const ticketTableBody = document.getElementById("ticketTableBody");
const inventoryTableBody = document.getElementById("inventoryTableBody");
const searchInput = document.getElementById("searchInput");
const statusFilter = document.getElementById("statusFilter");
const emptyMessage = document.getElementById("emptyMessage");

const modal = document.getElementById("ticketModal");
const ticketForm = document.getElementById("ticketForm");
const formMessage = document.getElementById("formMessage");

const allowedStatuses = ["Pending", "In Progress", "Completed"];

function getStatusClass(status) {
    return status.toLowerCase().replace(/\s+/g, "-");
}

// Convert backend field names into frontend field names.
function formatTicket(ticket) {
    return {
        ...ticket,
        ticketId: ticket.ticketId,
        customerName: ticket.customerName,
        deviceName: ticket.deviceName,
        issue: ticket.issueDescription,
        status: ticket.status
    };
}

// GET: Fetch tickets from Spring Boot and MySQL.
async function loadTickets() {
    try {
        console.log("Fetching tickets from:", API_BASE);

        const data = await apiRequest(API_BASE);

        if (!Array.isArray(data)) {
            throw new Error("Expected the backend to return a JSON array.");
        }

        tickets = data.map(formatTicket);

        console.log("Tickets loaded:", tickets);

        renderTickets();
    } catch (error) {
        console.error("Could not load tickets:", error);

        emptyMessage.hidden = false;
        emptyMessage.textContent =
            "Could not load tickets. Check the backend and browser console.";

        updateDashboard();
    }
}

function updateDashboard() {
    document.getElementById("totalTickets").textContent = tickets.length;

    document.getElementById("pendingTickets").textContent =
        tickets.filter(ticket => ticket.status === "Pending").length;

    document.getElementById("progressTickets").textContent =
        tickets.filter(ticket => ticket.status === "In Progress").length;

    document.getElementById("completedTickets").textContent =
        tickets.filter(ticket => ticket.status === "Completed").length;
}

// PATCH: Persist a ticket status change in the backend.
async function updateTicketStatus(ticket, newStatus, statusSelect) {
    const previousStatus = ticket.status;

    if (!allowedStatuses.includes(newStatus)) {
        statusSelect.value = previousStatus;
        return;
    }

    statusSelect.disabled = true;

    try {
        await apiRequest(
            `${API_BASE}/${ticket.ticketId}/status`,
            {
                method: "PATCH",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    status: newStatus
                })
            }
        );

        // Update the local ticket only after the server accepts the change.
        const currentTicket = tickets.find(
            item => item.ticketId === ticket.ticketId
        );

        if (currentTicket) {
            currentTicket.status = newStatus;
        }

        console.log(
            `Ticket ${ticket.ticketId} status updated to ${newStatus}`
        );

        renderTickets();
    } catch (error) {
        console.error("Status update failed:", error);

        statusSelect.value = previousStatus;

        alert(
            `Could not update ticket #${ticket.ticketId}. ` +
            "The previous status has been restored. Check the Console."
        );
    } finally {
        statusSelect.disabled = false;
    }
}

function renderTickets() {
    const searchTerm = searchInput.value.trim().toLowerCase();
    const selectedStatus = statusFilter.value;

    const filteredTickets = tickets.filter(ticket => {
        const matchesSearch = [
            ticket.ticketId,
            ticket.customerName,
            ticket.deviceName,
            ticket.issue
        ].some(value =>
            String(value ?? "").toLowerCase().includes(searchTerm)
        );

        const matchesStatus =
            selectedStatus === "All" ||
            ticket.status === selectedStatus;

        return matchesSearch && matchesStatus;
    });

    ticketTableBody.replaceChildren();

    filteredTickets.forEach(ticket => {
        const row = document.createElement("tr");

        const values = [
            `#${ticket.ticketId}`,
            ticket.customerName,
            ticket.deviceName,
            ticket.issue
        ];

        values.forEach(value => {
            const cell = document.createElement("td");
            cell.textContent = value ?? "";
            row.appendChild(cell);
        });

        // Create the status dropdown.
        const statusCell = document.createElement("td");
        const statusSelect = document.createElement("select");

        statusSelect.className = "status-select";
        statusSelect.setAttribute(
            "aria-label",
            `Change status for ticket ${ticket.ticketId}`
        );

        allowedStatuses.forEach(status => {
            const option = document.createElement("option");

            option.value = status;
            option.textContent = status;
            option.selected = ticket.status === status;

            statusSelect.appendChild(option);
        });

        // IMPORTANT: Save every dropdown change through PATCH.
        statusSelect.addEventListener("change", () => {
            updateTicketStatus(
                ticket,
                statusSelect.value,
                statusSelect
            );
        });

        
        statusCell.appendChild(statusSelect);
        row.appendChild(statusCell);

        // Actions column
        const actionsCell = document.createElement("td");

        const editButton = document.createElement("button");
        editButton.textContent = "Edit";
        editButton.className = "edit-btn";

        editButton.addEventListener("click", () => {
            editTicket(ticket);
        });

        const deleteButton = document.createElement("button");
        deleteButton.textContent = "Delete";
        deleteButton.className = "delete-btn";

        deleteButton.addEventListener("click", () => {
            deleteTicket(ticket);
        });

        actionsCell.appendChild(editButton);
        actionsCell.appendChild(deleteButton);

        row.appendChild(actionsCell);
        ticketTableBody.appendChild(row);

    });

    emptyMessage.hidden = filteredTickets.length !== 0;

    if (filteredTickets.length === 0) {
        emptyMessage.textContent =
            tickets.length === 0
                ? "No service tickets found."
                : "No tickets match your search or selected status.";
    }

    updateDashboard();
}


async function editTicket(ticket) {
    const customerName = prompt(
        "Enter customer name:",
        ticket.customerName
    );

    if (customerName === null) return;

    const deviceName = prompt(
        "Enter device name:",
        ticket.deviceName
    );

    if (deviceName === null) return;

    const issueDescription = prompt(
        "Enter issue description:",
        ticket.issue
    );

    if (issueDescription === null) return;

    if (
        !customerName.trim() ||
        !deviceName.trim() ||
        !issueDescription.trim()
    ) {
        alert("All fields are required.");
        return;
    }

    try {
        await apiRequest(`${API_BASE}/${ticket.ticketId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                customerName: customerName.trim(),
                deviceName: deviceName.trim(),
                issueDescription: issueDescription.trim()
            })
        });

        alert("Ticket updated successfully!");
        await loadTickets();

    } catch (error) {
        console.error("Could not edit ticket:", error);
        alert("Ticket update failed. Check the Console.");
    }
}

async function deleteTicket(ticket) {
    const confirmed = confirm(
        `Are you sure you want to delete ticket #${ticket.ticketId}?`
    );

    if (!confirmed) return;

    try {
        await apiRequest(`${API_BASE}/${ticket.ticketId}`, {
            method: "DELETE"
        });

        alert("Ticket deleted successfully!");
        await loadTickets();

    } catch (error) {
        console.error("Could not delete ticket:", error);
        alert("Ticket deletion failed. Check the Console.");
    }
}


async function renderInventory() {
    try {
        components = await apiRequest(COMPONENTS_API);

        inventoryTableBody.replaceChildren();

        components.forEach(component => {
            const row = document.createElement("tr");

            const values = [
                component.componentName,
                component.quantity,
                component.minimumStock,
                `₹${Number(component.unitPrice ?? 0).toFixed(2)}`
            ];

            values.forEach(value => {
                const cell = document.createElement("td");
                cell.textContent = value ?? "";
                row.appendChild(cell);
            });

            const statusCell = document.createElement("td");
            const badge = document.createElement("span");

            const isLow =
                component.quantity <= component.minimumStock;

            badge.className = isLow
                ? "badge low-stock"
                : "badge stock-ok";

            badge.textContent = isLow ? "Low Stock" : "In Stock";

            statusCell.appendChild(badge);
            row.appendChild(statusCell);

            inventoryTableBody.appendChild(row);
        });

    } catch (error) {
        console.error("Could not load inventory:", error);

        inventoryTableBody.replaceChildren();

        const row = document.createElement("tr");
        const cell = document.createElement("td");

        cell.colSpan = 5;
        cell.textContent =
            "Could not load inventory. Check the backend and Console.";

        row.appendChild(cell);
        inventoryTableBody.appendChild(row);
    }
}


// Open and close the new-ticket modal.
document.getElementById("newTicketBtn").addEventListener("click", () => {
    formMessage.textContent = "";
    modal.hidden = false;
});

document.getElementById("closeModal").addEventListener("click", () => {
    modal.hidden = true;
});

searchInput.addEventListener("input", renderTickets);
statusFilter.addEventListener("change", renderTickets);

// POST: Create and save a new ticket in MySQL.
ticketForm.addEventListener("submit", async event => {
    event.preventDefault();

    const customerName =
        document.getElementById("customerName").value.trim();

    const deviceName =
        document.getElementById("deviceName").value.trim();

    const issueDescription =
        document.getElementById("issueDescription").value.trim();

    if (!customerName || !deviceName || !issueDescription) {
        formMessage.textContent = "Please complete all fields.";
        return;
    }

    const newTicket = {
        customerName,
        deviceName,
        issueDescription
    };

    const submitButton = ticketForm.querySelector(
        'button[type="submit"]'
    );

    submitButton.disabled = true;
    formMessage.textContent = "Saving ticket...";

    try {
        await apiRequest(API_BASE, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(newTicket)
        });

        await loadTickets();

        ticketForm.reset();
        modal.hidden = true;
        formMessage.textContent = "";
    } catch (error) {
        console.error("Could not create ticket:", error);

        formMessage.textContent =
            "Could not save ticket. Check the backend and Console.";
    } finally {
        submitButton.disabled = false;
    }
});

// Initial page setup.
renderInventory();
loadTickets();
