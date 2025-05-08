#!/bin/bash

echo "开始执行日程显示Bug修复脚本..."

# 1. 停止服务
echo "停止服务..."
# 根据您的实际情况修改停止服务的命令，例如
# systemctl stop your-service-name
# 或
# docker-compose down

# 2. 导入数据库修改
echo "导入数据库修改..."
# 根据您的数据库连接信息修改以下命令
# mysql -u username -p database_name < alter_schedule_table.sql

# 3. 重新构建项目
echo "重新构建项目..."
cd server
mvn clean package -DskipTests

# 4. 重启服务
echo "重启服务..."
# 根据您的实际情况修改启动服务的命令，例如
# systemctl start your-service-name
# 或
# docker-compose up -d

echo "修复完成。请验证用户日程是否正常显示，以及管理员日历视图是否不再有重复显示的问题。" 