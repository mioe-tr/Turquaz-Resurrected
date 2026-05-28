/*
 * Turquaz Resurrected — GPLv3
 */
import { api } from "@/lib/api";

export interface Bill {
  id: string;
  companyId: string;
  type: number;
  documentNo: string;
  definition: string;
  billDate: string;
  dueDate: string;
  currentCardId: string;
  printed: boolean;
  open: boolean;
}

export interface CreateBill {
  type: number;
  billDate: string;
  dueDate: string;
  documentNo: string;
  definition: string;
  currentCardId: string;
  exchangeRateId: string;
  engineSequenceId: string;
}

export interface Order {
  id: string;
  companyId: string;
  type: number;
  documentNo: number;
  definition: string;
  orderDate: string;
  dueDate: string;
  deliverDate: string;
  currentCardId: string;
  billId: string;
  totalAmount: number;
  delivered: boolean;
}

export interface CreateOrder {
  type: number;
  documentNo: number;
  orderDate: string;
  dueDate: string;
  deliverDate: string;
  currentCardId: string;
  billId: string;
  definition: string;
  discountRatePercent: number;
  vatPercent: number;
  discountAmount: number;
  charges: number;
  vatAmount: number;
  totalAmount: number;
}

export interface Consignment {
  id: string;
  companyId: string;
  type: number;
  documentNo: string;
  referenceBillNo: string;
  definition: string;
  date: string;
  currentCardId: string;
  printed: boolean;
}

export interface CreateConsignment {
  type: number;
  date: string;
  documentNo: string;
  referenceBillNo: string;
  definition: string;
  currentCardId: string;
  exchangeRateId: string;
  engineSequenceId: string;
}

export const listBills = async (): Promise<Bill[]> =>
  (await api.get<Bill[]>("/api/v1/bills")).data;
export const createBill = async (r: CreateBill): Promise<Bill> =>
  (await api.post<Bill>("/api/v1/bills", r)).data;
export const printBill = async (id: string): Promise<Bill> =>
  (await api.post<Bill>(`/api/v1/bills/${id}/print`)).data;
export const closeBill = async (id: string): Promise<Bill> =>
  (await api.post<Bill>(`/api/v1/bills/${id}/close`)).data;

export const listOrders = async (): Promise<Order[]> =>
  (await api.get<Order[]>("/api/v1/orders")).data;
export const createOrder = async (r: CreateOrder): Promise<Order> =>
  (await api.post<Order>("/api/v1/orders", r)).data;
export const deliverOrder = async (id: string): Promise<Order> =>
  (await api.post<Order>(`/api/v1/orders/${id}/deliver`)).data;

export const listConsignments = async (): Promise<Consignment[]> =>
  (await api.get<Consignment[]>("/api/v1/consignments")).data;
export const createConsignment = async (r: CreateConsignment): Promise<Consignment> =>
  (await api.post<Consignment>("/api/v1/consignments", r)).data;
export const printConsignment = async (id: string): Promise<Consignment> =>
  (await api.post<Consignment>(`/api/v1/consignments/${id}/print`)).data;
