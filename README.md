# File Watcher Service

This is a Spring Boot application that watches directories for file changes and can log or move files based on configurations. The application provides REST APIs to start and stop watching directories, as well as to list the currently watched directories.

## Features

- Watch multiple directories for file changes.
- Log file creation/modification events.
- Move files from source to target directories.
- REST API for starting/stopping directory watches and listing watched directories.
- Configuration via `application.properties`.

## Requirements

- Java 11
- Maven

## Getting Started

### Clone the Repository

```bash
git clone https://github.com/yourusername/file-watcher-service.git
cd file-watcher-service
```
# File Watcher Service - REST API Endpoints

## Start Watching a Directory

**URL:** `/api/filewatcher/start`  
**Method:** `POST`  
**Request Body:**

```json
{
  "sourceDir": "/path/to/source",
  "targetDir": "/path/to/target",
  "logOnly": false
}
```
**Response:**

```json

{
  "message": "File watch service started for /path/to/source"
}
```

**Example using curl:**
```bash
curl -X POST -H "Content-Type: application/json" -d '{
  "sourceDir": "/path/to/source",
  "targetDir": "/path/to/target",
  "logOnly": false
}' http://localhost:8080/api/filewatcher/start

```