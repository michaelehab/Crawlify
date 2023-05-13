package com.example.crawlify.model;
import java.util.*;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "word")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class Word {
    @Id
    private String id;
    @Indexed(unique = true)
    private String word;
    private HashMap<String, ArrayList<Double>> TF_IDFandOccurrences;

}