# CockroachDB HA/FT Cluster — Equipo J

API REST sobre un clúster distribuido de CockroachDB desplegado en Oracle Cloud Infrastructure, con alta disponibilidad, failover automático, monitoreo completo y load testing.

---

## Integrantes

| Nombre | Rol principal |
|--------|---------------|
| Xavier | Security, Benchmark, Prometheus metrics, DevOps |
| Paloma | CRUD endpoints (Users, Accounts, Transactions, Audit logs, Seed) |
| Daniela | Infraestructura OCI, Networking, CockroachDB setup |

---

## Arquitectura general

```
Internet
  ↓
OCI Load Balancer  129.153.133.42:80
  ↓
api-node-1:8080  (Spring Boot)
  ↓
monitor-node-1:26258  (HAProxy — Round Robin)
  ↓  ↓  ↓
crdb-node-1  crdb-node-2  crdb-node-3  (CockroachDB Cluster, Raft)
  ↓
OCI Object Storage  (backups diarios)

Prometheus :9090 ← scraping 5 VMs + Spring Boot
Grafana    :3000 ← dashboards
```

### Nodos OCI (Ubuntu 22.04, VM.Standard.A1.Flex ARM)

| VM | IP Pública | IP Privada | Rol |
|----|-----------|------------|-----|
| crdb-node-1 | 129.158.226.108 | 10.0.1.180 | CockroachDB nodo 1 |
| crdb-node-2 | 129.80.104.69 | 10.0.1.160 | CockroachDB nodo 2 |
| crdb-node-3 | 150.136.105.88 | 10.0.1.211 | CockroachDB nodo 3 |
| api-node-1 | 157.151.230.60 | 10.0.1.17 | Spring Boot API |
| monitor-node-1 | 129.153.37.68 | 10.0.1.229 | Prometheus + Grafana + HAProxy |

### Puertos

| Servicio | Puerto | Dónde |
|---------|--------|-------|
| OCI Load Balancer | 80 | Público |
| Spring Boot | 8080 | api-node-1 |
| CockroachDB SQL | 26257 | crdb-node-1/2/3 |
| HAProxy (SQL proxy) | 26258 | monitor-node-1 |
| HAProxy Stats | 8404 | monitor-node-1 |
| Prometheus | 9090 | monitor-node-1 |
| Grafana | 3000 | monitor-node-1 |
| Node Exporter | 9100 | todas las VMs |

---

## Estructura del repositorio

```
Cockroach-cluster/
├── backend/          — Spring Boot app (Java 25, Maven)
├── infra/            — Documentación de infraestructura OCI
├── monitoring/       — prometheus.yml, docker-compose.yml
├── haproxy/          — haproxy.cfg
├── scripts/          — backup-full.sh, restore.sh, deploy-backend.sh
├── sql/              — DDL y queries de referencia
├── load-tests/       — Scripts Autocannon (Node.js)
├── docs/             — Evidencias y documentación técnica
├── .env.example      — Variables de entorno sin valores reales
└── .gitignore
```

---

## Stack tecnológico

| Capa | Tecnología |
|------|-----------|
| Lenguaje | Java 25 |
| Framework | Spring Boot 4.0.6 |
| Base de datos | CockroachDB v23.2.0 (compatible PostgreSQL) |
| ORM | Spring Data JPA + Hibernate |
| Seguridad | Spring Security + JWT (jjwt 0.12.6) |
| Rate limiting | Bucket4j |
| Métricas | Micrometer + Prometheus + Grafana |
| Seed | DataFaker 2.4.4 |
| Load testing | Autocannon (Node.js) |
| Build | Maven Wrapper |

---

## Requisitos

- Java 25+
- Maven 3.9+ (o usar `./mvnw`)
- CockroachDB corriendo y accesible
- Node.js 18+ (solo para load tests)

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

### Perfiles Spring

| Perfil | DB | ddl-auto |
|--------|----|----------|
| `local` | `localhost:26257` | none |
| `dev` | `10.0.1.180:26257` (HAProxy OCI) | update |

En producción las variables viven en `/etc/spring-api.env` — nunca en el repositorio.

---

## Levantar el backend

```bash
cd backend

# Compilar
./mvnw clean package -DskipTests

# Correr
java -jar target/crdbcluster-0.0.1-SNAPSHOT.jar

# O directamente con Maven
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

Hibernate crea las tablas automáticamente al iniciar (`ddl-auto: update`).

**Tablas que se generan:**
- `users` — UUID, name, email, password_hash, role, active, timestamps
- `accounts` — UUID, user_id FK, balance, status, timestamps
- `transactions` — UUID, account_id FK, amount, type, status, description, created_at
- `audit_logs` — UUID, user_id FK, action, endpoint, ip_address, created_at

---

## API — Endpoints

### Autenticación — `/api/auth` (pública)

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/auth/register` | Registro con BCrypt, retorna JWT |
| POST | `/api/auth/login` | Login, retorna JWT + email + rol |
| GET | `/api/auth/me` | Info del token actual |

