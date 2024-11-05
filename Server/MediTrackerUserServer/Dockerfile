# 使用 Java 17 镜像作为基础
FROM openjdk:17

# 设置工作目录
WORKDIR /app

# 将构建好的 JAR 文件复制到工作目录
COPY ./target/MediTrackerUserServer-0.0.1-SNAPSHOT.jar /app/MediTrackerUserServer.jar

# 设置运行 Java 应用的命令
CMD ["java", "-jar", "MediTrackerUserServer.jar"]
