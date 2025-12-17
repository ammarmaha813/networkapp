package net.thevpc.samples.springnuts.moduleai.service.api;

import net.thevpc.samples.springnuts.moduleai.model.ThreatResult;

/**
 * Interface publique pour l'ingestion de logs
 */
public interface ILogIngestionService {
    ThreatResult ingest(String rawLog);
}