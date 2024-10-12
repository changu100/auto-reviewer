package homework.autoreviewer.client

import io.ktor.client.HttpClient

interface ClientTemplate {
    fun getHttpClient(): HttpClient
}