# CockroachDB Cluster Demo

API REST construida con Spring Boot que demuestra alta disponibilidad, failover automático y benchmarking sobre un clúster de CockroachDB desplegado en Oracle Cloud Infrastructure (OCI).

---

## Arquitectura

```
Internet → OCI Load Balancer (129.153.133.42:80)
        → api-node-1:8080 (Spring Boot)
        → monitor-node-1:26258 (HAProxy)
        → crdb-node-1/2/3:26257 (CockroachDB)
```

### Nodos OCI

| VM | IP Pública | IP Privada | Rol |
|----|-----------|------------|-----|
| crdb-node-1 | 129.158.226.108 | 10.0.1.180 | CockroachDB nodo 1 |
| crdb-node-2 | 129.80.104.69 | 10.0.1.160 | CockroachDB nodo 2 |
| crdb-node-3 | 150.136.105.88 | 10.0.1.211 | CockroachDB nodo 3 |
| api-node-1 | 157.151.230.60 | 10.0.1.17 | Spring Boot API |
| monitor-node-1 | 129.153.37.68 | 10.0.1.229 | Prometheus + Grafana + HAProxy |

### Puertos

| Servicio | Puerto | VM |
|---------|--------|----|
| Spring Boot | 8080 | api-node-1 |
| CockroachDB SQL | 26257 | crdb-node-1/2/3 |
| HAProxy | 26258 | monitor-node-1 |
| HAProxy Stats | 8404 | monitor-node-1 |
| Prometheus | 9090 | monitor-node-1 |
| Grafana | 3000 | monitor-node-1 |
| Node Exporter | 9100 | todas las VMs |

---

## Stack tecnológico

- **Backend:** Spring Boot 4.0.6, Java 25
- **Base de datos:** CockroachDB (compatible con PostgreSQL)
- **Seguridad:** Spring Security + JWT (jjwt 0.12.6)
- **ORM:** Spring Data JPA + Hibernate
- **Rate limiting:** Bucket4j
- **Métricas:** Micrometer + Prometheus + Grafana
- **Seed / datos falsos:** DataFaker
- **Load testing:** Autocannon (Node.js)
- **Build:** Maven Wrapper

---

## Requisitos

- Java 25+
- Maven 3.9+
- CockroachDB corriendo y accesible
- Variable de entorno `JWT_SECRET` con al menos 256 bits

---

## Configuración

Copia `.env.example` a `.env` y ajusta los valores:

```env
DB_URL=jdbc:postgresql://<HAPROXY_IP>:26257/appdb?sslmode=disable
DB_USERNAME=root
DB_PASSWORD=
JWT_SECRET=<your-256-bit-secret>
SPRING_PROFILES_ACTIVE=dev
SERVER_PORT=8080
```

Los perfiles disponibles son:

- `local` — apunta a `localhost:26257`, `ddl-auto: none`
- `dev` — apunta a `10.0.1.180:26257` (HAProxy interno), `ddl-auto: update`

---

## Levantar el backend

```bash
cd backend
./mvnw clean package -DskipTests
java -jar target/crdbcluster-0.0.1-SNAPSHOT.jar
```

O con perfil específico:

```bash
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

---

## Endpoints

### Autenticación — `/api/auth`

| Método | Ruta | Descripción | Auth |
|--------|------|-------------|------|
| POST | `/api/auth/register` | Registrar usuario | Pública |
| POST | `/api/auth/login` | Login, devuelve JWT | Pública |
| GET | `/api/auth/me` | Info del token actual | Bearer |

### Usuarios — `/api/users` _(ADMIN)_

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/users` | Listar todos |
| GET | `/api/users/{id}` | Obtener por ID |
| PUT | `/api/users/{id}` | Actualizar nombre/rol |
| DELETE | `/api/users/{id}` | Eliminar |

### Cuentas — `/api/accounts`

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/accounts` | Crear cuenta |
| GET | `/api/accounts` | Listar todas |
| GET | `/api/accounts/{id}` | Obtener por ID |
| GET | `/api/accounts/user/{userId}/accounts` | Cuentas de un usuario |
| PATCH | `/api/accounts/{id}/status` | Cambiar status |

### Transacciones — `/api/transactions`

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/transactions` | Crear transacción |
| GET | `/api/transactions` | Listar todas |
| GET | `/api/transactions/{id}` | Obtener por ID |
| GET | `/api/transactions/account/{accountId}/transactions` | Por cuenta |

