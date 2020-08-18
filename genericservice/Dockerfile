FROM openjdk:8-jdk-alpine

EXPOSE 9005

COPY target/*.jar app.jar

CMD echo "Service Name..: $SERVICE_NAME"
CMD echo "Jaeger Url....: $JAEGER_URL"
CMD echo "Outgoing Url..: $OUTGOING_URL"

CMD java -jar /app.jar --spring.application.name="$SERVICE_NAME" --opentracing.jaeger.http-sender.url="$JAEGER_URL" --service.outgoing.url="$OUTGOING_URL"

