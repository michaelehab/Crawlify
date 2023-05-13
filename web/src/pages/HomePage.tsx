import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Box, Center, Image, Input, Button } from "@chakra-ui/react";
import logo from "../assets/logo/logo-black-no-background.png";

function Home() {
  const [query, setQuery] = useState("");
  const navigate = useNavigate();

  const handleChange = (event: any) => {
    setQuery(event.target.value);
  };

  const handleSubmit = (event: any) => {
    event.preventDefault();
    navigate(`/results/${encodeURI(query)}`);
  };

  return (
    <Center h="100vh" flexDirection={"column"}>
      <Image borderRadius="md" src={logo} alt="logo" height={"50vh"} />{" "}
      <Box as="form" onSubmit={handleSubmit} mt={10} display="flex">
        <Input
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
    </Center>
  );
}

export default Home;
