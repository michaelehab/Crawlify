import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Box, Center, Image, Input, Button } from "@chakra-ui/react";
import logo from "../assets/logo/logo-black-no-background.png";
import Autosuggest from "react-autosuggest";

function Home() {
  const [query, setQuery] = useState("");
  const navigate = useNavigate();
  const [value, setValue] = useState("");
  const [suggestions, setSuggestions] = useState<string[]>([]);

  const fetchSuggestions = async (value: string) => {
    try {
      const response = await fetch(`your-api-url?q=${value}`);
      const data = await response.json();
      setSuggestions(data.suggestions);
    } catch (error) {
      console.error(error);
    }
  };

  const onInputChange = (event: React.FormEvent, { newValue }: Autosuggest.ChangeEvent) => {
    setValue(newValue);
  };

  const renderSuggestion = (suggestion: string) => <div>{suggestion}</div>;

  const onSuggestionSelected = (event: React.FormEvent, { suggestion }: Autosuggest.SuggestionSelectedEventData<string>) => {
    navigate(`/results/${encodeURI(suggestion)}`);
  };

  const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setQuery(event.target.value);
  };

  const handleSubmit = (event: any) => {
    event.preventDefault();
    navigate(`/results/${encodeURI(query)}`);
  };
  const getSuggestions = (value: string) => {
    fetchSuggestions(value);
  };

  const inputProps = {
    placeholder: "Enter your search query",
    value,
    onChange: onInputChange,
  };

  return (
      <Center h="100vh" flexDirection={"column"}>
        <Image borderRadius="md" src={logo} alt="logo" height={"50vh"} />
        <Box as="form" onSubmit={handleSubmit} mt={10} display="flex">
          <Autosuggest
              suggestions={suggestions}
              onSuggestionsFetchRequested={({ value }) => getSuggestions(value)}
              onSuggestionsClearRequested={() => setSuggestions([])}
              getSuggestionValue={(suggestion) => suggestion}
              renderSuggestion={renderSuggestion}
              inputProps={inputProps}
              onSuggestionSelected={onSuggestionSelected}
          />
          <Button type="submit" bg={"#0718C4"} color={"white"} ml={2} borderRadius="5px">
            Search
          </Button>
        </Box>
      </Center>
  );
}

export default Home;