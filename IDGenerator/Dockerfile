FROM java:8
VOLUME /tmp
ARG zk_port
ARG zk_address
ARG local_prop_path
ENV ZK_ADDRESS ${zk_address}
ENV ZK_PORT ${zk_port}
ENV LOCAL_PROP_PATH ${local_prop_path}

ADD ./target/leaf-0.0.1-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]