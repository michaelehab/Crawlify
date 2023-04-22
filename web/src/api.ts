import { Result } from "./types";

export interface SearchRequest {
  query: String;
}

export interface SearchResponse {
  results: Result[];
}
