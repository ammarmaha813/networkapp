package net.thevpc.samples.springnuts.moduleai.service.impl.utils;

import net.thevpc.samples.springnuts.moduleai.model.NetworkLog;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
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
     * Parse un log JSON avec support étendu pour différents formats
     */
    public NetworkLog parseJson(String jsonLog) {
        try {
            return objectMapper.readValue(jsonLog, NetworkLog.class);
        } catch (Exception e) {
            log.error("Erreur lors du parsing JSON standard", e);
            // Tentative de parsing avec format ICMP
            return parseIcmpJson(jsonLog);
        }
    }

    /**
     * Parse un log JSON au format ICMP (packetNumber, source, destination, etc.)
     */
    private NetworkLog parseIcmpJson(String jsonLog) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> jsonMap = objectMapper.readValue(jsonLog, Map.class);

            String timestampStr = (String) jsonMap.get("timestamp");
            String source = (String) jsonMap.get("source");
            String destination = (String) jsonMap.get("destination");
            String protocol = (String) jsonMap.get("protocol");
            String info = (String) jsonMap.get("info");
            Object packetNumberObj = jsonMap.get("packetNumber");
            Object lengthObj = jsonMap.get("length");

            // ✅ SOLUTION: Générer un UUID valide pour la DB + stocker packetNumber dans metadata
            String uuid = UUID.randomUUID().toString();

            // Stocker packetNumber dans les métadonnées pour pouvoir le récupérer
            Map<String, Object> metadataMap = new HashMap<>(jsonMap);
            metadataMap.put("icmp_packet_number", packetNumberObj);
            metadataMap.put("original_id", packetNumberObj != null ? "ICMP_" + packetNumberObj.toString() : null);

            // Gérer length de manière sécurisée
            Long bytesSent = 0L;
            if (lengthObj != null) {
                if (lengthObj instanceof Number) {
                    bytesSent = ((Number) lengthObj).longValue();
                } else {
                    try {
                        bytesSent = Long.parseLong(lengthObj.toString());
                    } catch (NumberFormatException e) {
                        log.warn("Impossible de parser length: {}", lengthObj);
                        bytesSent = 0L;
                    }
                }
            }

            return NetworkLog.builder()
                    .id(uuid)  // ✅ UUID valide pour la base de données
                    .timestamp(parseTimestamp(timestampStr))
                    .sourceIp(source)
                    .destinationIp(destination)
                    .sourcePort(0) // ICMP n'a pas de ports
                    .destinationPort(0) // ICMP n'a pas de ports
                    .protocol(protocol)
                    .action(determineAction(info))
                    .severity(determineSeverity(info, protocol))
                    .message(info)
                    .bytesSent(bytesSent)
                    .bytesReceived(0L)
                    .rawLog(jsonLog)
                    .metadata(metadataMap)  // ✅ Inclure toutes les données ICMP dans metadata
                    .build();

        } catch (Exception e) {
            log.error("Erreur lors du parsing JSON ICMP", e);
            throw new RuntimeException("Impossible de parser le log JSON ICMP", e);
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

    /**
     * Parse un timestamp string en LocalDateTime
     */
    private LocalDateTime parseTimestamp(String timestampStr) {
        try {
            if (timestampStr == null) return null;

            // Format: "2025-11-20T00:44:41.792631Z"
            if (timestampStr.endsWith("Z")) {
                timestampStr = timestampStr.substring(0, timestampStr.length() - 1);
            }
            return LocalDateTime.parse(timestampStr);
        } catch (Exception e) {
            log.warn("Impossible de parser le timestamp: {}", timestampStr);
            return LocalDateTime.now();
        }
    }

    /**
     * Détermine l'action basée sur les informations ICMP
     */
    private String determineAction(String info) {
        if (info == null) return "UNKNOWN";

        info = info.toLowerCase();
        if (info.contains("echo (ping) reply")) return "ALLOW";
        if (info.contains("echo (ping) request")) return "ALLOW";
        if (info.contains("destination unreachable")) return "DROP";
        if (info.contains("time exceeded")) return "DROP";
        return "ALLOW";
    }

    /**
     * Détermine la sévérité basée sur le protocole et les informations
     */
    private String determineSeverity(String info, String protocol) {
        if (protocol == null) return "INFO";

        protocol = protocol.toUpperCase();

        // ICMP normal est généralement INFO
        if ("ICMP".equals(protocol)) {
            if (info != null && info.toLowerCase().contains("destination unreachable")) {
                return "WARNING";
            }
            return "INFO";
        }

        return "INFO";
    }
}