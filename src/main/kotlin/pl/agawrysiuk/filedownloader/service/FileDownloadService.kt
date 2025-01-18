package pl.agawrysiuk.filedownloader.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import pl.agawrysiuk.filedownloader.dto.FileDownloadRequestDTO
import pl.agawrysiuk.filedownloader.dto.FileDownloadResult
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.URL
import java.nio.file.Paths


@Service
class FileDownloadService(
    private val retryService: RetryService
) {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    fun downloadFiles(request: FileDownloadRequestDTO): List<FileDownloadResult> {
        val results = mutableListOf<FileDownloadResult>()

        request.links.forEach { linkDTO ->
            val targetDir = Paths.get(request.targetDir, linkDTO.subFilePath ?: "")
            val fileName = URL(linkDTO.link).file
                .substring(URL(linkDTO.link).file.lastIndexOf('/') + 1)

            results.add(retryService.retry {
                downloadFile(
                    linkDTO.link,
                    fileName,
                    targetDir.toFile()
                )
            })
        }

        return results
    }

    private fun downloadFile(url: String, fileName: String, downloadDir: File): FileDownloadResult {
        try {
            val website = URL(url)
            val connection = website.openConnection()
            val totalSize = connection.contentLength
            if (totalSize <= 0) {
                logger.warn { "Total size not available or invalid. Progress cannot be tracked." }
            }
            val inputStream: InputStream = connection.getInputStream()

            if (!downloadDir.exists()) downloadDir.mkdirs()
            val outputFile = File(downloadDir, fileName)

            val buffer = ByteArray(4096)
            var bytesRead: Int
            var downloadedSize = 0

            FileOutputStream(outputFile).use { output ->
                inputStream.use { input ->
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        downloadedSize += bytesRead

                        if (totalSize > 0) {
                            printProgress(downloadedSize, totalSize, fileName)
                            print("\n")
                        }
                    }
                }
            }
            logger.info { "File downloaded successfully: ${outputFile.absolutePath}" }
            return FileDownloadResult(url, success = true, filePath = outputFile.absolutePath)
        } catch (e: Exception) {
            logger.error { "Failed to download ${url}: ${e.message}" }
            e.printStackTrace()
            return FileDownloadResult(url, success = false, message = "${e::class}: ${e.message}")
        }
    }

    private fun printProgress(downloadedSize: Int, totalSize: Int, fileName: String) {
        val progress = (downloadedSize.toDouble() / totalSize * 100).toInt()
        print("\rDownloading: $fileName [${"=".repeat(progress / 2)}${" ".repeat(50 - progress / 2)}] $progress%")
        System.out.flush()
    }
}