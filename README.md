# Investment
## ℹ️ About the project
This project sets up a Java/Spring API using Docker Compose to serve as a backend to a Investment Aggregator Platform.

## 📋 Requirements to Run the Investment Project

To successfully run the Investment backend project, ensure that your environment meets the following requirements:

### 🖥️ System Requirements
- **Operating System:** 
  - Windows, macOS, or Linux

### ⚙️ Software Requirements
1. **Java Development Kit (JDK)**
   - Version: **Java 21 (LTS)**
   - Ensure that the JDK is properly installed and configured in your system's PATH.

2. **Maven**
   - Version: **3.6.0** or higher
   - Maven is required for building and running the Spring Boot application.

3. **Docker**
   - Version: **20.10** or higher
   - Docker is needed to run the MySQL database in a containerized environment.

### 📦 Additional Tools (Optional)
- **Git**
  - Version: **2.0** or higher
  - Git is recommended for cloning the project repository.

### 📑 Configuration
- Ensure that you have sufficient permissions to run Docker commands on your system.
- Verify that your system has enough resources (CPU, RAM, and Disk Space) to run the application and the database.

### 🔗 Links
- [Download Java JDK](https://www.oracle.com/java/technologies/javase-jdk21-downloads.html)
- [Download Maven](https://maven.apache.org/download.cgi)
- [Download Docker](https://www.docker.com/get-started)
- [Download Git](https://git-scm.com/downloads)

Once you have all the requirements in place, you can proceed to clone the repository and run the project.


## 🚀 How to Run the Project
To run the backend locally, you will need to have Java 21 (JDK), Maven, and Docker installed.

1. Clone the repository:
   ```bash
   git clone https://github.com/fcursino/investment.git
   cd investment
2. Start the database with Docker:
   ```bash
   docker-compose up
This will create and start the database container in the background.

3. Run the Spring Boot application:
Use Maven to compile and start the server.
   ```bash
   mvn spring-boot:run
Done! The backend server will be running at http://localhost:8080.
