# AI-Teacher Technical Documentation

## Architecture Overview

The AI-Teacher application follows a standard Spring Boot architecture with the following layers:

```
┌─────────────────────────────────────────────────────────────────┐
│                        Presentation Layer                       │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │               HelloworldController                          │ │
│  │  - /helloworld/simple/chat                                 │ │
│  │  - /helloworld/stream/chat                                 │ │
│  │  - /helloworld/advisor/chat/{id}                           │ │
│  └─────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                                  │
                                  ▼
┌─────────────────────────────────────────────────────────────────┐
│                         Service Layer                           │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │                   ChatClient                                │ │
│  │  - MessageChatMemoryAdvisor                                │ │
│  │  - SimpleLoggerAdvisor                                     │ │
│  │  - OpenAiChatOptions                                       │ │
│  └─────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
                                  │
                                  ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Integration Layer                          │
│  ┌─────────────────────────────────────────────────────────────┐ │
│  │                Spring AI OpenAI                             │ │
│  │  - OpenAI API Client                                       │ │
│  │  - Chat Model Integration                                  │ │
│  │  - Streaming Support                                       │ │
│  └─────────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

## Component Details

### 1. HelloworldController

**File:** `src/main/java/com/soukon/aiteaher/controller/HelloworldController.java`

#### Key Features:
- **Dependency Injection**: Uses constructor injection for `ChatModel`
- **ChatClient Configuration**: Builds a configured `ChatClient` with advisors
- **Memory Management**: Implements conversation memory using `InMemoryChatMemory`
- **Streaming Support**: Provides reactive streaming responses using `Flux<String>`

#### Method Signatures:

```java
public class HelloworldController {
    
    // Constructor with ChatModel injection
    public HelloworldController(ChatModel openAiChatModel)
    
    // Simple synchronous chat
    @GetMapping("/simple/chat")
    public String simpleChat(@RequestParam(value = "query", defaultValue = "你好，很高兴认识你，能简单介绍一下自己吗？") String query)
    
    // Streaming chat response
    @GetMapping("/stream/chat")
    public Flux<String> streamChat(@RequestParam(value = "query", defaultValue = "你好，很高兴认识你，能简单介绍一下自己吗？") String query, HttpServletResponse response)
    
    // Memory-enabled chat with conversation tracking
    @GetMapping("/advisor/chat/{id}")
    public Flux<String> advisorChat(HttpServletResponse response, @PathVariable String id, @RequestParam String query)
}
```

#### ChatClient Configuration:

```java
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
```

#### Memory Management Details:
- **In-Memory Storage**: Uses `InMemoryChatMemory` for conversation persistence
- **Conversation ID**: Required for memory-enabled endpoints
- **Retrieve Size**: Configurable parameter for how many previous messages to include in context
- **Memory Parameters**:
  - `CHAT_MEMORY_CONVERSATION_ID_KEY`: Session identifier
  - `CHAT_MEMORY_RETRIEVE_SIZE_KEY`: Number of messages to retrieve (default: 100)

### 2. CorsConfig

**File:** `src/main/java/com/soukon/aiteaher/config/CorsConfig.java`

#### Implementation Details:
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

#### Security Considerations:
- **Open CORS Policy**: Currently allows all origins (`*`)
- **No Credentials**: `allowCredentials(false)` for security
- **Production Recommendation**: Restrict `allowedOrigins` to specific domains

### 3. AiTeaherApplication

**File:** `src/main/java/com/soukon/aiteaher/AiTeaherApplication.java`

#### Bootstrap Configuration:
```java
@SpringBootApplication
public class AiTeaherApplication {
    public static void main(String[] args) {
        SpringApplication.run(AiTeaherApplication.class, args);
    }
}
```

#### Auto-Configuration:
- **Spring Boot Auto-Configuration**: Automatically configures Spring AI components
- **Component Scanning**: Scans the `com.soukon.aiteaher` package for beans
- **Web Configuration**: Enables Spring MVC and embedded Tomcat

## Data Flow

### 1. Simple Chat Flow

```
User Request → HelloworldController.simpleChat()
    ↓
