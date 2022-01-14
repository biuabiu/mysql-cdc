[TOC]

Replicator是一个简单、快速、高性能、轻量级、基于java的开源mysql-cdc框架。

### 项目简介
项目是基于[mysql-binlog-connector-java](mysql-binlog-connector-java)开发,转发该项目解析的binlog事件,并在此基础上二次封装转发,可将数据快速写入其它中间件,如ES,MQ,MongoDB等

### 快速开始
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
