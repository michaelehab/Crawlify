import { useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  Box,
  Flex,
  Input,
  HStack,
  Button,
  useColorModeValue,
  Image,
} from "@chakra-ui/react";
import logo from "../assets/logo/logo-black-no-background.png";

export default function Simple() {
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
    <Box bg={useColorModeValue("gray.100", "gray.900")} px={4}>
      <Flex h={16} alignItems={"center"} justifyContent={"space-between"}>
        <HStack spacing={8} alignItems={"center"}>
          <Image src={logo} height={"8vh"} my={5} />
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
        </HStack>
      </Flex>
    </Box>
  );
}