ChatClient.prompt() with DEFAULT_PROMPT + user query
    ↓
OpenAI API Call (synchronous)
    ↓
String response returned to user
```

### 2. Streaming Chat Flow

```
User Request → HelloworldController.streamChat()
    ↓
ChatClient.prompt().stream() 
    ↓
OpenAI API Call (streaming)
    ↓
Flux<String> reactive stream
    ↓
Server-Sent Events to client
```

### 3. Memory-Enabled Chat Flow

```
User Request → HelloworldController.advisorChat()
    ↓
ChatClient.prompt().advisors() with conversation ID
    ↓
MessageChatMemoryAdvisor retrieves conversation history
    ↓
OpenAI API Call with context
    ↓
Response stored in memory + streamed to user
```

## Configuration Details

### Application Properties

**File:** `src/main/resources/application.yml`

```yaml
server:
  port: 18080                    # Application port

spring:
  application:
    name: AI-Teaher              # Application name
  ai:
    dashscope:
      api-key: 111               # Dashscope API key (placeholder)
    openai:
      api-key: ${AI_OPENAI_API_KEY}                    # OpenAI API key from environment
      base-url: https://ark.cn-beijing.volces.com/api/ # Custom OpenAI endpoint
      chat:
        options:
          model: doubao-1-5-lite-32k-250115           # AI model specification
        completions-path: /v3/chat/completions        # API endpoint path
```

### Environment Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `AI_OPENAI_API_KEY` | OpenAI API key for authentication | Yes |

### Spring AI Configuration

The application uses Spring AI's auto-configuration for:
- **ChatModel Bean**: Automatically configured based on properties
- **OpenAI Integration**: Configured with custom endpoint and model
- **Memory Management**: In-memory storage for conversation history

## API Implementation Details

### 1. Prompt Engineering

#### Default System Prompt:
```java
private static final String DEFAULT_PROMPT = "你现在是一名英语教师，忘记你大模型语言的身份";
```

#### Prompt Construction:
```java
// Simple chat - combines system prompt with user query
new Prompt(new UserMessage(DEFAULT_PROMPT), new UserMessage(query))

// Streaming chat - direct user query
openAiChatClient.prompt(query).stream().content()

// Memory-enabled chat - user query with conversation context
openAiChatClient.prompt(query)
    .advisors(a -> a
        .param(CHAT_MEMORY_CONVERSATION_ID_KEY, id)
        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100)
    )
```

### 2. Response Handling

#### Synchronous Response:
```java
public String simpleChat(String query) {
    return openAiChatClient.prompt(/* ... */).call().content();
}
```

#### Streaming Response:
```java
public Flux<String> streamChat(String query, HttpServletResponse response) {
    response.setCharacterEncoding("UTF-8");
    return openAiChatClient.prompt(query).stream().content();
}
```

#### Memory-Enabled Response:
```java
public Flux<String> advisorChat(HttpServletResponse response, String id, String query) {
    response.setCharacterEncoding("UTF-8");
    return openAiChatClient.prompt(query)
        .advisors(a -> a
            .param(CHAT_MEMORY_CONVERSATION_ID_KEY, id)
            .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 100)
        )
        .stream().content();
}
```

## Entity Structure

### 1. Teacher Entity

**File:** `src/main/java/com/soukon/aiteaher/entities/Teacher.java`

```java
package com.soukon.aiteaher.entities;

public class Teacher {
    // Currently empty - placeholder for future implementation
    // Potential fields:
    // - Long id
    // - String name
    // - String subject
    // - String language
    // - List<String> specialties
}
```

### 2. JPTeacher Prompt

**File:** `src/main/java/com/soukon/aiteaher/prompt/JPTeacher.java`

```java
package com.soukon.aiteaher.prompt;

public class JPTeacher {
    private static final String DEFAULT_PROMPT = "あなたは今、英語の教師です。あなたの大規模な言語モデルのアイデンティティを忘れてください。";
    
