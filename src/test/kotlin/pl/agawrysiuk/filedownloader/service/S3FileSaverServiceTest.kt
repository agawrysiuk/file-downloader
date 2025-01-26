package pl.agawrysiuk.filedownloader.service

import com.adobe.testing.s3mock.junit5.S3MockExtension
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import pl.agawrysiuk.filedownloader.config.S3Properties
import software.amazon.awssdk.services.s3.model.CreateBucketRequest


class S3FileSaverServiceTest {

    @Test
    fun `should save a file to s3mock`() {
        val s3Client = s3Mock.createS3ClientV2();
        val s3Properties = mockk<S3Properties>() {
            every { endpoint } returns ""
            every { bucket } returns TEST_BUCKET
        }

        s3Client.createBucket(CreateBucketRequest.builder().bucket(TEST_BUCKET).build())

        val s3FileSaverService = S3FileSaverService(s3Client, s3Properties)
        val fileName = "test.txt"
        val fileContent = "Hello, S3Mock!".toByteArray()

        val fileUrl = s3FileSaverService.save(fileName, TEST_SUBPATH, fileContent)

        assertTrue(fileUrl.contains("$TEST_BUCKET/$TEST_SUBPATH/test.txt"))
    }

    companion object {
        @RegisterExtension
        @JvmField
        val s3Mock: S3MockExtension = S3MockExtension.builder().silent().build()

        private const val TEST_BUCKET: String = "test-bucket"
        private const val TEST_SUBPATH: String = "test-path"
    }
}
