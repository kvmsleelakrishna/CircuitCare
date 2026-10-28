# CircuitCare - Electronics Service Management System

A web-based electronics service management system built to manage service tickets, track repair progress, and monitor component inventory.

## About the Project

CircuitCare helps organize electronics repair requests through a simple dashboard. Users can create service tickets, view existing tickets, search and filter requests, and update repair statuses.

This project was developed to apply Java backend development concepts, REST API integration, database persistence, and frontend development in a practical application.

## Features

- **Dashboard:** View total, pending, in-progress, and completed service tickets.
- **Create Service Tickets:** Record customer details, device information, and reported issues.
- **Ticket Management:** Retrieve tickets from the backend and display them in a table.
- **Status Updates:** Change ticket statuses and persist updates through REST APIs.
- **Search and Filter:** Find tickets using search and status filters.
- **Component Inventory:** Display component quantities and identify low-stock items.

## Technology Stack

| Technology | Purpose |
|---|---|
| Java | Backend programming |
| Spring Boot | REST API development |
| Spring Data JPA / Hibernate | Database persistence |
| MySQL | Relational database |
| HTML | Frontend structure |
| CSS | Frontend styling |
| JavaScript | Frontend logic and API integration |
| Maven | Dependency and build management |
| Git & GitHub | Version control and source code hosting |

## Project Structure

```text
CircuitCare/
├── Frontend/
│   ├── index.html
│   ├── style.css
│   └── script.js
├── src/
│   └── main/
│       ├── java/com/circuitcare/
│       └── resources/
│           └── application.properties
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

## Getting Started

### Prerequisites

Install the following before running the project:

- Java JDK 21
- MySQL Server
- Git
- A web browser
- Visual Studio Code (recommended)

### 1. Clone the Repository

```bash
git clone https://github.com/kvmsleelakrishna/CircuitCare.git
cd CircuitCare
```

### 2. Set Up the Database

Start MySQL and create the database:

```sql
CREATE DATABASE circuitcare;
```

### 3. Configure Database Credentials

Configure the following environment variables for the Spring Boot application:

```text
DB_URL=jdbc:mysql://localhost:3306/circuitcare
DB_USERNAME=your_mysql_username
DB_PASSWORD=your_mysql_password
```

The application reads these values from `src/main/resources/application.properties`.

**Security note:** Do not commit real database passwords or other secrets to GitHub.

### 4. Run the Backend

From the project root directory, run the following command in the VS Code terminal on Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

The backend should start at:

`http://localhost:8080`

### 5. Run the Frontend

Open the `Frontend` folder in Visual Studio Code and launch `index.html` using the Live Server extension, or open the file in a browser.

The frontend expects the Spring Boot API to be available at:

`http://localhost:8080/api/tickets`

Keep the backend running while using the frontend. The MySQL server must also be running and correctly configured.

## REST API Endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/tickets` | Retrieve service tickets |
| POST | `/api/tickets` | Create a service ticket |
| PATCH | `/api/tickets/{ticketId}/status` | Update a ticket's status |

### Example: Create a Service Ticket

**Request:** `POST /api/tickets`

```json
{
  "customerName": "Rahul",
  "deviceName": "Laptop",
  "issueDescription": "Device overheating"
}
```

### Example: Update Ticket Status

**Request:** `PATCH /api/tickets/1/status`

```json
{
  "status": "Completed"
}
```

Use an existing ticket ID when testing the status update endpoint.

## Current Limitations

- The backend and MySQL database are configured for local execution.
- The frontend inventory currently uses sample component data.
- Public deployment has not yet been configured.

## Future Enhancements

- Deploy the backend and frontend for public access.
- Add ticket editing and deletion.
- Implement user authentication and role-based access.
- Add inventory updates and stock management.
- Improve validation, error handling, and automated testing.

## Author

**Leela Krishna**

B.Tech - Electronics and Communication Engineering

[GitHub Profile](https://github.com/kvmsleelakrishna)

---

*CircuitCare - Making electronics service management more organized and efficient.*
