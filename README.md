# mcpclient

Spring AI MCP client built while following the Udemy course [Java Spring AI](https://www.udemy.com/course/java-spring-ai/). Reference repository: [souissimedmehdi/spring-ia](https://github.com/souissimedmehdi/spring-ia).

A Spring Boot service that exposes a chat endpoint. The model (Claude on Amazon Bedrock Converse) can call tools provided by MCP servers, here the official filesystem server started over stdio.

## Stack
- Java 25, Spring Boot 4.x, Maven (wrapper)
- Spring AI 2.x: `ChatClient`, MCP client, Bedrock Converse, Titan embeddings
- Node.js / npm (the MCP filesystem server runs through `npx`)

## Prerequisites
- JDK 25 (`JAVA_HOME` set)
- Node.js with `npx` on the `PATH`
- AWS credentials with access to Bedrock in `eu-west-2`

## Configuration
Create `src/main/resources/secrets.properties` (gitignored, imported optionally):

```properties
spring.ai.bedrock.aws.access-key=...
spring.ai.bedrock.aws.secret-key=...
BEDROCK_MODEL_ID=arn:aws:bedrock:<region>:<account-id>:inference-profile/<profile-id>
```

`BEDROCK_MODEL_ID` is kept here because the ARN contains your AWS account ID.

Other settings live in `src/main/resources/application.properties`:

| Property | Purpose |
|---|---|
| `spring.ai.bedrock.aws.region` | AWS region |
| `spring.ai.bedrock.converse.chat.model` | Bedrock model / inference profile |
| `spring.ai.mcp.client.stdio.servers-configuration` | MCP servers file (`mcp-servers.local.json`) |

Copy `mcp-servers.stdio.json` to `mcp-servers.local.json` (gitignored, so your machine-specific paths stay private) and replace the `<...>` placeholders. The GitHub server reads its token from the `GITHUB_PERSONAL_ACCESS_TOKEN` environment variable.

The file declares the stdio MCP servers (filesystem, GitHub and the local Spring AI MCP server jar). Set the Java and jar paths too. On Windows the command must be `npx.cmd`, because Java cannot launch `.cmd` scripts as `npx`. Use `npx` on Linux/macOS. Set the directory argument to the folder the filesystem server may access.

The `remotedemo` MCP server is reached over streamable HTTP at `http://localhost:8090/mcp` (configured in `application.properties`). Start that server first, or remove the connection.

## MCP sampling
`HelpDeskSamplingProvider` handles sampling requests (`@McpSampling(clients = "remotedemo")`). When the remote server's `summarizeTickets` tool asks for an LLM completion, the client runs it on its own model and returns the text, so the server needs no API key. It uses the low-level `ChatModel` rather than the `ChatClient`, which avoids a loop of tool call -> sampling -> tool call.

## Run
```bash
./mvnw spring-boot:run
```

## Usage
```bash
curl -H "username: mehdi" "http://localhost:8080/api/chat?message=List the files in the allowed directory"
```

```bash
curl -H "username: mehdi" "http://localhost:8080/api/summarize-tickets"
```
Calls the `summarizeTickets` tool of the remote server (sampling) and returns its summary as-is.

## Tests
```bash
./mvnw test
```

## Roadmap
- Versioned `POST /api/v1/chat` with validated request/response records
- `ProblemDetail` error handling
- Controller tests with a mocked `ChatClient`
- RAG with a vector store (Titan embeddings already configured)
- Authentication and a restricted MCP tool allowlist
