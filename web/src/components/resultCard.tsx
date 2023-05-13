import {
  Box,
  Text,
  Link,
  Heading,
  Badge,
  Image,
  Stack,
} from "@chakra-ui/react";
import { SearchResult } from "../types";
import parse from "html-react-parser";

export const ResultCard: React.FC<SearchResult> = (result) => {
  return (
    <Box p="4" m="4" border="1px solid gray" shadow="md">
      <Stack direction="row" align="center" spacing="4">
        <Image
          src={`https://s2.googleusercontent.com/s2/favicons?domain_url=${result.url}`}
          fallbackSrc="https://via.placeholder.com/32"
          boxSize="32px"
        />
        <Link href={`${result.url}`} color="blue.500">
          <Heading as="h2" size="md">
            {result.title}
          </Heading>
        </Link>
      </Stack>
      <Badge colorScheme={"blue"} variant={"outline"}>
        {result.url}
      </Badge>
      <Text>{parse(result.snippet)}</Text>
    </Box>
  );
};
