# 📒 Smart Contact Manager Application

A full-stack Contact Management web application built with **Spring Boot** and **MySQL**, supporting complete CRUD operations with a clean and responsive UI.
---
## 🚀 Features

- ✅ Add new contacts (name, phone, email, address)
- ✅ View all saved contacts
- ✅ Update existing contact details
- ✅ Delete contacts
- ✅ Responsive UI with Bootstrap
- ✅ REST API backend with Spring Boot

---
## 🛠️ Tech Stack

| Layer | Technology |
------------------
| Backend | Java, Spring Boot, Spring Data JPA, Hibernate |
| Database | MySQL |
| Frontend | HTML, CSS, JavaScript, Bootstrap |
| Tools | Git, Postman |

---
## ⚙️ How to Run Locally

### Prerequisites
- Java 17+
- MySQL installed
- Maven installed

### Steps
1. **Clone the repository**
```bash
   git clone https://github.com/AsjadKhan01/Smart-Contact-Manager-Application.git
   cd Smart-Contact-Manager-Application
```

2. **Configure the database**
   Open `src/main/resources/application.properties` and update:
```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/mydb1
   spring.datasource.username=root
   spring.datasource.password=root1234
   spring.jpa.hibernate.ddl-auto=update
```

3. **Run the application**
```bash
   mvn spring-boot:run
```

4. **Open in browser**
