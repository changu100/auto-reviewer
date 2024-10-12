package homework.autoreviewer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AutoReviewerApplication

fun main(args: Array<String>) {
    runApplication<AutoReviewerApplication>(*args)
}
