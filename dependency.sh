#!/bin/bash

mkdir -p lib/

cp rec-core/build/libs/rec-core-*.jar lib/

[ -e lib/dblite.jar ] || wget https://github.com/kenpusney/dblite/releases/download/v0.0.1/dblite-0.0.1.jar -O lib/dblite.jar
[ -e lib/postgres-jdbc.jar ] || wget https://jdbc.postgresql.org/download/postgresql-42.1.1.jar -O lib/postgres-jdbc.jar


