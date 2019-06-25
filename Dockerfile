FROM openjdk:11-jdk-slim
ARG jar
RUN groupadd -g 998 censuscsvservice && \
    useradd -r -u 998 -g censuscsvservice censuscsvservice
USER censuscsvservice
COPY $jar /opt/censuscsvservice.jar
ENV JAVA_OPTS=""
ENTRYPOINT [ "java", "-jar", "/opt/censuscsvservice.jar" ]
