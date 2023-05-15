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
        //trim the query to remove leading and trailing whitespaces
        query = query.trim();
        //replace multiple whitespaces with a single space
        query = query.replaceAll("\\s+", " ");
        //convert the query to lowercase
        query = query.toLowerCase();
        //return the normalized query
        return query;
    }

    public SearchQuery saveSearchQuery(String query) {
        String normalizedQuery = normalizeQuery(query);
        Optional<SearchQuery> existingSearchQuery = searchQueryRepository.findSearchQueryByQuery(normalizedQuery);
        if(existingSearchQuery.isPresent()){
            existingSearchQuery.get().setPopularity(existingSearchQuery.get().getPopularity() + 1);
            return searchQueryRepository.save(existingSearchQuery.get());
        }
        SearchQuery searchQuery = SearchQuery.builder().query(normalizedQuery).popularity(1L).build();
        return searchQueryRepository.save(searchQuery);
    }

    public List<SearchQuery> findPopularQueriesByText(String text) {
        //create a PageRequest with page number 0, page size 10 and sort by popularity descending
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "popularity"));
        //use the pageRequest as the pageable argument
        return searchQueryRepository.findByQueryTextRegex(text, pageRequest).getContent();
    }
}
