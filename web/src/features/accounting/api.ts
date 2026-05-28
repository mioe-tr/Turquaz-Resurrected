/*
 * Turquaz Resurrected — GPLv3
 */
import { api } from "@/lib/api";

export interface JournalLineRequest {
  accountId: string;
  debit: number;
  credit: number;
  description: string;
}

export interface PostJournalRequest {
  transactionDate: string; // ISO date (YYYY-MM-DD)
  documentNo: string;
  description: string;
  journalId: string;
  transactionTypeId: string;
  moduleId: string;
  engineSequenceId: string;
  exchangeRateId: string;
  exchangeRate: number;
  lines: JournalLineRequest[];
}

export interface JournalEntryResponse {
  transactionId: string;
  journalId: string;
  transactionDate: string;
  documentNo: string;
  description: string;
}

export interface TrialBalanceLineResponse {
  accountId: string;
  totalDebit: number;
  totalCredit: number;
  net: number;
}

export interface AccountBalanceResponse extends TrialBalanceLineResponse {
  asOf: string;
}

export async function postJournal(req: PostJournalRequest): Promise<JournalEntryResponse> {
  const { data } = await api.post<JournalEntryResponse>("/api/v1/accounting/journal", req);
  return data;
}

export async function trialBalance(asOf?: string): Promise<TrialBalanceLineResponse[]> {
  const { data } = await api.get<TrialBalanceLineResponse[]>(
    "/api/v1/accounting/trial-balance",
    { params: asOf ? { asOf } : undefined },
  );
  return data;
}

export async function accountBalance(
  accountId: string,
  asOf?: string,
): Promise<AccountBalanceResponse> {
  const { data } = await api.get<AccountBalanceResponse>(
    `/api/v1/accounting/accounts/${accountId}/balance`,
    { params: asOf ? { asOf } : undefined },
  );
  return data;
}
