#!/bin/bash
API_NODE="ubuntu@157.151.230.60"
REPO_PATH="/opt/spring-api"

echo "Desplegando backend en api-node-1..."

ssh $API_NODE "
  cd $REPO_PATH &&
  git pull &&
  sudo rm -rf backend/target &&
  mvn clean package -DskipTests -f backend/pom.xml &&
  sudo systemctl restart spring-api &&
  echo 'Deploy completado.'