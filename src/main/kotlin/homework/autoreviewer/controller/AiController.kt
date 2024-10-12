package homework.autoreviewer.controller

import homework.autoreviewer.client.AiResponseHandler
import homework.autoreviewer.core.prefix.preFix
import homework.autoreviewer.core.types.AiName
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Deprecated("응답 테스트를 위해 존재")
@RestController
@RequestMapping("$preFix/ai")
class AiController(
    private val aiResponseHandlers: List<AiResponseHandler>,
) {
    @GetMapping("/test")
    fun test(
        @RequestParam() request: String,
        @RequestParam() model: String,
    ): List<String> {
        return aiResponseHandlers.first { it.getAiName().name == model }.handleResponse(listOf(request))
    }

    @GetMapping("/model")
    fun getModel(): List<AiName> {
        return AiName.entries
    }
}
