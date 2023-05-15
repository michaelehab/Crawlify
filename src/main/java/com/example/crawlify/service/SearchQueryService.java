package com.example.crawlify.service;

import com.example.crawlify.model.SearchQuery;
import com.example.crawlify.repository.SearchQueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SearchQueryService {
    private final SearchQueryRepository searchQueryRepository;

    @Autowired
    public SearchQueryService(SearchQueryRepository searchQueryRepository) {
        this.searchQueryRepository = searchQueryRepository;
    }

    public String normalizeQuery(String query) {
        query = query.trim();
        query = query.replaceAll("\\s+", " ");
        query = query.toLowerCase();
        return query;
    }

    public void saveSearchQuery(String query) {
        String normalizedQuery = normalizeQuery(query);
        Optional<SearchQuery> existingSearchQuery = searchQueryRepository.findSearchQueryByQuery(normalizedQuery);
        if(existingSearchQuery.isPresent()){
            existingSearchQuery.get().setPopularity(existingSearchQuery.get().getPopularity() + 1);
            searchQueryRepository.save(existingSearchQuery.get());
            return;
        }
        SearchQuery searchQuery = SearchQuery.builder().query(normalizedQuery).popularity(1L).build();
        searchQueryRepository.save(searchQuery);
    }

    public List<SearchQuery> findPopularQueriesByText(String text) {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "popularity"));
        String regexString = "^" + text;
        return searchQueryRepository.findByQueryTextRegex(regexString, pageRequest).getContent();
    }
}
