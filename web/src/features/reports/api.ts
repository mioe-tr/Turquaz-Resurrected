/*
 * Turquaz Resurrected — GPLv3
 */
import { api } from "@/lib/api";

export interface InventoryProfitRow {
  cardId: string;
  amountIn: number;
  amountOut: number;
  costIn: number;
  revenueOut: number;
  avgUnitCost: number;
  costOfSold: number;
  profit: number;
}

export async function inventoryProfit(
  from: string,
  to: string,
): Promise<InventoryProfitRow[]> {
  const { data } = await api.get<InventoryProfitRow[]>("/api/v1/reports/inventory-profit", {
    params: { from, to },
  });
  return data;
}
