import { API_BASE_URL, TEST_USER } from './config.js'

export async function getToken() {
    if (process.env.JWT_TOKEN) {
        console.log('Usando token desde variable JWT_TOKEN')
        return process.env.JWT_TOKEN
    }

    const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            Accept: 'application/json'
        },
        body: JSON.stringify(TEST_USER)
    })

    const contentType = response.headers.get('content-type') ?? ''
    const rawBody = await response.text()

    if (!response.ok) {
        throw new Error(`
Login falló.

Status: ${response.status}
Content-Type: ${contentType}

Respuesta:
${rawBody.slice(0, 500)}
`)
    }

    const loginResponse = JSON.parse(rawBody)

    if (!loginResponse.token) {
        throw new Error(`
No se pudo obtener token.

Respuesta:
${JSON.stringify(loginResponse, null, 2)}
`)
    }

    console.log('Token generado correctamente')
    console.log('Usuario:', loginResponse.email)
    console.log('Rol:', loginResponse.role)

    return loginResponse.token
}