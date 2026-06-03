import { runAuthenticatedTest } from './runner.js'

runAuthenticatedTest({
    title: 'Read benchmark',
    path: '/api/benchmark/read',
    method: 'GET',
    connections: 10,
    duration: 30
})