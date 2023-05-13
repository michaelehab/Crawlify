import { SearchResult } from "./types";

export interface SearchRequest {
  query: string;
}

export interface SearchResponse {
  results: SearchResult[];
  searchTime: string;
  totalPages: number;
  currentPage: number;
}
