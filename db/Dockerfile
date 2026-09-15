FROM postgres:18.4

RUN mkdir -p /etc/postgres/logs && chown -R postgres:postgres /etc/postgres/logs
COPY postgres/init/postgres.conf /etc/postgres/postgres.conf
COPY postgres/init/pg_hba.conf /etc/postgres/pg_hba.conf
COPY postgres/init/init_schema.sql /docker-entrypoint-initdb.d/init.sql

EXPOSE 5432
CMD ["postgres", "-c", "config_file=/etc/postgres/postgres.conf", "-c", "hba_file=/etc/postgres/pg_hba.conf"]