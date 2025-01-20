
# File Download Service

A Spring Boot application to handle file downloads from specified URLs, with retry logic and file storage support.

## Features

- Accepts file download requests via REST API.
- Allows retry on failure (configurable retry attempts and delay).
- Supports storing downloaded files to a specified directory.
- Generates logs for download progress and errors.
- Swagger API documentation for easy testing and interaction.

## Endpoints

### POST /api/files/download

#### Request Body:
```json
{
  "links": [
    {
      "link": "http://example.com/file1.txt",
      "subFilePath": "subdir"
    },
    {
      "link": "http://example.com/file2.jpg",
      "subFilePath": "images"
    }
  ],
  "targetDir": "/path/to/download/directory"
}
```

#### Response:
```json
{
  "results": [
    {
      "link": "http://example.com/file1.txt",
      "success": true,
      "filePath": "/path/to/download/directory/subdir/file1.txt"
    },
    {
      "link": "http://example.com/file2.jpg",
      "success": false,
      "message": "Error downloading file"
    }
  ]
}
```

## Configuration

- **Retry settings**: Configured via `application.properties`
    - Max attempts: 3
    - Delay: 2000ms

## Docker

### Dockerfile
Build and run the application in Docker:
```bash
docker build --tag 'file-downloader'
docker run -v /your-path:/data -p 8080:8080 file-downloader
```

### Docker Compose
Run `docker-compose.yml` file - replace `your-path` to the path in your PC.
```bash
docker-compose up --build
```

## Swagger Documentation

Access the API documentation at `/swagger-ui.html`.

## Running the Application

Run the application using the following command:

```bash
./gradlew bootRun
```

## Tests

Test cases are available for both successful and failed file downloads. To run tests:

```bash
./gradlew test
```

## Dependencies

- Spring Boot
- Kotlin
- Springdoc OpenAPI
- Resilience4j for retry logic
- MockK for testing

## License

This project is licensed under the MIT License.
