package pl.agawrysiuk.filedownloader.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.system.CapturedOutput
import org.springframework.boot.test.system.OutputCaptureExtension
import pl.agawrysiuk.filedownloaderdto.DownloadLinkDTO
import pl.agawrysiuk.filedownloaderdto.FileDownloadRequestDTO

@ExtendWith(OutputCaptureExtension::class)
class FileDownloadServiceTest {

    private lateinit var saverMock: FileSaverService
    private lateinit var fileDownloadService: FileDownloadService

    @BeforeEach
    fun setUp() {
        saverMock = mockk<FileSaverService>() {
            every { save(any(), any(), any()) } returnsArgument (0)
            every { name } returns "mockk"
        }
        fileDownloadService = FileDownloadService(RetryService(), listOf(saverMock), "mockk")
    }

    @Test
    fun `should download file successfully and not show downloading progress for file without content length`(output: CapturedOutput) {
        val link = DownloadLinkDTO(link = "https://example-files.online-convert.com/document/txt/example.txt")
        val result = fileDownloadService.downloadFiles(FileDownloadRequestDTO(listOf(link), FAKE_DIR_PATH))
        assertTrue(result.first().success)
        assertEquals("example.txt", result.first().filePath)
        assertFalse(output.contains("Downloading:"))
        assertFalse(output.contains("100%"))
        assertTrue(output.contains("Total size not available or invalid. Progress cannot be tracked."))
        verify { saverMock.save("example.txt", null, any()) }
    }

    @Test
    fun `should download file successfully and show downloading progress`(output: CapturedOutput) {
        val link = DownloadLinkDTO(link = "http://research.nhm.org/pdfs/10840/10840-001.pdf")
        val result = fileDownloadService.downloadFiles(FileDownloadRequestDTO(listOf(link), FAKE_DIR_PATH))
        assertTrue(result.first().success)
        assertEquals("10840-001.pdf", result.first().filePath)
        assertTrue(output.contains("Downloading: 10840-001.pdf"))
        assertTrue(output.contains("100%"))
        verify { saverMock.save("10840-001.pdf", null, any()) }
    }

    @Test
    fun `should inform about failure`() {
        val link = DownloadLinkDTO(link = "https://example-files.online-convert.com/document/txt/example2.txt")
        val result = fileDownloadService.downloadFiles(FileDownloadRequestDTO(listOf(link), FAKE_DIR_PATH))
        assertTrue(!result.first().success)
        assertNotNull(result.first().message)
        assertTrue(result.first().message!!.startsWith("class java.io.FileNotFoundException: https://example-files.online-convert.com/document/txt/example2.txt"))
        verify(exactly = 0) { saverMock.save("example2.txt", any(), any()) }
    }

    @Test
    fun `should download file successfully and put it into the subdirectory`() {
        val subFilePath = "new"
        val link = DownloadLinkDTO(
            link = "https://example-files.online-convert.com/document/txt/example.txt",
            subFilePath = subFilePath
        )
        val result = fileDownloadService.downloadFiles(FileDownloadRequestDTO(listOf(link), subFilePath))
        assertEquals("example.txt", result.first().filePath)
        verify { saverMock.save("example.txt", subFilePath, any()) }
    }

    companion object {
        private const val FAKE_DIR_PATH = "/fake/path/to/file"
    }
}