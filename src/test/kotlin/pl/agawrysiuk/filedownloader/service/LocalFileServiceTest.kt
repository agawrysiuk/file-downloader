package pl.agawrysiuk.filedownloader.service

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

@SpringBootTest
class LocalFileServiceTest {

    @Autowired
    private lateinit var localFileSaverService: LocalFileSaverService

    @Test
    fun `should save file successfully to the given directory`() {
        val result = localFileSaverService.save(fileName, null, byteArray)
        assertEquals("$tempDir/$fileName", result)
        assertTrue(File(result).exists())
        File(result).delete()
    }

    @Test
    fun `should save file successfully to the given subdirectory`() {
        val subFilePath = "new"
        val result = localFileSaverService.save(fileName, subFilePath, byteArray)
        assertEquals("$tempDir/$subFilePath/$fileName", result)
        assertTrue(File(result).exists())
        File(result).delete()
    }

    companion object {
        private val byteArray = byteArrayOf(72, 101, 108, 108, 111) // "Hello"
        private val fileName = "example.txt"
        private lateinit var tempDir: Path

        @JvmStatic
        @DynamicPropertySource
        fun registerProperties(registry: DynamicPropertyRegistry) {
            tempDir = Files.createTempDirectory("temp")
            registry.add("app.service.local.path") { tempDir.toAbsolutePath().toString() }
        }

        @JvmStatic
        @AfterAll
        fun deleteAll() {
            tempDir.toFile().deleteRecursively()
        }
    }
}