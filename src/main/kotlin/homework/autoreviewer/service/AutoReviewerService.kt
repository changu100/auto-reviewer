package homework.autoreviewer.service

import homework.autoreviewer.client.AiResponseHandler
import homework.autoreviewer.client.github.GitHubService
import homework.autoreviewer.core.ai.model.AiModel
import org.springframework.stereotype.Service

data class AutoReviewDto(
    val action: String,
    val url: String,
    val commitSha: String,
)

@Service
class AutoReviewerService(
    private val gitHubService: GitHubService,
    private val aiResponseHandlers: List<AiResponseHandler>,
) {
    fun reviewCode(dto: AutoReviewDto) {
        if (dto.action != "opened" && dto.action != "reopened") {
            return
        }
        val prChanges = gitHubService.getFileChanges(dto.url)

        prChanges.forEach { change ->
            val review =
                aiResponseHandlers.first { it.getAiName() == AiModel.getModel() }
                    .handleResponse(listOf("한글로 code review 작성해줘", change))
            println("review = $review")
            gitHubService.addComment(dto.url, "[Ai Review] \n $review")
        }
    }
}