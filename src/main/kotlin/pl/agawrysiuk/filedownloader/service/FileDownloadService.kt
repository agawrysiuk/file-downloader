package pl.agawrysiuk.filedownloader.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import pl.agawrysiuk.filedownloaderdto.FileDownloadRequestDTO
import pl.agawrysiuk.filedownloaderdto.FileDownloadResult
import java.io.InputStream
import java.net.URL


@Service
class FileDownloadService(
    private val retryService: RetryService,
    private val fileSaverService: FileSaverService,
) {
    companion object {
        private val logger = KotlinLogging.logger {}
    }

    fun downloadFiles(request: FileDownloadRequestDTO): List<FileDownloadResult> {
        val results = mutableListOf<FileDownloadResult>()

        request.links.forEach { linkDTO ->
            val fileName = URL(linkDTO.link).file
                .substring(URL(linkDTO.link).file.lastIndexOf('/') + 1)

            results.add(retryService.retry {
                downloadFile(
                    linkDTO.link,
                    fileName,
                    linkDTO.subFilePath,
                )
            })
        }

        return results
    }

    private fun downloadFile(url: String, fileName: String, subFilePath: String?): FileDownloadResult {
        try {
            val website = URL(url)
            val connection = website.openConnection()
            val totalSize = connection.contentLength
            if (totalSize <= 0) {
                logger.warn { "Total size not available or invalid. Progress cannot be tracked." }
            }
            val inputStream: InputStream = connection.getInputStream()

            val buffer = ByteArray(4096)
            var bytesRead: Int
            var downloadedSize = 0

            val outputStream = mutableListOf<Byte>()
            inputStream.use { input ->
                while (input.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.addAll(buffer.take(bytesRead))
                    downloadedSize += bytesRead

                    if (totalSize > 0) {
                        printProgress(downloadedSize, totalSize, fileName)
                    }
                }
            }

            val savedPath = fileSaverService.save(fileName, subFilePath, outputStream.toByteArray())
            logger.info { "File downloaded successfully: $savedPath" }
            return FileDownloadResult(url, success = true, filePath = savedPath)
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