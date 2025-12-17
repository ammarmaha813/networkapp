package net.thevpc.samples.springnuts.moduleai.service.impl.utils;

import net.thevpc.samples.springnuts.moduleai.model.NetworkLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class LogParser {

    private final ObjectMapper objectMapper;

    public LogParser() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    // Pattern pour parser des logs de type syslog
    private static final Pattern SYSLOG_PATTERN = Pattern.compile(
            "(?<timestamp>\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}).*" +
                    "src=(?<srcIp>[\\d.]+):(?<srcPort>\\d+).*" +
                    "dst=(?<dstIp>[\\d.]+):(?<dstPort>\\d+).*" +
                    "proto=(?<proto>\\w+).*" +
                    "action=(?<action>\\w+)"
    );

    /**
     * Parse un log JSON
     */
    public NetworkLog parseJson(String jsonLog) {
        try {
            return objectMapper.readValue(jsonLog, NetworkLog.class);
        } catch (Exception e) {
            log.error("Erreur lors du parsing JSON", e);
            throw new RuntimeException("Impossible de parser le log JSON", e);
        }
    }

    /**
     * Parse un log texte (format syslog)
     */
    public NetworkLog parseTextLog(String textLog) {
        try {
            Matcher matcher = SYSLOG_PATTERN.matcher(textLog);

            if (matcher.find()) {
                return NetworkLog.builder()
                        .id(UUID.randomUUID().toString())
                        .timestamp(LocalDateTime.parse(matcher.group("timestamp")))
                        .sourceIp(matcher.group("srcIp"))
                        .sourcePort(Integer.parseInt(matcher.group("srcPort")))
                        .destinationIp(matcher.group("dstIp"))
                        .destinationPort(Integer.parseInt(matcher.group("dstPort")))
                        .protocol(matcher.group("proto"))
                        .action(matcher.group("action"))
                        .rawLog(textLog)
                        .severity("INFO")
                        .bytesSent(0L)
                        .bytesReceived(0L)
                        .build();
            } else {
                throw new IllegalArgumentException("Format de log non reconnu");
            }

        } catch (Exception e) {
            log.error("Erreur lors du parsing du log texte", e);
            throw new RuntimeException("Impossible de parser le log texte", e);
        }
    }

    /**
     * Parse automatiquement (JSON ou texte)
     */
    public NetworkLog parse(String rawLog) {
        rawLog = rawLog.trim();

        if (rawLog.startsWith("{")) {
            return parseJson(rawLog);
        } else {
            return parseTextLog(rawLog);
        }
    }
}