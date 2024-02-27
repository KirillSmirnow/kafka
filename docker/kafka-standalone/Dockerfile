FROM alpine
WORKDIR /kafka
RUN apk add bash openjdk17
RUN wget https://downloads.apache.org/kafka/3.6.1/kafka_2.13-3.6.1.tgz
RUN tar xf *.tgz --strip-components=1
COPY server.properties ./config/
