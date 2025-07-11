FROM eclipse-temurin:17-jdk

# Add wait script
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

# Copy your app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Wait for DB, then run app
ENTRYPOINT ["/wait-for-it.sh", "mysqldb:3306", "--timeout=30", "--", "java", "-jar", "/app.jar"]
