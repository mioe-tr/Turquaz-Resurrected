/*
 * Turquaz Resurrected — GPLv3
 */
import { api } from "@/lib/api";

export interface Cheque {
  id: string;
  companyId: string;
  chequeNo: string;
  portfolioNo: string;
  bankId: string;
  amount: number;
  debtor: string;
  dueDate: string;
  valueDate: string;
  type: number;
}

export interface CreateCheque {
  chequeNo: string;
  portfolioNo: string;
  bankId: string;
  currencyId: string;
  exchangeRateId: string;
  exchangeRate: number;
  bankName: string;
  bankBranchName: string;
  bankAccountNo: string;
  amount: number;
  debtor: string;
  paymentPlace: string;
  dueDate: string; // ISO date
  valueDate: string;
  type: number;
}

export async function listCheques(type?: number): Promise<Cheque[]> {
  const { data } = await api.get<Cheque[]>("/api/v1/cheques", {
    params: type ? { type } : undefined,
  });
  return data;
}

export async function createCheque(req: CreateCheque): Promise<Cheque> {
  const { data } = await api.post<Cheque>("/api/v1/cheques", req);
  return data;
}
