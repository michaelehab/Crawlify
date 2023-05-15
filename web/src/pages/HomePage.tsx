import { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import {
  Box,
  Center,
  Image,
  Input,
  Button,
  List,
  ListItem,
} from "@chakra-ui/react";
import logo from "../assets/logo/logo-black-no-background.png";
import { useTitle } from "../utils/useTitle";
import { Suggestion } from "../types";

function Home() {
  useTitle("Home Page");
  const [query, setQuery] = useState("");
  const [suggestions, setSuggestions] = useState<Suggestion[]>([]);
  const navigate = useNavigate();

  const handleChange = (event: any) => {
    setQuery(event.target.value);
  };

  const handleSubmit = (event: any) => {
    event.preventDefault();
    navigate(`/results/${encodeURI(query)}`);
  };

  async function fetchSuggestions() {
    try {
      const response = await fetch(
        `http://localhost:8081/search/${encodeURI(query)}?ie=UTF-8`
      );
      const data = await response.json();
      setSuggestions(data);
    } catch (error) {
      console.error(error);
    }
  }

  useEffect(() => {
    const fetchData = async () => {
      if (query !== "") await fetchSuggestions();
    };
    fetchData();
  }, [query]);

  return (
    <Center h="100vh" flexDirection={"column"}>
      <Image borderRadius="md" src={logo} alt="logo" height={"50vh"} />
      <Box as="form" onSubmit={handleSubmit} mt={10} display="flex">
        <Input
          name="searchQueryInput"
          type="text"
          value={query}
          onChange={handleChange}
          placeholder="Enter your search query"
          borderRadius="5px"
          width={"50vw"}
        />
        <Button
          type="submit"
          bg={"#0718C4"}
          color={"white"}
          ml={2}
          borderRadius="5px"
        >
          Search
        </Button>
      </Box>
      <List width={"50vw"} mt={2} overflowY="auto" borderWidth={1}>
        {suggestions &&
          suggestions.map((suggestion) => (
            <Link to={`/results/${suggestion.query}`}>
              <ListItem key={suggestion.query} p={2}>
                {suggestion.query}
              </ListItem>
            </Link>
          ))}
      </List>
    </Center>
  );
}

export default Home;
