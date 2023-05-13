import React, { useState, useEffect } from "react";
import { useQuery } from "@tanstack/react-query";
import { useParams } from "react-router-dom";
import {
  Box,
  Heading,
  Text,
  Spinner,
  ButtonGroup,
  Button,
  Center,
} from "@chakra-ui/react";
import { useTitle } from "../utils/useTitle";
import { callEndpoint } from "../utils/callEndpoint";
import { SearchRequest, SearchResponse } from "../api";
import { ResultCard } from "../components/resultCard";

function Results() {
  const { query } = useParams();
  useTitle(`${query}`);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [searchTime, setSearchTime] = useState("");

  const { data, isLoading, refetch } = useQuery(
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
    <Box maxW="800px" mx="auto" p="5">
      <Heading as="h1" mb="2">
        Search results for "{query}"
      </Heading>
      {isLoading && <Spinner />}
      {data && <Text>The search took {searchTime} milliseconds</Text>}
      {data &&
        data.results.length > 0 &&
        data.results.map((result, i) => <ResultCard key={i} {...result} />)}
      <Center>
        <ButtonGroup mt="2">
          <Button onClick={handlePrev} disabled={page === 1}>
            Prev
          </Button>
          <Text>
            Page {page} of {totalPages}
          </Text>
          <Button onClick={handleNext} disabled={page === totalPages}>
            Next
          </Button>
        </ButtonGroup>
      </Center>
    </Box>
  );
}

export default Results;
