FROM mysql:8.0

ADD ../config/init.sql /docker-entrypoint-initdb.d/init.sql