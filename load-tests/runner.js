import autocannon from 'autocannon'
import { getToken } from './auth.js'
import { API_BASE_URL, DEFAULT_CONNECTIONS, DEFAULT_DURATION } from './config.js'

export async function runAuthenticatedTest({
                                               path,
                                               method = 'GET',
                                               connections = DEFAULT_CONNECTIONS,
                                               duration = DEFAULT_DURATION,
                                               body = undefined,
                                               title = 'Autocannon test'
                                           }) {
    const token = await getToken()

    console.log(`\n=== ${title} ===`)
    console.log(`URL: ${API_BASE_URL}${path}`)
    console.log(`Method: ${method}`)
    console.log(`Connections: ${connections}`)
    console.log(`Duration: ${duration}s\n`)

    const instance = autocannon(
        {
            url: `${API_BASE_URL}${path}`,
            method,
            connections,
            duration,
            headers: {
                Authorization: `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body
        },
        (err, result) => {
            if (err) {
                console.error('Error durante la prueba:', err)
                return
            }

            autocannon.printResult(result)
        }
    )

    autocannon.track(instance, {
        renderProgressBar: true
    })
}