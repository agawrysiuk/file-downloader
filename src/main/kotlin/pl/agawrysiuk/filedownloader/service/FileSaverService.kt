package pl.agawrysiuk.filedownloader.service

interface FileSaverService {
    fun save(fileName: String, subFilePath: String?, fileContent: ByteArray): String
    val name: String
}