#!/bin/bash
# scripts/init-cluster.sh
# Inicializar el cluster — ejecutar UNA SOLA VEZ desde crdb-node-1
# Requiere que los 3 nodos ya esten corriendo con install-cockroachdb.sh

echo "Inicializando cluster CockroachDB..."
cockroach init --insecure --host=10.0.1.180:26257

echo "Creando base de datos appdb..."
cockroach sql --insecure --host=10.0.1.180:26257 \
  --execute="CREATE DATABASE IF NOT EXISTS appdb;"

echo "Verificando estado del cluster..."
cockroach node status --insecure --host=10.0.1.180:26257

echo "Cluster inicializado. Accede al Admin UI en http://10.0.1.180:8080"