/*
 * Turquaz Resurrected — GPLv3
 */
import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { useTranslation } from "react-i18next";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Label } from "@/components/ui/label";
import { extractApiError } from "@/lib/api";
import { listInventoryCards, listWarehouses, stockOnHand } from "./api";

export function StockOnHandPage() {
  const { t } = useTranslation();
  const cards = useQuery({ queryKey: ["inventoryCards"], queryFn: listInventoryCards });
  const warehouses = useQuery({ queryKey: ["warehouses"], queryFn: listWarehouses });

  const [cardId, setCardId] = useState("");
  const [warehouseId, setWarehouseId] = useState("");
  const [asOf, setAsOf] = useState("");

  const stock = useQuery({
    enabled: false,
    queryKey: ["stockOnHand", cardId, warehouseId, asOf],
    queryFn: () => stockOnHand(cardId, warehouseId, asOf || undefined),
  });

  const apiError = extractApiError(stock.error);

  return (
    <div className="container py-6 space-y-4">
      <h1 className="text-2xl font-semibold">{t("inventory.stock.title")}</h1>

      <Card>
        <CardHeader><CardTitle>{t("inventory.stock.query")}</CardTitle></CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4 items-end">
            <div className="space-y-1.5">
              <Label htmlFor="cardId">{t("inventory.stock.card")}</Label>
              <select
                id="cardId"
                value={cardId}
                onChange={(e) => setCardId(e.target.value)}
                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
              >
                <option value="">—</option>
                {cards.data?.map((c) => (
                  <option key={c.id} value={c.id}>{c.code} — {c.name}</option>
                ))}
              </select>
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="warehouseId">{t("inventory.stock.warehouse")}</Label>
              <select
                id="warehouseId"
                value={warehouseId}
                onChange={(e) => setWarehouseId(e.target.value)}
                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
              >
                <option value="">—</option>
                {warehouses.data?.map((w) => (
                  <option key={w.id} value={w.id}>{w.code} — {w.name}</option>
                ))}
              </select>
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="asOf">{t("inventory.stock.asOf")}</Label>
              <input
                id="asOf"
                type="datetime-local"
                value={asOf}
                onChange={(e) => setAsOf(e.target.value)}
                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm"
              />
            </div>
            <Button
              onClick={() => stock.refetch()}
              disabled={!cardId || !warehouseId || stock.isFetching}
            >
              {t("inventory.stock.calculate")}
            </Button>
          </div>

          {apiError && (
            <p className="text-sm text-destructive mt-4" role="alert">{apiError.message}</p>
          )}

          {stock.data && (
            <div className="mt-6 p-4 rounded-md bg-muted">
              <div className="text-sm text-muted-foreground">{t("inventory.stock.result")}</div>
              <div className="text-3xl font-semibold mt-1">
                {stock.data.amount.toLocaleString("tr-TR", { maximumFractionDigits: 6 })}
              </div>
              <div className="text-xs text-muted-foreground mt-2">
                {t("inventory.stock.asOf")}: {new Date(stock.data.asOf).toLocaleString("tr-TR")}
              </div>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
