package pl.agawrysiuk.filedownloader.service

import org.springframework.stereotype.Service
import pl.agawrysiuk.filedownloader.config.S3Properties
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest

@Service
class S3FileSaverService(
    private val s3Client: S3Client,
    private val s3Properties: S3Properties
) : FileSaverService {

    override val name: String = NAME

    override fun save(fileName: String, subFilePath: String?, fileContent: ByteArray): String {
        val objectName = subFilePath?.let { "$it/$fileName" } ?: fileName

        try {
            s3Client.putObject(
                PutObjectRequest.builder()
                    .bucket(s3Properties.bucket)
                    .key(objectName)
                    .build(),
                RequestBody.fromBytes(fileContent)
            )

            return "${s3Properties.endpoint}/${s3Properties.bucket}/$objectName"
        } catch (e: Exception) {
            throw RuntimeException("Error saving file to S3 mock: ${e.message}", e)
        }
    }


    companion object {
        private const val NAME: String = "s3"
    }
}
