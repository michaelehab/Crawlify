import { ApiError } from "./apiError";

const HOST = "http://localhost:8081";

export const replaceParams = (
  endpoint: string,
  ...params: string[]
): string => {
  let url = endpoint;
  const placeholders = url.match(/:[^\/]*/g) || [];
  if (placeholders.length !== params.length) {
    throw new Error(
      `Too ${
        placeholders.length < params.length ? "many" : "few"
      } params for url: ${url}!`
    );
  }
  for (let index = 0; index < params.length; index++) {
    url = url.replace(placeholders[index], params[index]);
  }
  return url;
};

export async function callEndpoint<Request, Response>(
  endpoint: string,
  method: string,
  request?: Request
): Promise<Response> {
  console.log(`Sending request to ${endpoint}`);
  const requestBody = request ? JSON.stringify(request) : undefined;
  const response = await fetch(`${HOST}${endpoint}`, {
    method: method,
    headers: {
      "Content-Type": "application/json",
    },
    body: requestBody,
  });
  if (!response.ok) {
    let msg = (await response.json()).error;
    throw new ApiError(response.status, msg);
  }
  const isJson = response.headers
    .get("content-type")
    ?.includes("application/json");
  return isJson ? ((await response.json()) as Response) : ({} as Response);
}
