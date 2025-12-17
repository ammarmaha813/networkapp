package net.thevpc.samples.springnuts.moduleai.service.impl.service;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;
import net.thevpc.samples.springnuts.moduleai.service.api.IRAGService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RAGService implements IRAGService {

    private final ChatModel chatModel;
    private final VectorStoreService vectorStoreService;

    public RAGService(ChatModel chatModel, VectorStoreService vectorStoreService) {
        this.chatModel = chatModel;
        this.vectorStoreService = vectorStoreService;
    }

    public String ask(String question) {
        List<Document> relevantDocs = vectorStoreService.searchSimilarLogs(question, 5, 0.0);
        String context = relevantDocs.stream()
                .map(doc -> doc.getText())
                .collect(Collectors.joining("\n---\n"));

        String promptText = String.format("""
            You are a network security assistant. Use the following logs to answer the question.
            
            CONTEXT:
            %s
            
            QUESTION: %s
            
            Provide a concise, technical answer based on the logs above.
            """, context, question);

        ChatResponse response = chatModel.call(new Prompt(promptText));
        return response.getResult().getOutput().getText();
    }
}