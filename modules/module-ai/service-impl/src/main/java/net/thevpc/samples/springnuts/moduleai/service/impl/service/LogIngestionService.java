package net.thevpc.samples.springnuts.moduleai.service.impl.service;
import net.thevpc.samples.springnuts.moduleai.model.NetworkLog;
import net.thevpc.samples.springnuts.moduleai.model.ThreatResult;
import net.thevpc.samples.springnuts.moduleai.service.impl.utils.LogParser;
import net.thevpc.samples.springnuts.moduleai.service.api.ILogIngestionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogIngestionService implements ILogIngestionService {

    private final LogParser logParser;
    private final VectorStoreService vectorStoreService;
    private final ThreatDetectionService threatDetectionService;
    @Override
    public ThreatResult ingest(String rawLog) {
        NetworkLog networkLog = logParser.parse(rawLog);   // <-- variable renommée
        vectorStoreService.storeLog(networkLog);
        ThreatResult result = threatDetectionService.analyze(networkLog);

        log.info("Log {} classifié comme {}", networkLog.getId(), result.getClassification());
        return result;
    }
}