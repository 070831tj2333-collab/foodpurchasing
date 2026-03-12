#!/bin/sh
# 在项目根目录执行：./deploy/build.sh
# 会在 target/ 下生成 campus-food-0.0.1-SNAPSHOT.jar，可重命名为 campus-food.jar 上传到服务器

set -e
cd "$(dirname "$0")/.."
./mvnw clean package -DskipTests
echo "构建完成: target/campus-food-0.0.1-SNAPSHOT.jar"
echo "上传到服务器后建议重命名为: campus-food.jar"
