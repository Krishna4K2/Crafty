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

You can run the voting service with either H2 (default) or MongoDB as the database.

#### **Option 1: H2 (default, in-memory)**
1. Start the application with:
  ```sh
  java -jar target/voting-0.0.1-SNAPSHOT.jar
  ```
2. The service will start on port **8086** by default.
3. Access the app in your browser at [http://localhost:8086](http://localhost:8086)

#### **Option 2: MongoDB (external, persistent)**
1. Set the environment variable `SPRING_PROFILES_ACTIVE=mongo` (or add to `.env` file).
2. Make sure MongoDB is running (see Docker Compose below).
3. Start the application with:
  ```sh
  SPRING_PROFILES_ACTIVE=mongo java -jar target/voting-0.0.1-SNAPSHOT.jar
  ```
4. The service will start on port **8086** by default and use MongoDB for data storage.
5. Access the app in your browser at [http://localhost:8086](http://localhost:8086)


### Build & Run with Docker
You do NOT need to build the JAR locally. The Docker build will handle everything.


#### **Option 1: H2 (default, in-memory)**
```sh
docker build -t voting-app .
docker run -p 8086:8086 voting-app
```

#### **Option 2: MongoDB (external, persistent)**
Use Docker Compose to run both the voting service and MongoDB:
```sh
docker-compose up --build
```
This will start both services, and the voting app will use MongoDB for data storage.

To run with MongoDB outside Docker Compose, set the environment variable:
```sh
docker run -p 8086:8086 -e SPRING_PROFILES_ACTIVE=mongo voting-app
```


The service will be available at [http://localhost:8086](http://localhost:8086)

---

## Quick Reference: Switching Between H2 and MongoDB


| Scenario              | Environment Variable           | DB Setup         | How to Run                                 |
|-----------------------|-------------------------------|------------------|--------------------------------------------|
| H2 (local)            | SPRING_PROFILES_ACTIVE=h2      | None             | `java -jar ...`                            |
| MongoDB (local)       | SPRING_PROFILES_ACTIVE=mongo   | MongoDB running  | `SPRING_PROFILES_ACTIVE=mongo java -jar ...` |
| H2 (docker)           | SPRING_PROFILES_ACTIVE=h2      | None             | `docker-compose up --build`                |
| MongoDB (docker)      | SPRING_PROFILES_ACTIVE=mongo   | Compose handles  | `SPRING_PROFILES_ACTIVE=mongo docker-compose up --build` |

### Step-by-Step: Local (H2 or MongoDB)
1. **H2 (default):**
  - No extra setup needed.
  - Build and run as described above.
2. **MongoDB:**
  - Start MongoDB locally: `docker run -d --name voting-mongo -p 27017:27017 mongo:6`
  - Set `SPRING_PROFILES_ACTIVE=mongo` in your environment or `.env` file.
  - Build and run as described above.

### Step-by-Step: Docker Compose
1. **H2 (default):**
  - Run: `docker-compose up --build`
  - No environment variable needed.
2. **MongoDB:**
  - Run: `SPRING_PROFILES_ACTIVE=mongo docker-compose up --build`
  - Compose will start both MongoDB and the voting service.

---

### Notes
- Make sure no other service is running on port 8086.
- You can configure the port and other settings in `src/main/resources/application.properties`.
- The Maven Wrapper (`mvnw`/`mvnw.cmd`) ensures consistent Maven builds even if Maven is not installed globally.
  