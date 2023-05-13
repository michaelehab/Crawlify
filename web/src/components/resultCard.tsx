import {
  Text,
  Link,
  Heading,
  Badge,
  Image,
  Stack,
  Container,
} from "@chakra-ui/react";
import { SearchResult } from "../types";
import parse from "html-react-parser";

export const ResultCard: React.FC<SearchResult> = (result) => {
  const websiteName = result.url
    .replace(/https?:\/\/(www\.)?/, "")
    .split("/")[0]
    .replace(/\.com$/, "");

  return (
    <Container p="4" my="4" border="1px solid gray" shadow="md" maxW="900px">
      <Stack direction="row" align="center" spacing="4">
        <Image
          src={`https://s2.googleusercontent.com/s2/favicons?domain_url=${result.url}`}
          fallbackSrc="https://via.placeholder.com/32"
          boxSize="32px"
          borderRadius="full"
        />
        <Stack direction="column">
          <Heading size="sm" color="blue.500">
            {websiteName}
          </Heading>

          <Link href={`${result.url}`} color="blue.500">
            <Heading as="h2" size="md">
              {result.title}
            </Heading>
          </Link>
        </Stack>
      </Stack>
      <Badge colorScheme={"blue"} variant={"outline"}>
        {result.url}
      </Badge>
      <Text>{parse(result.snippet)}</Text>
    </Container>
  );
};
