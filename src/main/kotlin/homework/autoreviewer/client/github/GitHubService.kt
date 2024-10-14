package homework.autoreviewer.client.github

import homework.autoreviewer.client.ClientTemplate
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
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
data class GitHubReviewCreateResponse(
    val id: Long,
    @SerialName("node_id")
    val nodeId: String,
    val body: String,
    @SerialName("commit_id")
    val commitId: String,
)

@Serializable
data class GitHubPullRequestFilesResponse(
    val filename: String,
    val additions: Int,
    val patch: String,
)

@Component
class GitHubService(
    private val clientTemplate: ClientTemplate,
    @Value("\${github.api-key}") private val apiKey: String,
    @Value("\${reviewer.review-target.file-extension}") private val reviewTargetFileExtensions: List<String>,
) {
    fun getFileChanges(url: String): List<String> {
        return runBlocking {
            val httpResponse: HttpResponse =
                // https://docs.github.com/ko/rest/pulls/pulls?apiVersion=2022-11-28#list-pull-requests-files
                clientTemplate.getHttpClient().get("$url/files") {
                    header("Accept", "application/vnd.github+json")
                    bearerAuth(apiKey)
                    header("X-GitHub-Api-Version", "2022-11-28")
                    contentType(ContentType.Application.Json)
                }
            val response: List<GitHubPullRequestFilesResponse> = httpResponse.body()
            response.filter { file -> reviewTargetFileExtensions.any { file.filename.endsWith(it) } }
                .map { file -> file.patch }
        }
    }

    fun addComment(
        url: String,
        comment: String,
    ) {
        val reviewCreateResponse = getPullRequestUrl(url, comment)
        submitReview(url, reviewCreateResponse.id)
    }

    fun submitReview(url: String, reviewId: Long) {
        runBlocking {
            clientTemplate.getHttpClient().post("$url/reviews/$reviewId/events") {
                header("Accept", "application/vnd.github+json")
                bearerAuth(apiKey)
                header("X-GitHub-Api-Version", "2022-11-28")
                contentType(ContentType.Application.Json)
                setBody(mapOf("event" to "COMMENT"))
            }
        }
    }

    private fun getPullRequestUrl(url: String, comment: String): GitHubReviewCreateResponse {
        val body = mapOf(
            "body" to comment
        )

        return runBlocking {
            val httpResponse: HttpResponse =
                clientTemplate.getHttpClient().post("$url/reviews") {
                    header("Accept", "application/vnd.github+json")
                    bearerAuth(apiKey)
                    header("X-GitHub-Api-Version", "2022-11-28")
                    contentType(ContentType.Application.Json)
                    setBody(body)
                }
            val response: GitHubReviewCreateResponse = httpResponse.body()
            response
        }
    }
}