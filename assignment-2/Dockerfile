FROM gradle

WORKDIR /lb-assignment
COPY . .
RUN ./gradlew clean build -x test && cp build/libs/*.jar ./app.jar

CMD ["java", "-jar", "app.jar"]
