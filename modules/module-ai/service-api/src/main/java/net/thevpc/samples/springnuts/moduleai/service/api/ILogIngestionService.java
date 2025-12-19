
package net.thevpc.samples.springnuts.moduleai.service.api;

import net.thevpc.samples.springnuts.moduleai.model.ThreatResult;
public interface ILogIngestionService {

    /**
     * Ingère et analyse un log réseau brut
     *
     * @param rawLog Le log au format texte ou JSON
     * @return Le résultat de l'analyse de menace
     */
    ThreatResult ingest(String rawLog);
}