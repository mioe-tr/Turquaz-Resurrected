/*
 * Turquaz Resurrected — GPLv3
 */
import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { useTranslation } from "react-i18next";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { extractApiError } from "@/lib/api";
import { downloadExcel } from "@/lib/excelExport";
import { inventoryProfit } from "./api";

const isoStart = (date: string) => (date ? `${date}T00:00:00Z` : "");
const isoEnd = (date: string) => (date ? `${date}T23:59:59Z` : "");

export function InventoryProfitPage() {
  const { t } = useTranslation();
  const [from, setFrom] = useState("");
  const [to, setTo] = useState("");

  const q = useQuery({
    enabled: false,
    queryKey: ["inventoryProfit", from, to],
    queryFn: () => inventoryProfit(isoStart(from), isoEnd(to)),
  });

  const apiError = extractApiError(q.error);

  const totalProfit = (q.data ?? []).reduce((s, r) => s + r.profit, 0);
  const totalRevenue = (q.data ?? []).reduce((s, r) => s + r.revenueOut, 0);

  return (
    <div className="container py-6 space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold">{t("reports.inventoryProfit.title")}</h1>
        {q.data && q.data.length > 0 && (
          <Button
            variant="outline"
            onClick={() =>
              downloadExcel("stok_kar_analizi", "Stok Kâr Analizi", [
                { header: t("reports.inventoryProfit.card"), value: (r) => r.cardId },
                { header: t("reports.inventoryProfit.amountIn"), value: (r) => r.amountIn },
                { header: t("reports.inventoryProfit.amountOut"), value: (r) => r.amountOut },
                { header: t("reports.inventoryProfit.costIn"), value: (r) => r.costIn },
                { header: t("reports.inventoryProfit.revenueOut"), value: (r) => r.revenueOut },
                { header: t("reports.inventoryProfit.avgUnitCost"), value: (r) => r.avgUnitCost },
                { header: t("reports.inventoryProfit.costOfSold"), value: (r) => r.costOfSold },
                { header: t("reports.inventoryProfit.profit"), value: (r) => r.profit },
              ], q.data)
            }
          >
            {t("common.exportExcel")}
          </Button>
        )}
      </div>
      <Card>
        <CardHeader><CardTitle>{t("reports.inventoryProfit.run")}</CardTitle></CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 items-end">
            <div className="space-y-1.5">
              <Label htmlFor="from">{t("reports.inventoryProfit.from")}</Label>
              <Input id="from" type="date" value={from} onChange={(e) => setFrom(e.target.value)} />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="to">{t("reports.inventoryProfit.to")}</Label>
              <Input id="to" type="date" value={to} onChange={(e) => setTo(e.target.value)} />
            </div>
            <Button onClick={() => q.refetch()} disabled={!from || !to || q.isFetching}>
              {t("reports.inventoryProfit.run")}
            </Button>
          </div>
          {apiError && (
            <p className="text-sm text-destructive mt-4">{apiError.message}</p>
          )}
        </CardContent>
      </Card>

      <Card>
        <CardContent className="p-0">
          {q.data && q.data.length === 0 && (
            <div className="p-6 text-muted-foreground">{t("common.empty")}</div>
          )}
          {q.data && q.data.length > 0 && (
            <>
              <div className="p-4 bg-muted/50 text-sm flex gap-6">
                <span>Σ Gelir: <b>{totalRevenue.toLocaleString("tr-TR")}</b></span>
                <span>Σ Kâr: <b>{totalProfit.toLocaleString("tr-TR")}</b></span>
              </div>
              <table className="w-full text-sm">
                <thead className="bg-muted text-left">
                  <tr>
                    <th className="px-3 py-2">{t("reports.inventoryProfit.card")}</th>
                    <th className="px-3 py-2 text-right">{t("reports.inventoryProfit.amountIn")}</th>
                    <th className="px-3 py-2 text-right">{t("reports.inventoryProfit.amountOut")}</th>
                    <th className="px-3 py-2 text-right">{t("reports.inventoryProfit.costIn")}</th>
                    <th className="px-3 py-2 text-right">{t("reports.inventoryProfit.revenueOut")}</th>
                    <th className="px-3 py-2 text-right">{t("reports.inventoryProfit.avgUnitCost")}</th>
                    <th className="px-3 py-2 text-right">{t("reports.inventoryProfit.costOfSold")}</th>
                    <th className="px-3 py-2 text-right font-bold">{t("reports.inventoryProfit.profit")}</th>
                  </tr>
                </thead>
                <tbody>
                  {q.data.map((r) => (
                    <tr key={r.cardId} className="border-t">
                      <td className="px-3 py-2 font-mono text-xs">{r.cardId.slice(0, 8)}</td>
                      <td className="px-3 py-2 text-right">{r.amountIn.toLocaleString("tr-TR")}</td>
                      <td className="px-3 py-2 text-right">{r.amountOut.toLocaleString("tr-TR")}</td>
                      <td className="px-3 py-2 text-right">{r.costIn.toLocaleString("tr-TR")}</td>
                      <td className="px-3 py-2 text-right">{r.revenueOut.toLocaleString("tr-TR")}</td>
                      <td className="px-3 py-2 text-right">{r.avgUnitCost.toLocaleString("tr-TR")}</td>
                      <td className="px-3 py-2 text-right">{r.costOfSold.toLocaleString("tr-TR")}</td>
                      <td className="px-3 py-2 text-right font-medium">
                        {r.profit.toLocaleString("tr-TR")}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
