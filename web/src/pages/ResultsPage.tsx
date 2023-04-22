import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import {
  Box,
  Heading,
  Text,
  Link,
  Spinner,
  Alert,
  AlertIcon,
  UnorderedList,
  ListItem,
  ButtonGroup,
  Button,
  Center,
} from "@chakra-ui/react"; // import Chakra UI components here

function Results() {
  const { query } = useParams();
  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [page, setPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);

  /* const fetchResults = async () => {
    setLoading(true);
    setError(null);
    try {
      // replace this with your own API endpoint
      // assume the API accepts a page parameter and returns a totalPages property
      const response = await fetch(
        `https://localhost:8081/api/search/${query}?page=${page}`
      );
      const data = await response.json();
      setResults(data.results);
      setTotalPages(data.totalPages);
    } catch (err) {
      setError(err.message);
    }
    setLoading(false);
  }; */

  /* useEffect(() => {
    fetchResults();
  }, [query, page]); */

  const handlePrev = () => {
    if (page > 1) {
      setPage(page - 1);
    }
  };

  const handleNext = () => {
    if (page < totalPages) {
      setPage(page + 1);
    }
  };

  return (
    <Box maxW="800px" mx="auto" p="5">
      <Heading as="h1" mb="2">
        Search results for "{query}"
      </Heading>
      {loading && <Spinner />}{" "}
      {error && (
        <Alert status="error">
          <AlertIcon />
          {error}
        </Alert>
      )}
      {
        /* results.length > 0 &&  */ <>
          <Box>
            {/* {results.map((result) => ( */}
            <Box key={1} p="2" m={5} border="1px solid gray">
              <Link href="https://google.com" color="blue.500">
                <Heading as="h2" size="md">
                  Page 1 Title
                </Heading>
              </Link>
              <Text>Page 1 URL</Text>
              <Text>Page 1 Decription</Text>
            </Box>
            <Box key={1} p="2" m={5} border="1px solid gray">
              <Link href="https://google.com" color="blue.500">
                <Heading as="h2" size="md">
                  Page 2 Title
                </Heading>
              </Link>
              <Text>Page 2 URL</Text>
              <Text>Page 2 Decription</Text>
            </Box>
            <Box key={1} p="2" m={5} border="1px solid gray">
              <Link href="https://google.com" color="blue.500">
                <Heading as="h2" size="md">
                  Page 3 Title
                </Heading>
              </Link>
              <Text>Page 3 URL</Text>
              <Text>Page 3 Decription</Text>
            </Box>
            {/* ))} */}
          </Box>
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
        </>
      }
    </Box>
  );
}

export default Results;
