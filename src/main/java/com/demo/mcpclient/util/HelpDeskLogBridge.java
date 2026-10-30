package com.demo.mcpclient.util;

import io.modelcontextprotocol.spec.McpSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mcp.annotation.McpLogging;
import org.springframework.stereotype.Component;

@Component
public class HelpDeskLogBridge {

    Logger log = LoggerFactory.getLogger(HelpDeskLogBridge.class);

    @McpLogging(clients = "remotedemo")
    public void onServerLog(McpSchema.LoggingLevel level,
                            String source,
                            String message) {
        log.info("Received log from server - Level: {}, Source: {}, Message: {}", level, source, message);
    }
}
