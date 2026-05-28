/*
 * Turquaz Resurrected — GPLv3
 */
import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { useTranslation } from "react-i18next";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Label } from "@/components/ui/label";
import { Input } from "@/components/ui/input";
import { downloadExcel } from "@/lib/excelExport";
import { trialBalance } from "./api";

export function TrialBalancePage() {
  const { t } = useTranslation();
  const [asOf, setAsOf] = useState("");

  const q = useQuery({
    queryKey: ["trialBalance", asOf],
    queryFn: () => trialBalance(asOf || undefined),
  });

  const totalDebit = (q.data ?? []).reduce((s, l) => s + l.totalDebit, 0);
  const totalCredit = (q.data ?? []).reduce((s, l) => s + l.totalCredit, 0);

  return (
    <div className="container py-6 space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold">{t("accounting.trialBalance.title")}</h1>
        {q.data && q.data.length > 0 && (
          <Button
            variant="outline"
            onClick={() =>
              downloadExcel("mizan", "Mizan", [
                { header: t("accounting.trialBalance.account"), value: (l) => l.accountId },
                { header: t("accounting.trialBalance.totalDebit"), value: (l) => l.totalDebit },
                { header: t("accounting.trialBalance.totalCredit"), value: (l) => l.totalCredit },
                { header: t("accounting.trialBalance.net"), value: (l) => l.net },
              ], q.data)
            }
          >
            {t("common.exportExcel")}
          </Button>
        )}
      </div>
      <Card>
        <CardHeader><CardTitle>{t("accounting.trialBalance.calculate")}</CardTitle></CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 items-end">
            <div className="space-y-1.5">
              <Label htmlFor="asOf">{t("accounting.trialBalance.asOf")}</Label>
              <Input
                id="asOf"
                type="datetime-local"
                value={asOf}
                onChange={(e) => setAsOf(e.target.value)}
              />
            </div>
            <Button onClick={() => q.refetch()} disabled={q.isFetching}>
              {t("accounting.trialBalance.calculate")}
            </Button>
          </div>

          {q.data && q.data.length > 0 && (
            <div className="mt-4 text-sm text-muted-foreground">
              Toplam: borç {totalDebit.toLocaleString("tr-TR")}, alacak{" "}
              {totalCredit.toLocaleString("tr-TR")}
              {Math.abs(totalDebit - totalCredit) > 0.0001 && (
                <span className="text-destructive ml-2">⚠ dengesiz</span>
              )}
            </div>
          )}
        </CardContent>
      </Card>

      <Card>
        <CardContent className="p-0">
          {q.isLoading && <div className="p-6">{t("common.loading")}</div>}
          {q.data && q.data.length === 0 && (
            <div className="p-6 text-muted-foreground">{t("common.empty")}</div>
          )}
          {q.data && q.data.length > 0 && (
            <table className="w-full text-sm">
              <thead className="bg-muted text-left">
                <tr>
                  <th className="px-4 py-2">{t("accounting.trialBalance.account")}</th>
                  <th className="px-4 py-2 text-right">
                    {t("accounting.trialBalance.totalDebit")}
                  </th>
                  <th className="px-4 py-2 text-right">
                    {t("accounting.trialBalance.totalCredit")}
                  </th>
                  <th className="px-4 py-2 text-right">{t("accounting.trialBalance.net")}</th>
                </tr>
              </thead>
              <tbody>
                {q.data.map((l) => (
                  <tr key={l.accountId} className="border-t">
                    <td className="px-4 py-2 font-mono text-xs">{l.accountId}</td>
                    <td className="px-4 py-2 text-right">
                      {l.totalDebit.toLocaleString("tr-TR")}
                    </td>
                    <td className="px-4 py-2 text-right">
                      {l.totalCredit.toLocaleString("tr-TR")}
                    </td>
                    <td className="px-4 py-2 text-right font-medium">
                      {l.net.toLocaleString("tr-TR")}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
