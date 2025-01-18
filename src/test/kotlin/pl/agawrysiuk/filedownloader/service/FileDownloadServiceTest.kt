package pl.agawrysiuk.filedownloader.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension
import pl.agawrysiuk.filedownloader.dto.DownloadLinkDTO
import pl.agawrysiuk.filedownloader.dto.FileDownloadRequestDTO
import java.io.File
import java.nio.file.Files

@ExtendWith(OutputCaptureExtension::class)
@SpringBootTest
class FileDownloadServiceTest {

    @Autowired
    private lateinit var fileDownloadService: FileDownloadService

    @Test
    fun `should download file successfully and not show downloading progress for file without content length`(output: CapturedOutput) {
        val tempDir = Files.createTempDirectory("temp")
        val link = DownloadLinkDTO(link = "https://example-files.online-convert.com/document/txt/example.txt")
        val result = fileDownloadService.downloadFiles(FileDownloadRequestDTO(listOf(link), tempDir.toString()))
        assertTrue(result.first().success)
        assertTrue(File(result.first().filePath!!).exists())
        File(result.first().filePath!!).delete()
        tempDir.toFile().delete()
        assertFalse(output.contains("Downloading:"))
        assertFalse(output.contains("100%"))
        assertTrue(output.contains("Total size not available or invalid. Progress cannot be tracked."))
    }

    @Test
    fun `should download file successfully and show downloading progress`(output: CapturedOutput) {
        val tempDir = Files.createTempDirectory("temp")
        val link = DownloadLinkDTO(link = "http://research.nhm.org/pdfs/10840/10840-001.pdf")
        val result = fileDownloadService.downloadFiles(FileDownloadRequestDTO(listOf(link), tempDir.toString()))
        assertTrue(result.first().success)
        assertTrue(File(result.first().filePath!!).exists())
        File(result.first().filePath!!).delete()
        tempDir.toFile().delete()
        assertTrue(output.contains("Downloading: 10840-001.pdf"))
        assertTrue(output.contains("100%"))
    }

    @Test
    fun `should inform about failure`() {
        val tempDir = Files.createTempDirectory("temp")
        val link = DownloadLinkDTO(link = "https://example-files.online-convert.com/document/txt/example2.txt")
        val result = fileDownloadService.downloadFiles(FileDownloadRequestDTO(listOf(link), tempDir.toString()))
        assertTrue(!result.first().success)
        assertNotNull(result.first().message)
        assertTrue(result.first().message!!.startsWith("class java.io.FileNotFoundException: https://example-files.online-convert.com/document/txt/example2.txt"))
        tempDir.toFile().delete()
    }

    @Test
    fun `should download file successfully and put it into the subdirectory`() {
        val subFilePath = "new"
        val tempDir = Files.createTempDirectory("temp").resolve(subFilePath)
        val link = DownloadLinkDTO(
            link = "https://example-files.online-convert.com/document/txt/example.txt",
            subFilePath = subFilePath
        )
        val result = fileDownloadService.downloadFiles(FileDownloadRequestDTO(listOf(link), tempDir.toString()))
        assertTrue(result.first().success)
        assertTrue(File(result.first().filePath!!).exists())
        assertTrue(
            File(result.first().filePath!!).absolutePath.contains("temp") &&
                    File(result.first().filePath!!).absolutePath.contains("new")
        )
        File(result.first().filePath!!).delete()
        tempDir.toFile().delete()
    }
}