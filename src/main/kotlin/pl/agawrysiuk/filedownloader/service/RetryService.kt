package pl.agawrysiuk.filedownloader.service

import io.github.resilience4j.retry.Retry
import io.github.resilience4j.retry.RetryConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.IOException
import java.time.Duration
import java.util.UUID

@Service
class RetryService {

    @Value("\${app.retry.max-attempts:3}")
    private var maxAttempts: Int = 3

    @Value("\${app.retry.delay:2000}")
    private var delay: Long = 2000

    fun <T> retry(action: () -> T): T {
        return Retry.of(UUID.randomUUID().toString(), retryConfig<T>())
            .executeCallable {
                action()
            }
    }

    fun <T> retryConfig(): RetryConfig {
        return RetryConfig.custom<T>()
            .maxAttempts(maxAttempts)
            .waitDuration(Duration.ofMillis(delay))
            .retryExceptions(IOException::class.java)
            .build()
    }
}
