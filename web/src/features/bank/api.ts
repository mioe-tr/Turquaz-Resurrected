/*
 * Turquaz Resurrected — GPLv3
 */
import { api } from "@/lib/api";

export interface BankCard {
  id: string;
  companyId: string;
  code: string;
  bankName: string;
  branchName: string;
  accountNo: string;
  definition: string;
  currencyId: string;
}

export interface CreateBankCard {
  code: string;
  bankName: string;
  branchName: string;
  accountNo: string;
  definition: string;
  currencyId: string;
}

export async function listBankCards(): Promise<BankCard[]> {
  const { data } = await api.get<BankCard[]>("/api/v1/bank/cards");
  return data;
}

export async function createBankCard(req: CreateBankCard): Promise<BankCard> {
  const { data } = await api.post<BankCard>("/api/v1/bank/cards", req);
  return data;
}
