import { runAuthenticatedTest } from './runner.js'

runAuthenticatedTest({
    title: 'DB ping benchmark',
    path: '/api/benchmark/db-ping',
    method: 'GET',
    connections: 50,
    duration: 30
})