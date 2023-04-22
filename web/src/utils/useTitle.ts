import { useEffect } from "react";

export const useTitle = (pageTitle: string) => {
  useEffect(() => {
    document.title = "Crawlify | " + pageTitle;
  }, [pageTitle]);
};