**Ejemplo de login:**
```bash
curl -X POST http://129.153.133.42/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"xavier@prod.com","password":"123456"}'
```

Todas las demás rutas requieren el header:
```
Authorization: Bearer <token>
```

---

### Usuarios — `/api/users` _(ADMIN)_

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/users` | Listar todos los usuarios |
| GET | `/api/users/{id}` | Obtener usuario por ID |
| PUT | `/api/users/{id}` | Actualizar nombre y/o rol |
| DELETE | `/api/users/{id}` | Eliminar usuario |

---

### Cuentas — `/api/accounts` _(autenticado)_

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/accounts` | Crear cuenta |
| GET | `/api/accounts` | Listar todas |
| GET | `/api/accounts/{id}` | Obtener por ID |
| GET | `/api/accounts/user/{userId}/accounts` | Cuentas de un usuario |
| PATCH | `/api/accounts/{id}/status` | Cambiar status (ACTIVE / INACTIVE) |

---

### Transacciones — `/api/transactions` _(ADMIN, USER)_

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | `/api/transactions` | Crear transacción |
| GET | `/api/transactions` | Listar todas |
| GET | `/api/transactions/{id}` | Obtener por ID |
| GET | `/api/transactions/account/{accountId}/transactions` | Transacciones de una cuenta |

---

### Audit Logs — `/api/audit-logs` _(ADMIN)_

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/audit-logs` | Todos los registros de auditoría |
| GET | `/api/audit-logs/user/{userId}` | Logs de un usuario específico |

---

### Seed de datos — `/api/seed` _(ADMIN)_

Genera datos falsos realistas con DataFaker. El orden correcto es: usuarios → cuentas → transacciones.

| Método | Ruta | Descripción | Default |
|--------|------|-------------|---------|
| POST | `/api/seed/users?count=N` | Generar N usuarios | 1,000 |
| POST | `/api/seed/accounts?count=N` | Generar N cuentas | 1,000 |
| POST | `/api/seed/transactions?count=N` | Generar N transacciones | 10,000 |
| DELETE | `/api/seed/clean` | Limpiar toda la data (FK order) | — |

Límites: usuarios y cuentas hasta 100,000 — transacciones hasta 1,000,000.

---

### Benchmark — `/api/benchmark` _(ADMIN, MONITOR)_

Mide latencia real de operaciones contra CockroachDB.

| Método | Ruta | Qué mide |
|--------|------|---------|
| GET | `/api/benchmark/read` | `COUNT(*) FROM users` |
| POST | `/api/benchmark/write` | `INSERT INTO users` |
| GET | `/api/benchmark/db-ping` | `SELECT 1` |
| GET | `/api/benchmark/heavy-read` | `COUNT(*) FROM transactions` |
| GET | `/api/benchmark/random-transaction` | `BEGIN + SELECT + COMMIT` |

Todos retornan `{ operation, latency_ms, timestamp, ... }`.

---

### Status — `/api/status` (pública)

| Método | Ruta | Descripción |
|--------|------|-------------|
| GET | `/api/status` | Estado de la app |
| GET | `/api/status/db` | Verifica conexión a CockroachDB — retorna 503 si falla |

---

### Actuator / Métricas

| Ruta | Descripción |
|------|-------------|
| `/actuator/health` | Health check (público) — usado por OCI LB |
| `/actuator/prometheus` | Endpoint de métricas para Prometheus |

**Métricas custom expuestas:**

| Métrica | Descripción |
|---------|-------------|
| `crdb.seed.users` | Total de usuarios sembrados |
| `crdb.seed.accounts` | Total de cuentas sembradas |
| `crdb.seed.transactions` | Total de transacciones sembradas |
| `crdb.benchmark.reads` | Total de reads ejecutados |
| `crdb.benchmark.writes` | Total de writes ejecutados |
| `crdb.auth.failures` | Fallos de autenticación |
| `crdb.ratelimit.hits` | Requests bloqueados por rate limit |
| `crdb.db.latency` | Latencia de queries a la DB |

---

## Seguridad

| Rol | Acceso |
|-----|--------|
| `ADMIN` | Todos los endpoints |
| `USER` | Cuentas y transacciones propias |
| `MONITOR` | Endpoints de benchmark |

- Autenticación stateless via JWT (1 día de expiración)
- Passwords hasheados con BCrypt
- **Rate limiting: 100 req/min por IP** — exceder retorna `429 Too Many Requests`
- Respuestas de error 401/403 siempre en JSON

---

## Load Tests (Autocannon)

```bash
cd load-tests
npm install
```

| Comando | Escenario | Conexiones | Duración |
|---------|-----------|-----------|---------|
| `npm run read` | GET benchmark/read | 10 | 30s |
| `npm run write` | POST benchmark/write | 50 | 120s |
| `npm run ping` | GET benchmark/db-ping | 50 | 30s |
| `npm run failover` | GET benchmark/read + matar un nodo | 20 | 60s |

Para reutilizar un token sin hacer login en cada ejecución:

```bash
JWT_TOKEN=<tu_token> npm run read
```

**Resultados base (read-test):**
- Latencia promedio: ~59ms
- Throughput: ~166 req/s
- Rate limiting confirmado con respuestas 429 al exceder 100 req/min

---

## Prueba de Failover

1. Inicia el test de failover:
   ```bash
   cd load-tests && npm run failover
   ```

2. Mientras corre, apaga un nodo de CockroachDB:
   ```bash
   ssh crdb2 "sudo systemctl stop cockroach"
   ```

3. Observa en tiempo real:
   - **Grafana** → dashboard Cluster Health
   - **HAProxy Stats** → `http://129.153.37.68:8404/stats`
   - **Autocannon** → las requests continúan sin errores

