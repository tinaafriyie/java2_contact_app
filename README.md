 Contact Management Application

A JavaFX-based desktop application for managing personal and professional contacts with a modern, user-friendly interface.

 Project Information

- Course: Java 2
- Project Type: Team Project (4 members)
- Deadline: March 1st, 2026, 23:59:59.999
- Language: Java 25
- Build Tool: Maven

 Team Members

- **Person 1:** [Amoako Ernestina Afriyie] - Database Layer & DAO Implementation
- **Person 2:** [Daniella Fonseca] - Business Logic & Service Layer
- **Person 3:** [Chiemerie David Ekwenua] - Main UI & TableView
- **Person 4:** [Borshon Alfred Gorman] - Forms & CRUD Operations

 Features

-  **View All Contacts** - Display all contacts in an organized table
-  **Add New Contact** - Create new contacts with comprehensive information
-  **Edit Contact** - Update existing contact details
-  **Delete Contact** - Remove contacts with confirmation dialog
-  **Search Functionality** - Real-time search across all contact fields
-  **Data Validation** - Email format validation and required field checking
-  **Unique Constraints** - Prevent duplicate phone numbers and emails
-  **Modern UI** - Clean, professional interface with custom styling

 Technology Stack

| Component | Technology |
|-----------|-----------|
| **Language** | Java 25 |
| **UI Framework** | JavaFX 25 |
| **Database** | SQLite 3.47.2.0 |
| **Build Tool** | Maven |
| **Testing** | JUnit 5.11.4 |
| **Additional** | AssertJ, Mockito |

## Project Structure

```
contact-app/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── module-info.java
│   │   │   └── com/contact/
│   │   │       ├── dao/              # Data Access Layer
│   │   │       │   ├── PersonDAO.java
│   │   │       │   └── PersonDAOImpl.java
│   │   │       ├── model/            # Entity Classes
│   │   │       │   └── Person.java
│   │   │       ├── service/          # Business Logic Layer
│   │   │       │   ├── PersonService.java
│   │   │       │   └── PersonServiceImpl.java
│   │   │       ├── ui/               # User Interface
│   │   │       │   ├── App.java
│   │   │       │   └── PersonFormController.java
│   │   │       └── util/             # Utilities
│   │   │           └── DatabaseConnection.java
│   │   └── resources/
│   │       ├── db/
│   │       │   └── init.sql          # Database Schema
│   │       ├── fxml/
│   │       │   └── PersonForm.fxml   # Form Layout
│   │       └── style.css             # Application Styling
│   └── test/
│       └── java/
│           └── com/contact/dao/
│               └── PersonDAOTest.java
├── database/
│   └── contacts.db                   # SQLite Database (auto-generated)
├── pom.xml                           # Maven Configuration
└── README.md
```

## Database Schema

```sql
CREATE TABLE IF NOT EXISTS person (
    idperson INTEGER PRIMARY KEY AUTOINCREMENT,
    lastname VARCHAR(45) NOT NULL,
    firstname VARCHAR(45) NOT NULL,
    nickname VARCHAR(45) NOT NULL,
    phone_number VARCHAR(15) UNIQUE,
    address VARCHAR(200),
    email_address VARCHAR(150) ,
    birth_date DATE
);
```

## Setup & Installation

### Prerequisites

- **JDK 25** (or JDK 21 minimum)
- **Maven 3.8+**
- **Git**

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/tinaafriyie/java2_contact_app.git
   cd java2-contact-app
   ```

2. **Build the project**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn javafx:run
   ```

   Or run directly from your IDE:
   - Right-click `App.java` → Run As → Java Application

## Usage

### Adding a Contact
1. Click the **"+ Add"** button
2. Fill in the required fields (First Name, Last Name, Nickname)
3. Optionally add phone, email, address, and birth date
4. Click **"Save"**

### Editing a Contact
1. Select a contact from the table
2. Click the **"Edit"** button
3. Modify the desired fields
4. Click **"Update"**

### Deleting a Contact
1. Select a contact from the table
2. Click the **"Delete"** button
3. Confirm the deletion in the dialog

### Searching Contacts
- Type in the search box at the top
- Results filter automatically as you type
- Search works across all fields (name, phone, email, nickname)

## Testing

Run all unit tests:
```bash
mvn test
```

Expected output:
```
Tests run: 14, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

  Architecture

The application follows a **layered architecture**:

1. **Presentation Layer (UI)** - JavaFX components and controllers
2. **Service Layer** - Business logic and validation
3. **Data Access Layer (DAO)** - Database operations
4. **Model Layer** - Entity classes representing data

 Design Patterns Used
- **Singleton Pattern** - Database connection management
- **DAO Pattern** - Data access abstraction
- **MVC Pattern** - Separation of concerns in UI

 Data Validation

- **Required Fields:** First Name, Last Name, Nickname
- **Email Validation:** Must match standard email format
- **Unique Constraints:** No duplicate phone numbers or emails
- **Length Limits:** Fields respect database column constraints
- **Date Validation:** Birth date cannot be in the future

 Known Issues

- Native access warnings in Java 25 (informational only)
- Eclipse Maven plugin warnings (can be ignored)

 Requirements Fulfilled

 Functional Requirements 
-  JavaFX GUI with clear, professional design
-  List all persons in database
-  Add new person with form
-  Update existing person data
-  Delete person with confirmation

 Non-Functional Requirements 
-  Maven project structure
-  SQLite database
-  Unit tests for database methods
-  Modularized code across multiple classes
-  Proper resource management (try-with-resources)
-  Project importable in any IDE

 Dependencies

Key dependencies (see `pom.xml` for complete list):

```xml
<dependencies>
    <dependency>
        <groupId>org.xerial</groupId>
        <artifactId>sqlite-jdbc</artifactId>
        <version>3.47.2.0</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>25</version>
    </dependency>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.11.4</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```

 UI Features

- **Modern Design** - Clean, professional interface with custom CSS
- **Responsive Layout** - Adapts to window resizing
- **Real-time Search** - Instant filtering as you type
- **Visual Feedback** - Success/error alerts for all operations
- **Keyboard Shortcuts** - Enter to submit forms, Escape to cancel
- **Table Sorting** - Click column headers to sort
- **Row Selection** - Click to select, double-click to edit

 Troubleshooting

### Application won't start
```bash
# Clean and rebuild
mvn clean install
# Delete database and restart
rm database/contacts.db
mvn javafx:run
```

### "Module not found" error
- Ensure `module-info.java` is in `src/main/java` 
- Run `mvn clean install` to rebuild

### Database errors
- Delete `database/contacts.db` and restart
- Check that `init.sql` is in `src/main/resources/db/`

# java2_contact_app