package pl.agawrysiuk.filedownloader.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "app.service.s3")
class S3Properties {
    lateinit var endpoint: String
    lateinit var bucket: String
    lateinit var region: String
    lateinit var accessKey: String
    lateinit var secretKey: String
}
