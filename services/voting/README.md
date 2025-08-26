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
1. Start the application with:
  ```sh
  java -jar target/voting-0.0.1-SNAPSHOT.jar
  ```
2. The service will start on port **8086** by default.
3. Access the app in your browser at [http://localhost:8086](http://localhost:8086)

### Build & Run with Docker
You do NOT need to build the JAR locally. The Docker build will handle everything.

**Build the Docker image:**
```sh
docker build -t voting-app .
```

**Run the Docker container:**
```sh
docker run -p 8086:8086 voting-app
```

The service will be available at [http://localhost:8086](http://localhost:8086)

### Notes
- Make sure no other service is running on port 8086.
- You can configure the port and other settings in `src/main/resources/application.properties`.
- The Maven Wrapper (`mvnw`/`mvnw.cmd`) ensures consistent Maven builds even if Maven is not installed globally.
  