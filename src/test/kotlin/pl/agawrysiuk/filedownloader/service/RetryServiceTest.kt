package pl.agawrysiuk.filedownloader.service

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.io.IOException

@SpringBootTest
class RetryServiceTest {

    @Autowired
    lateinit var retryService: RetryService

    @Test
    fun `test retry with first failure and second success`() {
        val downloadAction = mockk<() -> String>()
        every { downloadAction() } throws IOException("Network error") andThen "Success"

        val result = retryService.retry(downloadAction)

        assert(result == "Success")
        verify(exactly = 2) { downloadAction() }
    }
}
