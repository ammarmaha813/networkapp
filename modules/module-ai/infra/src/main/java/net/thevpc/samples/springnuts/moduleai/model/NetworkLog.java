package net.thevpc.samples.springnuts.moduleai.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NetworkLog {

    private String id;

    @JsonProperty("timestamp")
    private LocalDateTime timestamp;

    @JsonProperty("source_ip")
    private String sourceIp;

    @JsonProperty("destination_ip")
    private String destinationIp;

    @JsonProperty("source_port")
    private Integer sourcePort;

    @JsonProperty("destination_port")
    private Integer destinationPort;

    @JsonProperty("protocol")
    private String protocol;

    @JsonProperty("bytes_sent")
    private Long bytesSent;

    @JsonProperty("bytes_received")
    private Long bytesReceived;

    @JsonProperty("action")
    private String action; // ALLOW, DENY, DROP

    @JsonProperty("severity")
    private String severity; // INFO, WARNING, CRITICAL

    @JsonProperty("message")
    private String message;

    @JsonProperty("raw_log")
    private String rawLog;

    @JsonProperty("metadata")
    private Map<String, Object> metadata;

    /**
     * Méthode utilitaire pour convertir en texte pour embedding
     */
    public String toEmbeddingText() {
        return String.format(
                "Network Log: Timestamp=%s, SourceIP=%s, DestIP=%s, Protocol=%s, " +
                        "SourcePort=%d, DestPort=%d, Action=%s, Severity=%s, BytesSent=%d, BytesReceived=%d, Message=%s",
                timestamp != null ? timestamp : "N/A",
                sourceIp != null ? sourceIp : "N/A",
                destinationIp != null ? destinationIp : "N/A",
                protocol != null ? protocol : "N/A",
                sourcePort != null ? sourcePort : 0,
                destinationPort != null ? destinationPort : 0,
                action != null ? action : "N/A",
                severity != null ? severity : "N/A",
                bytesSent != null ? bytesSent : 0L,
                bytesReceived != null ? bytesReceived : 0L,
                message != null ? message : "N/A"
        );
    }
}