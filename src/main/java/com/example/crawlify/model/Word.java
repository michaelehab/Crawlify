package com.example.crawlify.model;
import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.example.crawlify.utils.wordData;


@Document(collection = "word")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Word {
    @Id
    private String id;
    private wordData wordData;

}