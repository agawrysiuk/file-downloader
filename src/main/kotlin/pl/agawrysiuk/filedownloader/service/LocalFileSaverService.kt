package pl.agawrysiuk.filedownloader.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Service
class LocalFileSaverService : FileSaverService {

    @Value("\${app.service.local.path}")
    private var localPath: String? = null

    override fun save(fileName: String, subFilePath: String?, fileContent: ByteArray): String {
        val saveDir = getSaveDir()
        val filePath = getFilePath(subFilePath, saveDir, fileName)

        createDirsIfNotExist(filePath)

        return save(filePath, fileContent)
    }

    private fun getSaveDir(): String = if (localPath.isNullOrBlank()) File(
        System.getProperty("user.home"),
        "Downloads"
    ).absolutePath else localPath!!


    private fun getFilePath(subFilePath: String?, saveDir: String, fileName: String): Path =
        if (subFilePath != null) Paths.get(saveDir, subFilePath, fileName) else Paths.get(saveDir, fileName)


    private fun createDirsIfNotExist(filePath: Path) {
        if (Files.notExists(filePath.parent)) {
            Files.createDirectories(filePath.parent)
        }
    }

    private fun save(filePath: Path, fileContent: ByteArray): String {
        Files.write(filePath, fileContent)
        return filePath.toAbsolutePath().toString()
    }
}
