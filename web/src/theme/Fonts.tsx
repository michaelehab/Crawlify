import { Global } from "@emotion/react";

const Fonts = () => (
  <Global
    styles={`
      /* latin */
      @font-face {
        font-family: 'Crawlify-heading';
        font-style: normal;
        src: url('./fonts/crawlify-source-code-pro-bold.ttf') format("truetype");
      }
      /* latin */
      @font-face {
        font-family: 'Crawlify-body';
        font-style: normal;
        src: url(./fonts/crawlify-hind-guntur-light.ttf) format("truetype");
      }
      `}
  />
);

export default Fonts;
