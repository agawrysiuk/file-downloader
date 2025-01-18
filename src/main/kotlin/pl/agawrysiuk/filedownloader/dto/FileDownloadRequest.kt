package pl.agawrysiuk.filedownloader.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import pl.agawrysiuk.filedownloader.annotations.NullableNotBlank

data class FileDownloadRequestDTO(
    @field:NotEmpty @field:Valid val links: List<DownloadLinkDTO>,
    @field:NotBlank val targetDir: String
)

data class DownloadLinkDTO(
    @field:NotBlank val link: String,
    @field:NullableNotBlank val subFilePath: String? = null
)