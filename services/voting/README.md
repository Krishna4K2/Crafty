## How to Build & Run the Voting Service Locally

### Prerequisites
- **Java JDK 21** installed and `JAVA_HOME` set.
- **Maven 3.9.11** or use the included Maven Wrapper (`mvnw`/`mvnw.cmd`).


### Build & Test Commands
Run these commands from the `services/voting` directory:

**Build the app (skip tests):**
```sh
mvn clean package -DskipTests
# Or use Maven Wrapper:
./mvnw clean package -DskipTests   # (Linux/macOS/Git Bash)
mvnw.cmd clean package -DskipTests # (Windows CMD)
```

**Build the app (run tests):**
```sh
mvn clean package
# Or use Maven Wrapper:
./mvnw clean package
mvnw.cmd clean package
```

**Compile only (no packaging):**
```sh
mvn clean compile
# Or use Maven Wrapper:
./mvnw clean compile
mvnw.cmd clean compile
```

**Run tests only:**
```sh
mvn test
# Or use Maven Wrapper:
./mvnw test
mvnw.cmd test
```

**Clean build artifacts:**
```sh
mvn clean
# Or use Maven Wrapper:
./mvnw clean
mvnw.cmd clean
```

The built JAR will be located in the `target/` folder, e.g. `target/voting-0.0.1-SNAPSHOT.jar`.




### Run the Service (Locally)

To run the voting service locally using the default H2 in-memory database:

1. Build the JAR as described above.
2. Start the service:
  ```sh
  java -jar target/voting-0.0.1-SNAPSHOT.jar
  ```
3. The service will start on port **8086** by default.
4. Access the app in your browser at [http://localhost:8086](http://localhost:8086)



### Build & Run with Docker
You do NOT need to build the JAR locally. The Docker build will handle everything.

#### **Standalone Docker (always H2, in-memory)**
```sh
docker build -t voting-app .
docker run -p 8086:8086 voting-app
```
> The standalone Docker image always uses H2. No environment variable needed.

#### **Docker Compose (always MongoDB)**
Use Docker Compose to run both the voting service and MongoDB:
```sh
docker-compose up --build
```
> The voting service will always use MongoDB in Docker Compose. No environment variable needed.


The service will be available at [http://localhost:8086](http://localhost:8086)

---

## Quick Reference: Switching Between H2 and MongoDB

### Step-by-Step: Local (H2 only)
1. **H2 (default):**
  - No extra setup needed.
  - Build and run as described above.


### Step-by-Step: Docker Compose
1. **MongoDB (always used in Compose):**
  - Run: `docker-compose up --build`
  - Compose will start both MongoDB and the voting service. No environment variable needed.

---

### Notes
- Make sure no other service is running on port 8086.
- You can configure the port and other settings in `src/main/resources/application.properties`.
- The Maven Wrapper (`mvnw`/`mvnw.cmd`) ensures consistent Maven builds even if Maven is not installed globally.
  