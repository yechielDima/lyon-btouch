const BASE_URL = '/api'

class ApiClient {
  private async request<T>(path: string, options: RequestInit = {}): Promise<T> {
    const url = `${BASE_URL}${path}`

    const config: RequestInit = {
      ...options,
      credentials: 'include',
      headers: {
        'Content-Type': 'application/json',
        ...options.headers,
      },
    }

    const response = await fetch(url, config)

    if (!response.ok) {
      let message = `שגיאה ${response.status}`
      try {
        const body = await response.json()
        if (body.messages && body.messages.length > 0) {
          message = body.messages[0]
        } else if (body.message) {
          message = body.message
        }
      } catch {
        // ignore parse errors
      }
      throw new Error(message)
    }

    if (response.status === 204) {
      return undefined as T
    }

    return response.json()
  }

  async get<T>(path: string): Promise<T> {
    return this.request<T>(path, { method: 'GET' })
  }

  async post<T>(path: string, body?: unknown): Promise<T> {
    return this.request<T>(path, {
      method: 'POST',
      body: body ? JSON.stringify(body) : undefined,
    })
  }

  async put<T>(path: string, body?: unknown): Promise<T> {
    return this.request<T>(path, {
      method: 'PUT',
      body: body ? JSON.stringify(body) : undefined,
    })
  }
}

export const api = new ApiClient()
