/*
 * Turquaz Resurrected — GPLv3
 */
import { api } from "@/lib/api";

export interface CashCard {
  id: string;
  companyId: string;
  name: string;
  definition: string;
  accountingAccountsId: string;
}

export interface CreateCashCard {
  name: string;
  definition: string;
  accountingAccountsId: string;
}

export async function listCashCards(): Promise<CashCard[]> {
  const { data } = await api.get<CashCard[]>("/api/v1/cash/cards");
  return data;
}

export async function createCashCard(req: CreateCashCard): Promise<CashCard> {
  const { data } = await api.post<CashCard>("/api/v1/cash/cards", req);
  return data;
}
