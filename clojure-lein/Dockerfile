FROM java:openjdk-8-jre

COPY target/uberjar/clojure-lein-0.1.0-SNAPSHOT-standalone.jar clojure-lein-0.1.0-SNAPSHOT-standalone.jar

EXPOSE 4000

CMD ["java", "-jar", "clojure-lein-0.1.0-SNAPSHOT-standalone.jar"]
