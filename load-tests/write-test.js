import { runAuthenticatedTest } from './runner.js'

runAuthenticatedTest({
    title: 'Write benchmark',
    path: '/api/benchmark/write',
    method: 'POST',
    connections: 50,
    duration: 120
})