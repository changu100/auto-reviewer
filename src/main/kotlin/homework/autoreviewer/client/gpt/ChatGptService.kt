package homework.autoreviewer.client.gpt

import homework.autoreviewer.client.AiResponseHandler
import homework.autoreviewer.client.ClientTemplate
import homework.autoreviewer.core.types.AiName
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Serializable
data class ChatGptResponse(
    val id: String,
    val `object`: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>,
) {
    @Serializable
    data class Choice(
        val index: Int,
        val message: ResponseMessage,
        val logprobs: String?,
        @SerialName("finish_reason")
        val finishReason: String?,
    ) {
        @Serializable
        data class ResponseMessage(
            val role: String,
            val content: String,
            val refusal: String?,
        )
    }
}

@Serializable
data class ChatGptRequest(
    val model: String,
    val messages: List<Message>,
    val temperature: Double = 0.7,
)

@Serializable
data class Message(
    val role: String,
    val content: String,
)

@Component
class ChatGptAiResponseHandler(
    private val clientTemplate: ClientTemplate,
    @Value("\${chatgpt.api-key}") private val apiKey: String,
) : AiResponseHandler {
    private val aiName = AiName.CHATGPT
    override fun getAiName() = aiName

    override fun handleResponse(requests: List<String>): List<String> {
        return runBlocking {
            callOpenAiApi(
                requests,
                apiKey
            )
        }
    }

    suspend fun callOpenAiApi(request: List<String>, apiKey: String): List<String> {
        val openAiUrl = "https://api.openai.com/v1/chat/completions"

        // 요청 본문 데이터
        val requestBody = ChatGptRequest(
            model = "gpt-3.5-turbo", // 사용할 모델 선택
            messages = request.map { Message(role = "system", content = it) }
        )

        // API 호출
        val httpResponse: HttpResponse = clientTemplate.getHttpClient().post(openAiUrl) {
            // 이거 왜 동작하지 않지?
            // headers {
            //     append(HttpHeaders.Authorization, "Bearer $apiKey")
            //     append(HttpHeaders.ContentType, "application/json")
            // }
            contentType(ContentType.Application.Json)
            bearerAuth(apiKey)
            setBody(requestBody)
        }

        val response: ChatGptResponse = httpResponse.body()

        // 응답 처리
        return response.choices.map { it.message.content }
    }
}