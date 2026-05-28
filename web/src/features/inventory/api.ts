/*
 * Turquaz Resurrected — GPLv3
 */
import { api } from "@/lib/api";

export interface InventoryCard {
  id: string;
  companyId: string;
  code: string;
  name: string;
  definition: string;
  minimumAmount: number;
  maximumAmount: number;
  vatRate: number;
  discountPercent: number;
  specialVatRate: number;
}

export interface CreateInventoryCard {
  code: string;
  name: string;
  definition: string;
  minimumAmount: number;
  maximumAmount: number;
  vatRate: number;
  discountPercent: number;
  specialVatRate: number;
}

export interface Warehouse {
  id: string;
  companyId: string;
  code: string;
  name: string;
  address: string;
  city: string;
  telephone: string;
  description: string;
}

export interface CreateWarehouse {
  code: string;
  name: string;
  address: string;
  city: string;
  telephone: string;
  description: string;
}

export interface StockOnHand {
  cardId: string;
  warehouseId: string;
  amount: number;
  asOf: string;
}

export async function listInventoryCards(): Promise<InventoryCard[]> {
  const { data } = await api.get<InventoryCard[]>("/api/v1/inventory/cards");
  return data;
}
export async function createInventoryCard(req: CreateInventoryCard): Promise<InventoryCard> {
  const { data } = await api.post<InventoryCard>("/api/v1/inventory/cards", req);
  return data;
}

export async function listWarehouses(): Promise<Warehouse[]> {
  const { data } = await api.get<Warehouse[]>("/api/v1/inventory/warehouses");
  return data;
}
export async function createWarehouse(req: CreateWarehouse): Promise<Warehouse> {
  const { data } = await api.post<Warehouse>("/api/v1/inventory/warehouses", req);
  return data;
}

export async function stockOnHand(
  cardId: string,
  warehouseId: string,
  asOf?: string,
): Promise<StockOnHand> {
  const params: Record<string, string> = { cardId, warehouseId };
  if (asOf) params.asOf = asOf;
  const { data } = await api.get<StockOnHand>("/api/v1/inventory/ledger/stock", { params });
  return data;
}
