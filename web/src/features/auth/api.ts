/*
 * Turquaz Resurrected — GPLv3
 */
import { api } from "@/lib/api";

export interface LoginResponse {
  accessToken: string;
  tokenType: string;
  expiresInSeconds: number;
  userId: string;
  companyId: string;
  username: string;
}

export interface CurrentUserResponse {
  userId: string;
  companyId: string;
  username: string;
  realName: string;
}

export async function login(username: string, password: string): Promise<LoginResponse> {
  const { data } = await api.post<LoginResponse>("/auth/login", { username, password });
  return data;
}

export async function me(): Promise<CurrentUserResponse> {
  const { data } = await api.get<CurrentUserResponse>("/auth/me");
  return data;
}
