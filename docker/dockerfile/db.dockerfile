FROM mysql:8.0

COPY ../config/mysql.cnf /etc/mysql/conf.d/mysql.cnf
COPY ../config/init.sql /docker-entrypoint-initdb.d/init.sql