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
import { accountBalance } from "./api";

export function AccountBalancePage() {
  const { t } = useTranslation();
  const [accountId, setAccountId] = useState("");
  const [asOf, setAsOf] = useState("");

  const q = useQuery({
    enabled: false,
    queryKey: ["accountBalance", accountId, asOf],
    queryFn: () => accountBalance(accountId, asOf || undefined),
  });

  const apiError = extractApiError(q.error);

  return (
    <div className="container py-6 space-y-4">
      <h1 className="text-2xl font-semibold">{t("accounting.accountBalance.title")}</h1>
      <Card>
        <CardHeader><CardTitle>{t("accounting.accountBalance.calculate")}</CardTitle></CardHeader>
        <CardContent>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 items-end">
            <div className="space-y-1.5">
              <Label htmlFor="accountId">{t("accounting.accountBalance.accountId")}</Label>
              <Input
                id="accountId"
                value={accountId}
                onChange={(e) => setAccountId(e.target.value)}
                placeholder="UUID"
                className="font-mono text-xs"
              />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="asOf">{t("accounting.trialBalance.asOf")}</Label>
              <Input
                id="asOf"
                type="datetime-local"
                value={asOf}
                onChange={(e) => setAsOf(e.target.value)}
              />
            </div>
            <Button onClick={() => q.refetch()} disabled={!accountId || q.isFetching}>
              {t("accounting.accountBalance.calculate")}
            </Button>
          </div>

          {apiError && (
            <p className="text-sm text-destructive mt-4" role="alert">{apiError.message}</p>
          )}

          {q.data && (
            <div className="mt-6 grid grid-cols-3 gap-4">
              <div className="p-4 rounded-md bg-muted">
                <div className="text-xs text-muted-foreground">
                  {t("accounting.trialBalance.totalDebit")}
                </div>
                <div className="text-2xl font-semibold">
                  {q.data.totalDebit.toLocaleString("tr-TR")}
                </div>
              </div>
              <div className="p-4 rounded-md bg-muted">
                <div className="text-xs text-muted-foreground">
                  {t("accounting.trialBalance.totalCredit")}
                </div>
                <div className="text-2xl font-semibold">
                  {q.data.totalCredit.toLocaleString("tr-TR")}
                </div>
              </div>
              <div className="p-4 rounded-md bg-primary/10">
                <div className="text-xs text-muted-foreground">
                  {t("accounting.trialBalance.net")}
                </div>
                <div className="text-2xl font-semibold">
                  {q.data.net.toLocaleString("tr-TR")}
                </div>
              </div>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
}
