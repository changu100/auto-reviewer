package homework.autoreviewer.client

import homework.autoreviewer.core.types.AiName

interface AiResponseHandler {
    fun getAiName(): AiName
    fun handleResponse(requests: List<String>): List<String>
}
