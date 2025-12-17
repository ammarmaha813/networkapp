package net.thevpc.samples.springnuts.moduleai.service.impl.service;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmbeddingService {

    private final EmbeddingModel embeddingModel;

    public EmbeddingService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public List<Double> generateEmbedding(String text) {
        EmbeddingResponse response = embeddingModel.embedForResponse(List.of(text));
        float[] embedding = response.getResults().get(0).getOutput();

        // Convertir float[] en List<Double> manuellement
        List<Double> result = new ArrayList<>(embedding.length);
        for (float value : embedding) {
            result.add((double) value);
        }
        return result;
    }
}