FROM amazoncorretto:20
EXPOSE 8080:8080
RUN mkdir /app
COPY build/libs/*.jar /app/app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
ENV JWT_AUDIENCE=$JWT_AUDIENCE
ENV JWT_ISSUER=$JWT_ISSUER
ENV JWT_SECRET=$JWT_SECRET
ENV JWT_REALM=$JWT_REALM
ENV MONGO_HOST=$MONGO_HOST
ENV MONGO_PORT=$MONGO_PORT
ENV MONGO_USER=$MONGO_USER
ENV MONGO_PASSWORD=$MONGO_PASSWORDs