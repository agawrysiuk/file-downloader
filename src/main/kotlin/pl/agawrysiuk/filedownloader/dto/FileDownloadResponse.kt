package pl.agawrysiuk.filedownloader.dto

data class FileDownloadResponse(
    val results: List<FileDownloadResult>
)

data class FileDownloadResult(
    val link: String,
    val success: Boolean,
    val message: String? = null,
    val filePath: String? = null
)