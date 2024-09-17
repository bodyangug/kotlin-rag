# Chatbot API with Ktor and LangChain4j

Welcome to the Chatbot API project! This application is built using the Ktor framework in Kotlin and leverages
LangChain4j for advanced language model capabilities. The API provides endpoints for users to interact with an AI
assistant and upload context documents to enhance the assistant's responses.

## Features
- AI Assistant Endpoint: Interact with an AI assistant using natural language queries.
- Context Upload: Upload documents to provide context for the assistant.
- Dynamic Context Retrieval: Retrieve relevant context during conversations using embeddings.
- Session Management: Maintain conversation history using unique chat IDs.
- Dependency Injection: Utilize Koin for managing dependencies and configurations.
- Document Parsing: Use Apache Tika for parsing various document formats.

## Architecture
The application is structured around Ktor's routing system, with endpoints for handling user interactions. Key components include:

- Assistant: Handles user queries and provides responses using the AI model.
- EmbeddingStore: Stores and retrieves embeddings for context documents.
- Ingestor: Processes uploaded documents and adds them to the embedding store.
- Dependency Injection: Managed by Koin to provide instances of services and configurations.

## Prerequisites

- Kotlin: Version 2.0.0
- Java: JDK 11
- Gradle: Version 8.6 or higher.
- Azure OpenAI Service: Access to Azure OpenAI models like gpt-4 and text-embedding-ada-002.
- Docker: For running ChromaDB container.
- Postman: For testing API endpoints (optional).

## Running the Application
Run the application using Gradle:
```shell
./gradlew run
```
The server will start on the configured host and port (default is http://0.0.0.0:8080).

## Contributing
Contributions are welcome! Please follow these steps:

- Fork the Repository: Create your own fork of the project.
- Create a Branch: Work on your feature or bugfix in a new branch.
- Submit a Pull Request: When ready, submit a PR with a clear description of your changes.

## License
This project is licensed under the MIT License.