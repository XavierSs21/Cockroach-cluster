#!/bin/bash
# Uso: ./restore.sh <backup_path>
# Ejemplo: ./restore.sh 's3://cockroach-backups/2026/01/01-000000.00'

if [ -z "$1" ]; then
  echo "Uso: $0 <backup_path>"
  echo "Ejemplo: $0 's3://cockroach-backups?AWS_ACCESS_KEY_ID=<KEY>&AWS_SECRET_ACCESS_KEY=<SECRET>&AWS_ENDPOINT=https://idenm2iuwhbz.compat.objectstorage.us-ashburn-1.oraclecloud.com/2026-01-01'"
  exit 1
fi

DATE=$(date +%Y%m%d_%H%M%S)
echo "[$DATE] Iniciando restore de appdb..."

cockroach sql --insecure --host=10.0.1.180:26257 --execute="
DROP DATABASE IF EXISTS appdb CASCADE;
RESTORE DATABASE appdb FROM LATEST IN '$1';
"

echo "[$DATE] Restore completado."