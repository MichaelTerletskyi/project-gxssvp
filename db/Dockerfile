FROM postgres:18.4-alpine
LABEL authors="Michael Terletskyi"

COPY ./init-scripts/ /docker-entrypoint-initdb.d/

ENV POSTGRES_DB=db
ENV POSTGRES_USER=user
ENV POSTGRES_PASSWORD=password