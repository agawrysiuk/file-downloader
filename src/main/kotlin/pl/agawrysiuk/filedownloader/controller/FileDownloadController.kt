package pl.agawrysiuk.filedownloader.controller

import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import pl.agawrysiuk.filedownloader.dto.FileDownloadRequestDTO
import org.springframework.web.bind.annotation.*
import pl.agawrysiuk.filedownloader.dto.FileDownloadResponse
import pl.agawrysiuk.filedownloader.service.FileDownloadService

@RestController
@RequestMapping("/api/files")
class FileDownloadController(
    private val fileDownloadService: FileDownloadService
) {

    @PostMapping("/download")
    fun downloadFiles(@Valid @RequestBody request: FileDownloadRequestDTO): ResponseEntity<FileDownloadResponse> {
        val results = fileDownloadService.downloadFiles(request)
        val response = FileDownloadResponse(results)
        return when {
            results.all { it.success } -> ResponseEntity.ok(response)
            results.all { !it.success } -> ResponseEntity.status(HttpStatus.CONFLICT).body(response)
            else -> ResponseEntity.status(HttpStatus.MULTI_STATUS).body(response)
        }
    }
}