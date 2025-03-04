package pl.agawrysiuk.filedownloader.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun api(): OpenAPI =
        OpenAPI()
            .info(
                Info().title("File Download Service API")
                    .description("API for downloading files to the host system")
                    .version("v1.0")
            )
}