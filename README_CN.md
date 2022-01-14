[TOC]

Replicator是一个简单、快速、高性能、轻量级、基于java的开源mysql-cdc框架。

### 项目简介
项目是基于[mysql-binlog-connector-java](https://github.com/osheroff/mysql-binlog-connector-java)开发,转发该项目解析的binlog事件,并在此基础上二次封装转发,可将数据快速写入其它中间件,如ES,MQ,MongoDB等

### 快速开始
#### 克隆项目
```
# git bash 
git clone git@github.com:biuabiu/mysql-cdc.git #如果未配置github SSH,请使用 Https方式克隆
cd mysql-cdc/
mvn clean -DskipTests install
cd replicator-example/target/
# 请配置正确的用户名与密码
java -jar replicator-example-0.0.1-SNAPSHOT.jar --replicator.mysql.username=your_username --replicator.mysql.password=your_password 
```

#### 配置数据库
```
# 从库8.0.18开启binlog
log-bin=mysql-bin # 开启 binlog
binlog-format=ROW # 选择 ROW 模式
# mysql5.7是中划线,8.0中是下划线
server_id=2 # 配置 MySQL replaction 需要定义，需要保证唯一
# 开启row_data_meta,window 可以在workbrench控制
binlog_row_metadata = FULL
```
***上面这些配置处理好以后，记得需要重启***
#### 创建测试数据库
```
CREATE DATABASE IF NOT EXISTS test DEFAULT CHARACTER SET = 'utf8mb4';
```
#### 创建测试数据表
```
USE test;
SET NAMES utf8mb4;

DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `type` int(11) NULL DEFAULT NULL COMMENT '标签类型,codelist ',
  `code` int(11) NULL DEFAULT NULL COMMENT '标签code,codelist ',
  `code_desc` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NULL DEFAULT NULL COMMENT '标签code描述信息',
  `data_type` int(11) NULL DEFAULT NULL COMMENT '标签数据可选范围,codelist ',
  `create_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_bin ROW_FORMAT = Dynamic;

```
#### 测试SQL
```
INSERT INTO `tag` VALUES (3, 2, 6, '2022-01-14 10:17:57', 444444, '2021-08-27 11:41:35', '2022-01-14 10:17:57');
INSERT INTO `tag` VALUES (4, 2, 1222, '2022-01-14 10:17:57', 444444, '2021-08-27 11:41:35', '2022-01-14 10:17:57');
INSERT INTO `tag` VALUES (5, 2, 1222, '11', 3, '2021-08-27 11:41:35', '2022-01-13 11:45:09');
INSERT INTO `tag` VALUES (6, 2, 1222, '11', 3, '2021-08-27 11:41:35', '2022-01-13 11:45:09');
INSERT INTO `tag` VALUES (7, 1, 1222, '2022-01-11 18:29:09', 3, '2021-08-27 11:18:35', '2022-01-13 11:45:09');
INSERT INTO `tag` VALUES (8, 1, 1222, NULL, 3, '2021-08-27 11:18:35', '2022-01-13 11:45:09');
UPDATE `test`.`tag` SET `code_desc` =NOW() WHERE `id` <5
```
#### 测试SQL产生的项目log(
```
需要先启动replicator-example项目,并观察控制台)
```

### 注意事项
本项目要求MySQL 8.0+ or MariaDB 10.5+,因为较旧版本的 MySQL/MariaDB binlog不包含有关字段名称的信息,
MySQL 8.0+ 和 MariaDB 10.5+ 具有扩展的元数据，其中包含以下数据：
1. 列名
2. 列字符集
3. 数字列的符号
4. 枚举/设置值（在二进制日志中作为数字传输）
5. ...

详见[mysql binlog_row_metadata](https://dev.mysql.com/doc/refman/8.0/en/replication-options-binary-log.html#sysvar_binlog_row_metadata)和[mariadb binary-log-and-replication-more-metadata](https://mariadb.com/kb/en/changes-improvements-in-mariadb-105/#binary-log-and-replication-more-metadata)
### 补充说明
若实在无法达到项目要求的数据库版本,也可以考虑使用高版本作为较旧版本的从节点,如MySQL-5.7.16为Master节点,MySQL-8.0.18为从节点,然后监听从节点的binlog即可
