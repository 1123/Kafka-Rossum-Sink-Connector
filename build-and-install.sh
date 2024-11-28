mvn clean package -DskipTests
rm -r ~/confluent-7.7.0/share/java/1123-rossum-sink-connector-0.0.1
unzip target/components/packages/1123-rossum-sink-connector-0.0.1.zip
mv 1123-rossum-sink-connector-0.0.1 ~/confluent-7.7.0/share/java
