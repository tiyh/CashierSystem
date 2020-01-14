FROM java:8
VOLUME /tmp
ARG zk_port
ARG zk_address
ENV ZK_ADDRESS ${zk_address}
ENV ZK_PORT ${zk_port}

ADD ./target/leaf-0.0.1-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]