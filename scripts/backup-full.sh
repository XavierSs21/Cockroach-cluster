#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
echo "[$DATE] Iniciando backup full de appdb..."

cockroach sql --insecure --host=10.0.1.180:26257 --execute="
BACKUP DATABASE appdb INTO 's3://cockroach-backups?AWS_ACCESS_KEY_ID=<ACCESS_KEY>&AWS_SECRET_ACCESS_KEY=<SECRET_KEY>&AWS_ENDPOINT=https://idenm2iuwhbz.compat.objectstorage.us-ashburn-1.oraclecloud.com' AS OF SYSTEM TIME '-10s';
"

echo "[$DATE] Backup completado."