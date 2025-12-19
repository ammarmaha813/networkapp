package net.thevpc.samples.springnuts.moduleai.service.api;

/**
 * Service RAG (Retrieval Augmented Generation)
 * Permet de poser des questions en langage naturel sur les logs
 */
public interface IRAGService {

    /**
     * Pose une question et récupère une réponse basée sur les logs stockés
     *
     * @param question Question en langage naturel
     * @return Réponse générée par le LLM avec contexte des logs
     */
    String ask(String question);
}