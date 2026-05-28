/*
 * Turquaz Resurrected — GPLv3
 */
import { api } from "@/lib/api";

export interface UserSummary {
  id: string;
  username: string;
  realName: string;
  description: string;
}

export interface CompanyInfo {
  id: string;
  name: string;
  address: string;
  telephone: string;
  fax: string;
}

export interface UpdateCompany {
  name: string;
  address: string;
  telephone: string;
  fax: string;
}

export interface Currency {
  id: string;
  name: string;
  abbreviation: string;
  country: string;
  defaultCurrency: boolean;
  constant: boolean;
}

export interface CreateCurrency {
  name: string;
  abbreviation: string;
  country: string;
  defaultCurrency?: boolean;
  constant?: boolean;
}

export interface ExchangeRate {
  id: string;
  baseCurrencyId: string;
  exchangeCurrencyId: string;
  exchangeRatio: number;
  date: string;
}

export interface CreateExchangeRate {
  baseCurrencyId: string;
  exchangeCurrencyId: string;
  exchangeRatio: number;
  date: string;
}

export const listUsers = async (): Promise<UserSummary[]> =>
  (await api.get<UserSummary[]>("/api/v1/admin/users")).data;

export const changePassword = async (
  currentPassword: string,
  newPassword: string,
): Promise<void> => {
  await api.put("/api/v1/admin/users/me/password", { currentPassword, newPassword });
};

export const getCompany = async (): Promise<CompanyInfo> =>
  (await api.get<CompanyInfo>("/api/v1/admin/company")).data;

export const updateCompany = async (req: UpdateCompany): Promise<CompanyInfo> =>
  (await api.put<CompanyInfo>("/api/v1/admin/company", req)).data;

export const listCurrencies = async (): Promise<Currency[]> =>
  (await api.get<Currency[]>("/api/v1/currencies")).data;

export const createCurrency = async (req: CreateCurrency): Promise<Currency> =>
  (await api.post<Currency>("/api/v1/currencies", req)).data;

export const listExchangeRates = async (): Promise<ExchangeRate[]> =>
  (await api.get<ExchangeRate[]>("/api/v1/exchange-rates")).data;

export const createExchangeRate = async (req: CreateExchangeRate): Promise<ExchangeRate> =>
  (await api.post<ExchangeRate>("/api/v1/exchange-rates", req)).data;
