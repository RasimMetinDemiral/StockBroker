# StockBroker
Internal brokerage system API: Secure order management (CRUD) for stocks, designed for employee workflows.

Requirements

  Ensure the following technologies are installed on your system:

    •	Java Development Kit (JDK) 21
    •	Maven 3.8+
    •	Docker & Docker Compose
    •	Git (for cloning the project)

Technologies Used

  •	Java 21
  •	Spring Boot 3.4.4
  •	Spring Security (JWT authentication and authorization)
  •	MySQL 8.0
  •	Docker & Docker Compose
  •	Prometheus & Grafana (Monitoring)
  •	Swagger-UI (API test)
  •	JUnit & Mockito (Unit test)

Setup Instructions

  1.	Use IntellijIdea terminal for starting the MySQL container first to initialize the database volume. Wait for the MySQL container is successfully ready:
  
    docker-compose up mysql
  
  2.	Then build the application:
  
    ./mvnw clean package -DskipTests
  
  3.	Start the remaining services for use this script:
  
    docker-compose up –build

Running the Application

  •	Broker API: http://localhost:9090
  •	Swagger UI: http://localhost:9090/swagger-ui/index.html
  •	Prometheus Dasboard: http://localhost:9091 
  •	Prometheus: http://localhost:9090/actuator/prometheus
  •	Grafana Dashboard: http://localhost:3000/login 
    Username:admin, pass:admin
  •Database -> MYSQL_USER: root, MYSQL_ROOT_PASSWORD: rootroot ,MYSQL_DATABASE: broker_db

Application Credentials

  Role Username Password --> Admin admin admin123, Customer Metin 123456
				
API Testing with Swagger

    Get request for admin role steps:
        1.	Open Swagger UI.
        2.	Use the /auth/login endpoint with:
        3.	{"username": "admin","password": "admin123"}
        4.	Copy the token from the login response.
        5.	Click Authorize and paste Bearer <generated string>.
        6.	you can access admin-protected endpoints on swagger-ui.
