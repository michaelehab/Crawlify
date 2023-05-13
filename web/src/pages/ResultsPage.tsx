import React, { useState, useEffect } from "react";
import { useQuery } from "@tanstack/react-query";
import { useParams } from "react-router-dom";
import {
  Container,
  Heading,
  Text,
  Spinner,
  Flex,
  IconButton,
} from "@chakra-ui/react";
import { ChevronLeftIcon, ChevronRightIcon } from "@chakra-ui/icons";
import { useTitle } from "../utils/useTitle";
import { callEndpoint } from "../utils/callEndpoint";
import { SearchRequest, SearchResponse } from "../api";
import { ResultCard } from "../components/resultCard";
import Navigation from "../components/navigation";

function Results() {
  const { query } = useParams();
  useTitle(`${query}`);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [searchTime, setSearchTime] = useState("");

  const { data, isLoading, refetch } = useQuery<SearchResponse, string>(
    [`list${query}Results`, page],
    () =>
      callEndpoint<SearchRequest, SearchResponse>(
        `/search?query=${query}&page=${page}&ie=UTF-8`,
        "GET"
      )
  );

  useEffect(() => {
    if (data) {
      setTotalPages(data.totalPages);
      setPage(data.currentPage);
      setSearchTime(data.searchTime);
    }
  }, [data]);

  const handlePrev = () => {
    if (page > 1) {
      setPage(page - 1);
      refetch();
    }
  };

  const handleNext = () => {
    if (page < totalPages) {
      setPage(page + 1);
      refetch();
    }
  };

  return (
    <>
      <Navigation />
      <Container maxW="800px" p="5">
        <Heading as="h1" mb="2" size="lg" color="blue.600">
          Search results for "{query}"
        </Heading>
        {isLoading && <Spinner size="xl" color="blue.300" />}
        {data && (
          <Heading size="sm" color="blue.700">
            Search Took {searchTime} milliseconds
          </Heading>
        )}
        <Flex direction="column" align="center">
          {data &&
            data.results.length > 0 &&
            data.results.map((result, i) => <ResultCard key={i} {...result} />)}
          <Flex mt="2">
            <IconButton
              onClick={handlePrev}
              disabled={page === 1}
              icon={<ChevronLeftIcon />}
              aria-label="Previous page"
            />
            <Text mx="2">
              Page {page} of {totalPages}
            </Text>
            <IconButton
              onClick={handleNext}
              disabled={page === totalPages}
              icon={<ChevronRightIcon />}
              aria-label="Next page"
            />
          </Flex>
        </Flex>
      </Container>
    </>
  );
}

export default Results;
