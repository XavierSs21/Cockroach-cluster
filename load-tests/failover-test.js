import { runAuthenticatedTest } from './runner.js'

console.log('\n====================================')
console.log('PRUEBA DE FAILOVER')
console.log('====================================')
console.log('Durante la ejecución detén un nodo:')
console.log('')
console.log('ssh crdb2 "sudo systemctl stop cockroach"')
console.log('')
console.log('Observa:')
console.log('- Cockroach Admin UI')
console.log('- Grafana')
console.log('- HAProxy Stats')
console.log('- Resultado Autocannon')
console.log('====================================\n')

await runAuthenticatedTest({
    title: 'Failover benchmark',
    path: '/api/benchmark/read',
    method: 'GET',
    connections: 20,
    duration: 60
})