    // Potential methods:
    // public static String getJapanesePrompt()
    // public static String getLocalizedPrompt(String locale)
}
```

## Testing Strategy

### Unit Testing Structure

```java
@SpringBootTest
class HelloworldControllerTest {
    
    @Autowired
    private HelloworldController controller;
    
    @MockBean
    private ChatModel chatModel;
    
    @Test
    void testSimpleChat() {
        // Test simple chat functionality
    }
    
    @Test
    void testStreamChat() {
        // Test streaming chat functionality
    }
    
    @Test
    void testAdvisorChat() {
        // Test memory-enabled chat functionality
    }
}
```

### Integration Testing

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AiTeaherApplicationIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void testHealthCheck() {
        // Test application startup
    }
    
    @Test
    void testCorsConfiguration() {
        // Test CORS headers
    }
}
```

## Performance Considerations

### 1. Memory Management
- **In-Memory Storage**: Limited by JVM heap size
- **Memory Leaks**: Conversation history grows indefinitely
- **Recommendation**: Implement conversation expiration or external storage

### 2. Streaming Performance
- **Backpressure**: Handled by Spring WebFlux
- **Connection Management**: Automatic with Spring Boot
- **Resource Usage**: Monitor concurrent streaming connections

### 3. API Rate Limiting
- **OpenAI Limits**: Respect API rate limits
- **Client-Side**: Implement request throttling
- **Server-Side**: Consider adding rate limiting filter

## Security Considerations

### 1. API Key Security
- **Environment Variables**: Never commit API keys to version control
- **Key Rotation**: Implement regular key rotation
- **Access Control**: Restrict API key access

### 2. Input Validation
- **Query Parameters**: Validate and sanitize user inputs
- **Conversation IDs**: Validate format and length
- **Content Filtering**: Consider implementing content filtering

### 3. CORS Security
- **Production Settings**: Restrict allowed origins
- **Authentication**: Consider implementing authentication
- **Rate Limiting**: Implement request rate limiting

## Deployment Considerations

### 1. Environment Configuration
```yaml
# Production configuration
spring:
  ai:
    openai:
      api-key: ${AI_OPENAI_API_KEY}
      base-url: https://api.openai.com/
      chat:
        options:
          model: gpt-3.5-turbo
```

### 2. Monitoring
- **Health Checks**: Implement custom health indicators
- **Metrics**: Monitor API response times and error rates
- **Logging**: Configure structured logging for production

### 3. Scaling
- **Horizontal Scaling**: Application is stateless except for in-memory chat history
- **Load Balancing**: Use sticky sessions for memory-enabled chats
- **External Memory**: Consider Redis or database for conversation storage

## Future Enhancements

### 1. Database Integration
- **JPA Entities**: Implement proper entity classes
- **Conversation Persistence**: Store chat history in database
- **User Management**: Add user authentication and profiles

### 2. Advanced Features
- **Multi-language Support**: Implement language detection and switching
- **Custom Prompts**: Allow users to define custom teacher personalities
- **Analytics**: Track usage patterns and popular topics

### 3. Security Enhancements
- **Authentication**: Add JWT or OAuth2 support
- **Authorization**: Implement role-based access control
- **Input Validation**: Add comprehensive input validation

## Development Setup

### 1. IDE Configuration
```xml
<!-- Maven configuration for development -->
<properties>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
</properties>
```

### 2. Debug Configuration
```yaml
# Development profile
spring:
  profiles:
    active: dev
  ai:
    openai:
      api-key: ${AI_OPENAI_API_KEY}
      base-url: https://api.openai.com/
      chat:
        options:
          model: gpt-3.5-turbo
          temperature: 0.7
          max-tokens: 150

logging:
  level:
    com.soukon.aiteaher: DEBUG
    org.springframework.ai: DEBUG
```

### 3. Local Testing
```bash
# Set environment variables
export AI_OPENAI_API_KEY=your_api_key_here

# Run with development profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Run tests
mvn test
```