/*
 * Turquaz Resurrected — GPLv3
 */
import { api } from "@/lib/api";

export interface CurrentCard {
  id: string;
  companyId: string;
  code: string;
  name: string;
  definition: string;
  address: string;
  taxDepartment: string;
  taxNumber: string;
  creditLimit: number;
  riskLimit: number;
  discountRate: number;
  discountPayment: number;
  daysToValue: number | null;
}

export interface CreateCurrentCardRequest {
  code: string;
  name: string;
  definition: string;
  address: string;
  taxDepartment: string;
  taxNumber: string;
  creditLimit: number;
  riskLimit: number;
  discountRate: number;
  discountPayment: number;
  daysToValue: number;
}

export async function listCurrentCards(): Promise<CurrentCard[]> {
  const { data } = await api.get<CurrentCard[]>("/api/v1/current-cards");
  return data;
}

export async function createCurrentCard(req: CreateCurrentCardRequest): Promise<CurrentCard> {
  const { data } = await api.post<CurrentCard>("/api/v1/current-cards", req);
  return data;
}
