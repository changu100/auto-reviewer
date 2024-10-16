package homework.autoreviewer.controller.request

import com.fasterxml.jackson.annotation.JsonProperty
import homework.autoreviewer.service.AutoReviewDto

data class PullRequestRequest(
    val action: String,
    @JsonProperty("pull_request")
    val pullRequest: PullRequest,
    val repository: Repository,
) {
    data class Repository(
        @JsonProperty("full_name")
        val fullName: String,
    )

    data class PullRequest(
        val url: String,
        val number: Int,
        val head: Head,
    ) {
        data class Head(
            val sha: String,
        )
    }

    fun toDto() = AutoReviewDto(
        action = action,
        url = pullRequest.url,
        commitSha = pullRequest.head.sha,
    )
}