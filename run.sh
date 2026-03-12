#!/bin/sh
# 一键启动（开发环境，使用 H2 内存库，无需 MySQL）
# 在项目根目录执行：./run.sh

cd "$(dirname "$0")"

if [ -f "target/campus-food-0.0.1-SNAPSHOT.jar" ]; then
  echo "使用已打包的 JAR 启动..."
  exec java -jar target/campus-food-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
else
  echo "未找到 JAR，使用 Maven 启动..."
  exec ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
fi
