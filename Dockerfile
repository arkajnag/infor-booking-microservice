FROM apline:latest
LABEL MAINTAINER="Arkajyoti Nag(arka.imps@gmail.com)"
EXPOSE 3003
RUN apk --no-cache update
RUN apk --no-cache maven
RUN apk --no-cache openjdk8
WORKDIR /demo-carbooking-service
COPY /target/booking-service.jar /demo-carbooking-service/target/booking-service.jar
CMD ["java","-jar","/demo-carbooking-service/target/booking-service.jar"]