FROM davidcaste/alpine-java-unlimited-jce:jre8
VOLUME /tmp
ADD target/cogny-staff-backend.war app.war

ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ENV JAVA_OPTS=""
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.war