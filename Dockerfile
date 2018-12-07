FROM        java:8-jre-alpine

RUN         apk update && apk add bash
RUN         mkdir /app
ADD         target/universal/stage /app/

ENTRYPOINT ["/app/bin/omg-sendgrid"]
