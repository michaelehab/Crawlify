import { Box, Text, Link, Heading } from "@chakra-ui/react";
import { Result } from "../types";

export const ResultCard: React.FC<Result> = (result) => {
  return (
    <Box p="2" m={5} border="1px solid gray">
      <Link href={`${result.url}`} color="blue.500">
        <Heading as="h2" size="md">
          {result.title}
        </Heading>
      </Link>
      <Text>{result.url}</Text>
      <Text>{result.snippet}</Text>
    </Box>
  );
};
