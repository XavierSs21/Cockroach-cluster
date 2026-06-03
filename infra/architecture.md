# Infraestructura OCI — Equipo J

## Compartment
`cockroach-project`

## VMs

| VM | IP Pública | IP Privada | Rol |
|----|-----------|------------|-----|
| crdb-node-1 | 129.158.226.108 | 10.0.1.180 | CockroachDB nodo 1 |
| crdb-node-2 | 129.80.104.69 | 10.0.1.160 | CockroachDB nodo 2 |
| crdb-node-3 | 150.136.105.88 | 10.0.1.211 | CockroachDB nodo 3 |
| api-node-1 | 157.151.230.60 | 10.0.1.17 | Spring Boot API |
| monitor-node-1 | 129.153.37.68 | 10.0.1.229 | Prometheus + Grafana + HAProxy |

## OCI Load Balancer
`cockroach-lb` → IP pública: `129.153.133.42`

## Puertos

| Servicio | Puerto | VM |
|---------|--------|----|
| Spring Boot | 8080 | api-node-1 |
| CockroachDB SQL | 26257 | crdb-node-1/2/3 |
| HAProxy | 26258 | monitor-node-1 |
| HAProxy Stats | 8404 | monitor-node-1 |
| Prometheus | 9090 | monitor-node-1 |
| Grafana | 3000 | monitor-node-1 |
| Node Exporter | 9100 | todas las VMs |

## Flujo de tráfico

```
Internet → OCI LB (129.153.133.42:80)
        → api-node-1:8080 (Spring Boot)
        → monitor-node-1:26258 (HAProxy)
        → crdb-node-1/2/3:26257 (CockroachDB)
```