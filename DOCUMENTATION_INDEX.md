# AI-Teacher Documentation Index

## Overview

This directory contains comprehensive documentation for the AI-Teacher application, a Spring Boot-based AI-powered educational chat service. The documentation is organized into multiple files to serve different audiences and use cases.

## Documentation Files

### 📚 [API_DOCUMENTATION.md](./API_DOCUMENTATION.md)
**Primary audience:** API consumers, frontend developers, integration partners

**Contains:**
- Complete API reference with examples
- Quick start guide
- Configuration instructions
- Frontend integration examples (JavaScript, React, Python)
- Error handling and best practices
- Usage examples with curl commands

### 🔧 [TECHNICAL_DOCUMENTATION.md](./TECHNICAL_DOCUMENTATION.md)
**Primary audience:** Backend developers, system architects, DevOps engineers

**Contains:**
- Detailed architecture overview
- Component implementation details
- Data flow diagrams
- Configuration parameters
- Performance considerations
- Security recommendations
- Deployment strategies
- Testing approaches

### 📋 [OPENAPI_SPECIFICATION.yaml](./OPENAPI_SPECIFICATION.yaml)
**Primary audience:** API documentation tools, code generators, automated testing

**Contains:**
- OpenAPI 3.0 specification
- Machine-readable API definitions
- Schema definitions
- Response examples
- Parameter validations
- Error response formats

## Application Overview

### What is AI-Teacher?

AI-Teacher is a Spring Boot application that provides AI-powered educational chat services through OpenAI integration. It acts as an English teacher AI assistant that can:

- Engage in conversations with students
- Remember context across sessions
- Provide both simple and streaming chat responses
- Support multiple conversation formats

### Key Features

- **Simple Chat**: Synchronous chat interface
- **Streaming Chat**: Real-time streaming responses
- **Memory-Enabled Chat**: Conversation history and context
- **CORS Support**: Cross-origin resource sharing for web applications
- **OpenAI Integration**: Powered by OpenAI's language models

### Architecture

```
Frontend Applications
        ↓
    REST API Layer
        ↓
    Spring Boot Application
        ↓
    Spring AI Framework
        ↓
    OpenAI API
```

## Quick Navigation

### For API Users
- **Getting Started**: [API_DOCUMENTATION.md#quick-start](./API_DOCUMENTATION.md#quick-start)
- **API Reference**: [API_DOCUMENTATION.md#public-apis](./API_DOCUMENTATION.md#public-apis)
- **Code Examples**: [API_DOCUMENTATION.md#examples](./API_DOCUMENTATION.md#examples)

### For Developers
- **Architecture**: [TECHNICAL_DOCUMENTATION.md#architecture-overview](./TECHNICAL_DOCUMENTATION.md#architecture-overview)
- **Component Details**: [TECHNICAL_DOCUMENTATION.md#component-details](./TECHNICAL_DOCUMENTATION.md#component-details)
- **Development Setup**: [TECHNICAL_DOCUMENTATION.md#development-setup](./TECHNICAL_DOCUMENTATION.md#development-setup)

### For Tools & Automation
- **OpenAPI Spec**: [OPENAPI_SPECIFICATION.yaml](./OPENAPI_SPECIFICATION.yaml)
- **Schema Definitions**: View in any OpenAPI-compatible tool

## API Endpoints Summary

| Endpoint | Method | Description | Response Type |
|----------|---------|-------------|---------------|
| `/helloworld/simple/chat` | GET | Simple synchronous chat | Plain text |
| `/helloworld/stream/chat` | GET | Streaming chat responses | Server-Sent Events |
| `/helloworld/advisor/chat/{id}` | GET | Memory-enabled chat | Server-Sent Events |
| `/actuator/health` | GET | Health check | JSON |

## Technology Stack

- **Framework**: Spring Boot 3.4.0
- **AI Integration**: Spring AI 1.0.0-M6
- **Language**: Java 17
- **Build Tool**: Maven
- **AI Provider**: OpenAI (configurable endpoint)
- **Model**: doubao-1-5-lite-32k-250115 (configurable)

## Getting Started

### Prerequisites
- Java 17 or later
- Maven 3.6+
- OpenAI API key or compatible service

### Quick Start
```bash
# Set environment variable
export AI_OPENAI_API_KEY=your_api_key_here

# Run the application
mvn spring-boot:run

# Test the API
curl "http://localhost:18080/helloworld/simple/chat?query=Hello"
```

## Configuration

### Environment Variables
- `AI_OPENAI_API_KEY`: Required OpenAI API key

### Application Properties
- Port: 18080 (configurable)
- Model: doubao-1-5-lite-32k-250115 (configurable)
- Base URL: https://ark.cn-beijing.volces.com/api/ (configurable)

## Security Considerations

⚠️ **Important Security Notes:**
- API keys should never be committed to version control
- CORS is currently set to allow all origins (suitable for development only)
- No authentication is currently implemented
- Consider implementing rate limiting for production use

## Contributing

### Documentation Updates
When updating the codebase, please ensure to:
1. Update relevant documentation files
2. Keep API examples current
3. Update OpenAPI specification
4. Test all code examples

### Adding New Features
1. Update `API_DOCUMENTATION.md` with new endpoints
2. Update `TECHNICAL_DOCUMENTATION.md` with implementation details
3. Update `OPENAPI_SPECIFICATION.yaml` with new schema definitions
4. Add usage examples and test cases

## Support & Resources

### Documentation Links
- [API Documentation](./API_DOCUMENTATION.md) - Complete API reference
- [Technical Documentation](./TECHNICAL_DOCUMENTATION.md) - Implementation details
- [OpenAPI Specification](./OPENAPI_SPECIFICATION.yaml) - Machine-readable API spec

### External Resources
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring AI Documentation](https://spring.io/projects/spring-ai)
- [OpenAI API Documentation](https://platform.openai.com/docs/api-reference)

### Common Issues
1. **API Key Not Set**: Ensure `AI_OPENAI_API_KEY` environment variable is set
2. **Connection Issues**: Verify network connectivity to OpenAI endpoint
3. **Memory Issues**: Monitor JVM heap usage for long-running conversations
4. **Rate Limiting**: Respect OpenAI API rate limits

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](./LICENSE) file for details.

## Version History

- **v1.0.0**: Initial release with basic chat functionality
  - Simple chat endpoint
  - Streaming chat support
  - Memory-enabled conversations
  - OpenAI integration
  - CORS configuration

---

*Last updated: 2024-01-01*  
*Documentation maintained by: AI-Teacher Development Team*