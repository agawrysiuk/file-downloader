package pl.agawrysiuk.filedownloader.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import pl.agawrysiuk.filedownloader.service.FileDownloadService
import pl.agawrysiuk.filedownloaderdto.DownloadLinkDTO
import pl.agawrysiuk.filedownloaderdto.FileDownloadRequestDTO
import pl.agawrysiuk.filedownloaderdto.FileDownloadResponse
import pl.agawrysiuk.filedownloaderdto.FileDownloadResult

@WebMvcTest(FileDownloadController::class)
class FileDownloadControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockitoBean
    private lateinit var fileDownloadService: FileDownloadService

    @Test
    fun `should return download results with status ok`() {
        val links = listOf(
            DownloadLinkDTO(link = "http://example.com/file1.txt"),
            DownloadLinkDTO(link = "http://example.com/file2.txt")
        )
        val request = FileDownloadRequestDTO(links, "someDir")
        val results = listOf(
            FileDownloadResult(link = "http://example.com/file1.txt", success = true, filePath = "/tmp/file1.txt"),
            FileDownloadResult(link = "http://example.com/file2.txt", success = true, message = "/tmp/file2.txt")
        )
        val response = FileDownloadResponse(results)

        `when`(fileDownloadService.downloadFiles(request)).thenReturn(results)

        mockMvc.perform(
            post("/api/files/download")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(content().json(objectMapper.writeValueAsString(response)))
    }

    @Test
    fun `should return download results with status mixed`() {
        val links = listOf(
            DownloadLinkDTO(link = "http://example.com/file1.txt"),
            DownloadLinkDTO(link = "http://example.com/file2.txt")
        )
        val request = FileDownloadRequestDTO(links, "someDir")
        val results = listOf(
            FileDownloadResult(link = "http://example.com/file1.txt", success = true, filePath = "/tmp/file1.txt"),
            FileDownloadResult(link = "http://example.com/file2.txt", success = false, message = "Error downloading")
        )
        val response = FileDownloadResponse(results)

        `when`(fileDownloadService.downloadFiles(request)).thenReturn(results)

        mockMvc.perform(
            post("/api/files/download")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isMultiStatus)
            .andExpect(content().json(objectMapper.writeValueAsString(response)))
    }

    @Test
    fun `should return download results with status conflict`() {
        val links = listOf(
            DownloadLinkDTO(link = "http://example.com/file1.txt"),
            DownloadLinkDTO(link = "http://example.com/file2.txt")
        )
        val request = FileDownloadRequestDTO(links, "someDir")
        val results = listOf(
            FileDownloadResult(link = "http://example.com/file1.txt", success = false, filePath = "Error downloading"),
            FileDownloadResult(link = "http://example.com/file2.txt", success = false, message = "Error downloading")
        )
        val response = FileDownloadResponse(results)

        `when`(fileDownloadService.downloadFiles(request)).thenReturn(results)

        mockMvc.perform(
            post("/api/files/download")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isConflict)
            .andExpect(content().json(objectMapper.writeValueAsString(response)))
    }

    @Test
    fun `should throw exception on empty links list`() {
        val request = FileDownloadRequestDTO(emptyList(), "someDir")

        mockMvc.perform(
            post("/api/files/download")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw exception on blank targetDir`() {
        val request = FileDownloadRequestDTO(listOf(DownloadLinkDTO(link = "http://example.com/file1.txt")), "  ")

        mockMvc.perform(
            post("/api/files/download")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw exception on blank download link`() {
        val request = FileDownloadRequestDTO(listOf(DownloadLinkDTO(link = "  ")), "/temp")

        mockMvc.perform(
            post("/api/files/download")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `should throw exception on blank subFilePath`() {
        val request = FileDownloadRequestDTO(
            listOf(DownloadLinkDTO(link = "http://example.com/file1.txt", subFilePath = "  ")),
            "/temp"
        )

        mockMvc.perform(
            post("/api/files/download")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }
}