4. Al reiniciar el nodo, HAProxy lo reincorpora automáticamente sin intervención.

**Comportamiento validado:** HAProxy detecta el nodo DOWN via TCP health check y redistribuye el tráfico entre los 2 nodos restantes. CockroachDB mantiene quórum Raft con 2 de 3 nodos.

---

## Monitoreo

| Herramienta | URL |
|-------------|-----|
| Grafana | `http://129.153.37.68:3000` |
| Prometheus | `http://129.153.37.68:9090` |
| HAProxy Stats | `http://129.153.37.68:8404/stats` |
| CockroachDB Admin UI | `http://129.158.226.108:8080` |
| Spring Actuator | `http://157.151.230.60:8080/actuator/prometheus` |

**Targets en Prometheus (todos UP):**
- `cockroach-node-1/2/3` → `*:8080/_status/vars`
- `node-exporter` → 5 instancias en `:9100` (CPU, RAM, disco, red)
- `spring-boot` → `10.0.1.17:8080/actuator/prometheus`

**Dashboards Grafana:**
- *Cluster Health* — estado de nodos, conexiones SQL activas, latencia
- *Infrastructure Monitoring* — CPU, RAM, disco, red de todas las VMs

---

## Scripts de operación

### Deploy del backend

```bash
./scripts/deploy-backend.sh
```

Ejecuta `git pull → rm target → mvn package → systemctl restart spring-api` en `api-node-1`.

### Backup completo

```bash
./scripts/backup-full.sh
```

Ejecuta `BACKUP DATABASE appdb INTO 's3://cockroach-backups/...'` hacia OCI Object Storage. Cron configurado diariamente a las 2am.

### Restore

```bash
./scripts/restore.sh 's3://cockroach-backups?AWS_ACCESS_KEY_ID=<KEY>&...'
```

Hace `DROP DATABASE IF EXISTS appdb CASCADE` y luego `RESTORE DATABASE appdb FROM LATEST IN '...'`.

---

## Infraestructura OCI — Detalle

**Identity & Access Management:**
- Usuarios IAM: `xavier`, `daniela`, `paloma`
- Grupo `dba-team` con policy de administración del compartment `cockroach-project`
- Presupuesto con alerta al 80%

**Networking:**
- VCN `cockroach-vcn` — CIDR `10.0.0.0/16`
- Subnet pública `cockroach-subnet-public` — CIDR `10.0.1.0/24`
- Internet Gateway + Route Table `0.0.0.0/0 → cockroach-igw`
- Security List con puertos: 22, 80, 8080, 26257, 8404, 3000, 9090, 9100

**OCI Load Balancer:**
- `cockroach-lb` — IP `129.153.133.42`, listener HTTP:80
- Backend `api-node-1:8080`
- Health check → `/actuator/health`

**Object Storage:**
- Bucket `cockroach-backups` — namespace `idenm2iuwhbz`, región `us-ashburn-1`
- Acceso vía compatibilidad S3 con Customer Secret Keys

---

## CockroachDB — Setup del cluster

```
crdb-node-1  →  cockroach start --join=10.0.1.180,10.0.1.160,10.0.1.211
crdb-node-2  →  cockroach start --join=10.0.1.180,10.0.1.160,10.0.1.211
crdb-node-3  →  cockroach start --join=10.0.1.180,10.0.1.160,10.0.1.211

cockroach init --insecure --host=10.0.1.180:26257
```

- Modo insecure (ambiente académico)
- Replication factor 3 (default)
- Consenso Raft — quórum con 2 de 3 nodos
- Systemd con `Restart=always`
