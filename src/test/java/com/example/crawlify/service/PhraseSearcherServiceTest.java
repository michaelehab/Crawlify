package com.example.crawlify.service;

import com.example.crawlify.model.Word;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class PhraseSearcherServiceTest {

    @Mock
    private QueryService queryService;

    @InjectMocks
    private PhraseSearcherService phraseSearcherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void startProcessing_validQuery_returnsRelevantWords() {
        // Arrange
        String query = "solar energy";
        List<Word> expectedWords = new ArrayList<>();
        Word word1 = new Word();
        word1.setWord("solar");
        HashMap<String, ArrayList<Double>> word1Occurrences = new HashMap<>();
        word1Occurrences.put("https://en.wikipedia.org/wiki/Solar_energy", new ArrayList<>(List.of(0.5, 10.0, 20.0)));
        word1Occurrences.put("https://www.nationalgeographic.com/environment/article/solar-power", new ArrayList<>(List.of(0.4, 15.0)));
        word1.setTF_IDFandOccurrences(word1Occurrences);
        expectedWords.add(word1);

        Word word2 = new Word();
        word2.setWord("energy");
        HashMap<String, ArrayList<Double>> word2Occurrences = new HashMap<>();
        word2Occurrences.put("https://en.wikipedia.org/wiki/Solar_energy", new ArrayList<>(List.of(0.6, 12.0, 25.0)));
        word2Occurrences.put("https://www.nationalgeographic.com/environment/article/solar-power", new ArrayList<>(List.of(0.3, 18.0)));
        word2.setTF_IDFandOccurrences(word2Occurrences);
        expectedWords.add(word2);

        when(queryService.startProcessing(query)).thenReturn(expectedWords);

        // Act
        List<Word> actualWords = phraseSearcherService.startProcessing(query);

        // Assert
        assertEquals(expectedWords, actualWords);
    }

    @Test
    void startProcessing_invalidQuery_returnsEmptyList() {
        // Arrange
        String query = "";
        List<Word> expectedWords = new ArrayList<>();

        when(queryService.startProcessing(query)).thenReturn(expectedWords);

        // Act
        List<Word> actualWords = phraseSearcherService.startProcessing(query);

        // Assert
        assertEquals(expectedWords, actualWords);
    }
}
