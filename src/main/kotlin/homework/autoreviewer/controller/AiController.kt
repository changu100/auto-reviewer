package homework.autoreviewer.controller

import homework.autoreviewer.client.AiResponseHandler
import homework.autoreviewer.client.github.GitHubService
import homework.autoreviewer.controller.request.PullRequestRequest
import homework.autoreviewer.core.prefix.preFix
import homework.autoreviewer.core.types.AiName
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Deprecated("응답 테스트를 위해 존재")
@RestController
@RequestMapping("$preFix/ai")
class AiController(
    private val aiResponseHandlers: List<AiResponseHandler>,
    private val gitHubService: GitHubService,
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

    @PostMapping("/pr")
    @ResponseStatus(HttpStatus.OK)
    fun onPullRequestEvent(@RequestBody request: PullRequestRequest) {
        // PR 생성 이벤트일 때만 처리
        if (request.action == "opened" || request.action == "reopened") {
            val prNumber = request.pullRequest.number
            val url = request.pullRequest.url
            val commitSha = request.pullRequest.head.sha
            gitHubService.addComment(url, commitSha, prNumber, "안녕하세요")
        }
    }
}
