import { Box, Text, Link, Heading } from "@chakra-ui/react";
import { SearchResult } from "../types";
import parse from "html-react-parser";

export const ResultCard: React.FC<SearchResult> = (result) => {
  return (
    <Box p="2" m={5} border="1px solid gray">
      <Link href={`${result.url}`} color="blue.500">
        <Heading as="h2" size="md">
          {result.title}
        </Heading>
      </Link>
      <Text>{result.url}</Text>
      <Text>{parse(result.snippet)}</Text>
    </Box>
  );
};
