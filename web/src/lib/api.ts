/*
 * Turquaz Resurrected — GPLv3
 * Axios tabanlı API istemcisi: Bearer token'ı auth store'dan otomatik ekler,
 * 401'de oturumu temizler.
 */
import axios, { AxiosError } from "axios";
import { useAuthStore } from "@/store/auth";

export const api = axios.create({
  baseURL: "/",
  headers: { "Content-Type": "application/json" },
});

api.interceptors.request.use((config) => {
  const token = useAuthStore.getState().accessToken;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    if (error.response?.status === 401) {
      useAuthStore.getState().clear();
    }
    return Promise.reject(error);
  },
);

export interface ApiError {
  status: number;
  error: string;
  message: string;
  details?: string[];
  timestamp: string;
}

export function extractApiError(err: unknown): ApiError | null {
  if (err && typeof err === "object" && "response" in err) {
    const data = (err as AxiosError<ApiError>).response?.data;
    if (data && typeof data === "object" && "message" in data) {
      return data;
    }
  }
  return null;
}
