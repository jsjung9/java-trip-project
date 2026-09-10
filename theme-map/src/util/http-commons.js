import axios from "axios";

const accessTokenKey = "accessToken";
const server = axios.create({
  baseURL: import.meta.env.VITE_SERVER_URL,
  withCredentials: true,
  headers: { "Content-Type": "application/json" },
});

let refreshRequest = null;

server.interceptors.request.use((config) => {
  const accessToken = sessionStorage.getItem(accessTokenKey);
  if (accessToken) {
    config.headers.Authorization = `Bearer ${accessToken}`;
  }
  return config;
});

server.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    const shouldRefresh =
      error.response?.status === 401 &&
      !originalRequest?._retry &&
      !originalRequest?.url?.includes("/editor/login") &&
      !originalRequest?.url?.includes("/editor/refresh");

    if (!shouldRefresh) {
      return Promise.reject(error);
    }

    originalRequest._retry = true;
    refreshRequest ??= server
      .post("/editor/refresh")
      .then((response) => {
        const accessToken = response.data["access-token"];
        sessionStorage.setItem(accessTokenKey, accessToken);
        return accessToken;
      })
      .finally(() => {
        refreshRequest = null;
      });

    try {
      const accessToken = await refreshRequest;
      originalRequest.headers.Authorization = `Bearer ${accessToken}`;
      return server(originalRequest);
    } catch (refreshError) {
      sessionStorage.removeItem(accessTokenKey);
      window.dispatchEvent(new CustomEvent("auth-expired"));
      return Promise.reject(refreshError);
    }
  },
);

function serverAxios() {
  return server;
}

export { serverAxios };