### Seed — `/api/seed` _(ADMIN)_

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/seed/users?count=1000` | Generar usuarios falsos |
| POST | `/api/seed/accounts?count=1000` | Generar cuentas |
| POST | `/api/seed/transactions?count=10000` | Generar transacciones |
| DELETE | `/api/seed/clean` | Limpiar toda la data |

### Benchmark — `/api/benchmark` _(ADMIN, MONITOR)_

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/benchmark/read` | Count de usuarios con latencia |
| POST | `/api/benchmark/write` | Insert de usuario benchmark |
| GET | `/api/benchmark/db-ping` | Ping a la DB (`SELECT 1`) |
| GET | `/api/benchmark/heavy-read` | Count de transacciones |
| GET | `/api/benchmark/random-transaction` | BEGIN + query + COMMIT |

### Status — `/api/status`

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/status` | Estado de la app |
| GET | `/api/status/db` | Verificar conexión a CockroachDB |

### Audit logs — `/api/audit-logs` _(ADMIN)_

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/audit-logs` | Todos los registros |
| GET | `/api/audit-logs/user/{userId}` | Por usuario |

### Actuator (métricas)

```
GET /actuator/health
GET /actuator/prometheus
```

---

## Roles y seguridad

| Rol | Acceso |
|-----|--------|
| `ADMIN` | Todo |
| `USER` | Cuentas, transacciones |
| `MONITOR` | Endpoints de benchmark |

- Autenticación stateless vía JWT en header `Authorization: Bearer <token>`
- Rate limiting: **100 req/min por IP** (Bucket4j)
- Passwords hasheados con BCrypt

---

## Load tests

Requiere Node.js. Instala dependencias:

```bash
cd load-tests
npm install
```

Configura `load-tests/config.js` con la URL base y el usuario de prueba, luego:

```bash
npm run read       # GET /api/benchmark/read, 10 conexiones, 30s
npm run write      # POST /api/benchmark/write, 50 conexiones, 120s
npm run ping       # GET /api/benchmark/db-ping, 50 conexiones, 30s
npm run failover   # GET /api/benchmark/read durante 60s — mata un nodo mientras corre
```

Para usar un token ya generado sin hacer login cada vez:

```bash
JWT_TOKEN=<tu_token> npm run read
```

---

## Scripts de operación

### Deploy del backend

```bash
./scripts/deploy-backend.sh
```

Hace pull, rebuild con Maven y reinicia el servicio `spring-api` en `api-node-1`.

### Backup completo de la DB

```bash
./scripts/backup-full.sh
```

Ejecuta `BACKUP DATABASE appdb INTO 's3://...'` apuntando al Object Storage de OCI.

### Restore

```bash
./scripts/restore.sh 's3://cockroach-backups?...'
```

Hace `DROP DATABASE IF EXISTS appdb CASCADE` y luego `RESTORE DATABASE appdb FROM LATEST IN '...'`.

---

## Monitoreo

| Herramienta | URL |
|-------------|-----|
| Grafana | `http://129.153.37.68:3000` |
| Prometheus | `http://129.153.37.68:9090` |
| HAProxy Stats | `http://129.153.37.68:8404` |
| Spring Actuator | `http://157.151.230.60:8080/actuator/prometheus` |

Métricas personalizadas expuestas vía Micrometer:

- `crdb.seed.users` / `crdb.seed.accounts` / `crdb.seed.transactions`
- `crdb.benchmark.reads` / `crdb.benchmark.writes`
- `crdb.auth.failures`
- `crdb.ratelimit.hits`
- `crdb.db.latency`

---

## Prueba de failover

1. Inicia el test de failover:
   ```bash
   cd load-tests && npm run failover
   ```
2. Mientras corre, detén un nodo de CockroachDB:
   ```bash
   ssh crdb2 "sudo systemctl stop cockroach"
   ```
3. Observa en Grafana y HAProxy Stats cómo el clúster redistribuye el tráfico sin interrumpir las requests.

---

## Equipo J

| Integrante | Rol |
|------------|-----|
| Xavier Sotomayor | Infraestructura OCI / DevOps |
| Paloma Fernandez | Backend Spring Boot |
| Daniela Aldaco | Monitoreo / Grafana |

Compartment OCI: `cockroach-project`
