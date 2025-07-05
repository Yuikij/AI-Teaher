# AI-Teacher API Documentation

## Overview

AI-Teacher is a Spring Boot application that provides AI-powered educational chat services through OpenAI integration. The application acts as an English teacher AI assistant that can engage in conversations with students, remember context across sessions, and provide both simple and streaming chat responses.

## Table of Contents

1. [Quick Start](#quick-start)
2. [Configuration](#configuration)
3. [Public APIs](#public-apis)
4. [Components](#components)
5. [Entities](#entities)
6. [Configuration Classes](#configuration-classes)
7. [Examples](#examples)
8. [Error Handling](#error-handling)

## Quick Start

### Prerequisites

- Java 17 or later
- Maven 3.6+
- OpenAI API Key or compatible API service

### Running the Application

1. Set your OpenAI API key as an environment variable:
   ```bash
   export AI_OPENAI_API_KEY=your_api_key_here
   ```

2. Build and run the application:
   ```bash
   mvn clean package
   java -jar target/AI-Teaher-1.0.jar
   ```

3. The application will start on port 18080

## Configuration

### Application Properties

The application is configured through `application.yml`:

```yaml
server:
  port: 18080

spring:
  application:
    name: AI-Teaher
  ai:
    dashscope:
      api-key: 111
    openai:
      api-key: ${AI_OPENAI_API_KEY}
      base-url: https://ark.cn-beijing.volces.com/api/
      chat:
        options:
          model: doubao-1-5-lite-32k-250115
        completions-path: /v3/chat/completions
```

### Configuration Parameters

| Parameter | Description | Default Value |
|-----------|-------------|---------------|
| `server.port` | Application port | 18080 |
| `spring.ai.openai.api-key` | OpenAI API Key | From environment variable |
| `spring.ai.openai.base-url` | OpenAI API base URL | https://ark.cn-beijing.volces.com/api/ |
| `spring.ai.openai.chat.options.model` | AI model to use | doubao-1-5-lite-32k-250115 |

## Public APIs

### Base URL
```
http://localhost:18080
```

### 1. Simple Chat API

**Endpoint:** `GET /helloworld/simple/chat`

**Description:** Provides a simple chat interface with the AI teacher.

**Parameters:**
- `query` (optional): The user's question or message
- Default: "你好，很高兴认识你，能简单介绍一下自己吗？"

**Response:** Plain text response from the AI teacher

**Example Request:**
```bash
curl -X GET "http://localhost:18080/helloworld/simple/chat?query=What is the difference between 'a' and 'an'?"
```

**Example Response:**
```
Hello! I'm your English teacher. The difference between 'a' and 'an' is that 'a' is used before words that start with consonant sounds, while 'an' is used before words that start with vowel sounds. For example: 'a book' vs 'an apple'.
```

### 2. Stream Chat API

**Endpoint:** `GET /helloworld/stream/chat`

**Description:** Provides streaming chat responses for real-time interaction.

**Parameters:**
- `query` (optional): The user's question or message
- Default: "你好，很高兴认识你，能简单介绍一下自己吗？"

**Response:** Server-Sent Events (SSE) stream of text chunks

**Example Request:**
```bash
curl -X GET "http://localhost:18080/helloworld/stream/chat?query=Explain the present perfect tense"
```

**Example Response:**
```
The present perfect tense is used to describe actions that started in the past and continue to the present, or actions that happened at an unspecified time in the past...
```

### 3. Advisor Chat API (with Memory)

**Endpoint:** `GET /helloworld/advisor/chat/{id}`

**Description:** Provides chat functionality with conversation memory. The AI will remember previous conversations within the same session ID.

**Path Parameters:**
- `id`: Unique conversation identifier

**Query Parameters:**
- `query`: The user's question or message

**Response:** Server-Sent Events (SSE) stream of text chunks

**Example Request:**
```bash
# First message - introduce yourself
curl -X GET "http://localhost:18080/helloworld/advisor/chat/user123?query=Hello, my name is John"

# Second message - test memory
curl -X GET "http://localhost:18080/helloworld/advisor/chat/user123?query=What is my name?"
```

**Example Response:**
```
First response: Hello John! Nice to meet you. I'm your English teacher...
Second response: Your name is John. How can I help you with your English studies today?
```

## Components

### 1. AiTeaherApplication

**Package:** `com.soukon.aiteaher`

**Description:** Main Spring Boot application class that serves as the entry point.

**Usage:**
```java
@SpringBootApplication
public class AiTeaherApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiTeaherApplication.class, args);
    }
}
```

### 2. HelloworldController

**Package:** `com.soukon.aiteaher.controller`

**Description:** REST controller that handles all chat-related endpoints.

**Features:**
- Simple chat functionality
- Streaming chat responses
- Memory-enabled conversations
- Configurable AI model options

**Key Components:**
- `DEFAULT_PROMPT`: Default system prompt for the AI teacher
- `openAiChatClient`: Configured ChatClient with memory and logging advisors

**Constructor:**
```java
public HelloworldController(ChatModel openAiChatModel) {
    this.openAiChatClient = ChatClient.builder(openAiChatModel)
        .defaultAdvisors(
            new MessageChatMemoryAdvisor(new InMemoryChatMemory())
        )
        .defaultAdvisors(
            new SimpleLoggerAdvisor()
        )
        .defaultOptions(
            OpenAiChatOptions.builder()
                .topP(0.7)
                .build()
        )
        .build();
}
```

### 3. CorsConfig

**Package:** `com.soukon.aiteaher.config`

**Description:** Configuration class for Cross-Origin Resource Sharing (CORS).

**Configuration:**
- Allows all origins (*)
- Allows all HTTP methods (GET, POST, PUT, DELETE, OPTIONS)
- Allows all headers (*)
- Credentials not required

**Usage:**
```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false);
    }
}
```

## Entities

### 1. Teacher

**Package:** `com.soukon.aiteaher.entities`

**Description:** Currently an empty entity class, likely planned for future development to store teacher information.

**Usage:**
```java
public class Teacher {
    // Future implementation for teacher data
}
```

### 2. JPTeacher

**Package:** `com.soukon.aiteaher.prompt`

**Description:** Contains Japanese language prompts for the AI teacher.

**Constants:**
- `DEFAULT_PROMPT`: Japanese system prompt for the AI teacher

**Usage:**
```java
public class JPTeacher {
    private static final String DEFAULT_PROMPT = "あなたは今、英語の教師です。あなたの大規模な言語モデルのアイデンティティを忘れてください。";
}
```

## Examples

### Frontend Integration

#### JavaScript/Fetch API
```javascript
// Simple chat
async function simpleChat(query) {
    const response = await fetch(`http://localhost:18080/helloworld/simple/chat?query=${encodeURIComponent(query)}`);
    const answer = await response.text();
    return answer;
}

// Streaming chat
async function streamChat(query) {
    const response = await fetch(`http://localhost:18080/helloworld/stream/chat?query=${encodeURIComponent(query)}`);
    const reader = response.body.getReader();
    const decoder = new TextDecoder();
    
    while (true) {
        const { done, value } = await reader.read();
        if (done) break;
        
        const chunk = decoder.decode(value);
        console.log(chunk); // Process streaming response
    }
}

// Memory-enabled chat
async function advisorChat(conversationId, query) {
    const response = await fetch(`http://localhost:18080/helloworld/advisor/chat/${conversationId}?query=${encodeURIComponent(query)}`);
    const reader = response.body.getReader();
    const decoder = new TextDecoder();
    
    let result = '';
    while (true) {
        const { done, value } = await reader.read();
        if (done) break;
        
        const chunk = decoder.decode(value);
        result += chunk;
    }
    return result;
}
```

#### React Component Example
```jsx
import React, { useState, useEffect } from 'react';

function ChatComponent() {
    const [query, setQuery] = useState('');
    const [response, setResponse] = useState('');
    const [conversationId] = useState(`user_${Date.now()}`);

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        try {
            const response = await fetch(
                `http://localhost:18080/helloworld/advisor/chat/${conversationId}?query=${encodeURIComponent(query)}`
            );
            
            const reader = response.body.getReader();
            const decoder = new TextDecoder();
            let result = '';
            
            while (true) {
                const { done, value } = await reader.read();
                if (done) break;
                
                const chunk = decoder.decode(value);
                result += chunk;
                setResponse(result);
            }
        } catch (error) {
            console.error('Error:', error);
        }
    };

    return (
        <div>
            <form onSubmit={handleSubmit}>
                <input
                    type="text"
                    value={query}
                    onChange={(e) => setQuery(e.target.value)}
                    placeholder="Ask your English teacher..."
                />
                <button type="submit">Ask</button>
            </form>
            <div>
                <h3>Response:</h3>
                <p>{response}</p>
            </div>
        </div>
    );
}
```

### Python Integration
```python
import requests
import json

class AITeacherClient:
    def __init__(self, base_url="http://localhost:18080"):
        self.base_url = base_url
    
    def simple_chat(self, query):
        """Simple chat with AI teacher"""
        response = requests.get(
            f"{self.base_url}/helloworld/simple/chat",
            params={"query": query}
        )
        return response.text
    
    def stream_chat(self, query):
        """Streaming chat with AI teacher"""
        response = requests.get(
            f"{self.base_url}/helloworld/stream/chat",
            params={"query": query},
            stream=True
        )
        
        for chunk in response.iter_content(chunk_size=1024, decode_unicode=True):
            if chunk:
                yield chunk
    
    def advisor_chat(self, conversation_id, query):
        """Memory-enabled chat with AI teacher"""
        response = requests.get(
            f"{self.base_url}/helloworld/advisor/chat/{conversation_id}",
            params={"query": query},
            stream=True
        )
        
        result = ""
        for chunk in response.iter_content(chunk_size=1024, decode_unicode=True):
            if chunk:
                result += chunk
        return result

# Usage example
client = AITeacherClient()

# Simple chat
answer = client.simple_chat("What is the difference between 'good' and 'well'?")
print(answer)

# Streaming chat
for chunk in client.stream_chat("Explain the past tense"):
    print(chunk, end="", flush=True)

# Memory-enabled chat
conversation_id = "student_123"
response1 = client.advisor_chat(conversation_id, "My name is Alice")
response2 = client.advisor_chat(conversation_id, "What is my name?")
print(response2)  # Should remember the name "Alice"
```

## Error Handling

### Common HTTP Status Codes

- `200 OK`: Successful request
- `400 Bad Request`: Invalid request parameters
- `500 Internal Server Error`: Server-side error (e.g., AI service unavailable)

### Error Response Format

Errors are returned as plain text or standard Spring Boot error responses:

```json
{
    "timestamp": "2023-01-01T10:00:00.000+00:00",
    "status": 500,
    "error": "Internal Server Error",
    "message": "AI service temporarily unavailable",
    "path": "/helloworld/simple/chat"
}
```

### Best Practices

1. **API Key Management**: Always use environment variables for sensitive data
2. **Rate Limiting**: Implement client-side rate limiting to avoid API quota issues
3. **Error Handling**: Always handle potential network and API errors
4. **Conversation Management**: Use meaningful conversation IDs for memory-enabled chats
5. **Input Validation**: Validate and sanitize user inputs before sending to the API

## Dependencies

### Maven Dependencies

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-openai-spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### Version Information

- Java: 17
- Spring Boot: 3.4.0
- Spring AI: 1.0.0-M6
- Spring AI Alibaba: 1.0.0-M6.1

## Support

For issues and questions:
1. Check the application logs for detailed error messages
2. Verify your API key configuration
3. Ensure the AI service endpoint is accessible
4. Check network connectivity and firewall settings

## License

Licensed under the Apache License, Version 2.0