package com.bankofapi.hackathon.embedded_chat_service.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.simple.JdbcClient;

@Configuration
public class DocLoader {
    private final JdbcClient jdbcClient;
    private final VectorStore vectorStore;
    private static final Logger log = LoggerFactory.getLogger(DocLoader.class);
    @Value("classpath:/docs/natwest-reference.pdf")
    private Resource resource;
    public DocLoader(JdbcClient jdbcClient, VectorStore vectorStore) {
        this.jdbcClient = jdbcClient;
        this.vectorStore = vectorStore;
    }

    @PostConstruct
    public void init() {
        Integer count = jdbcClient.sql("select count(*) from vector_store")
                .query(Integer.class)
                .single();
        log.info("Current count of the Vector Store: {}", count);

        if (count == 0) {
            log.info("Loading Natwest Reference PDF into Vector Store");
            var pdfReader = new ParagraphPdfDocumentReader(resource);
            TextSplitter splitter = new TokenTextSplitter();
            vectorStore.accept(splitter.apply(pdfReader.get()));
            log.info("Vector store loaded with data");
        }
    }
}
