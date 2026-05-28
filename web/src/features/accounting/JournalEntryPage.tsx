/*
 * Turquaz Resurrected — GPLv3
 * Yevmiye fişi formu — çoklu satır + Σborç=Σalacak canlı doğrulama.
 */
import { useState } from "react";
import { useMutation } from "@tanstack/react-query";
import { useTranslation } from "react-i18next";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { extractApiError } from "@/lib/api";
import { postJournal, type JournalLineRequest } from "./api";

interface LineState {
  accountId: string;
  debit: string;
  credit: string;
  description: string;
}

const empty = (): LineState => ({ accountId: "", debit: "0", credit: "0", description: "" });

export function JournalEntryPage() {
  const { t } = useTranslation();

  const [transactionDate, setTransactionDate] = useState(
    new Date().toISOString().slice(0, 10),
  );
  const [documentNo, setDocumentNo] = useState("");
  const [description, setDescription] = useState("");
  const [ctx, setCtx] = useState({
    journalId: "",
    transactionTypeId: "",
    moduleId: "",
    engineSequenceId: "",
    exchangeRateId: "",
    exchangeRate: "1",
  });
  const [lines, setLines] = useState<LineState[]>([empty(), empty()]);

  const totalDebit = lines.reduce((s, l) => s + Number(l.debit || 0), 0);
  const totalCredit = lines.reduce((s, l) => s + Number(l.credit || 0), 0);
  const balanced = Math.abs(totalDebit - totalCredit) < 0.0001 && totalDebit > 0;

  const mutation = useMutation({
    mutationFn: () => {
      const reqLines: JournalLineRequest[] = lines.map((l) => ({
        accountId: l.accountId,
        debit: Number(l.debit || 0),
        credit: Number(l.credit || 0),
        description: l.description,
      }));
      return postJournal({
        transactionDate,
        documentNo,
        description,
        journalId: ctx.journalId,
        transactionTypeId: ctx.transactionTypeId,
        moduleId: ctx.moduleId,
        engineSequenceId: ctx.engineSequenceId,
        exchangeRateId: ctx.exchangeRateId,
        exchangeRate: Number(ctx.exchangeRate || 1),
        lines: reqLines,
      });
    },
  });

  const apiError = extractApiError(mutation.error);

  const updateLine = (i: number, patch: Partial<LineState>) => {
    setLines((prev) => prev.map((l, idx) => (idx === i ? { ...l, ...patch } : l)));
  };

  return (
    <div className="container py-6 space-y-4">
      <h1 className="text-2xl font-semibold">{t("accounting.journal.title")}</h1>

      <Card>
        <CardHeader><CardTitle>{t("accounting.journal.newEntry")}</CardTitle></CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div className="space-y-1.5">
              <Label htmlFor="date">{t("common.date")}</Label>
              <Input
                id="date"
                type="date"
                value={transactionDate}
                onChange={(e) => setTransactionDate(e.target.value)}
              />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="docNo">{t("common.documentNo")}</Label>
              <Input id="docNo" value={documentNo} onChange={(e) => setDocumentNo(e.target.value)} />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="desc">{t("common.description")}</Label>
              <Input id="desc" value={description} onChange={(e) => setDescription(e.target.value)} />
            </div>
          </div>

          <details className="border rounded-md">
            <summary className="px-3 py-2 cursor-pointer bg-muted text-sm">
              {t("accounting.journal.context")}
            </summary>
            <div className="p-3 grid grid-cols-1 md:grid-cols-3 gap-3">
              {([
                ["journalId", "accounting.journal.journalId"],
                ["transactionTypeId", "accounting.journal.transactionTypeId"],
                ["moduleId", "accounting.journal.moduleId"],
                ["engineSequenceId", "accounting.journal.sequenceId"],
                ["exchangeRateId", "accounting.journal.exchangeRateId"],
                ["exchangeRate", "accounting.journal.exchangeRate"],
              ] as const).map(([key, label]) => (
                <div key={key} className="space-y-1.5">
                  <Label htmlFor={key}>{t(label)}</Label>
                  <Input
                    id={key}
                    className="font-mono text-xs"
                    value={ctx[key]}
                    onChange={(e) => setCtx({ ...ctx, [key]: e.target.value })}
                  />
                </div>
              ))}
            </div>
          </details>

          <div className="space-y-2">
            <div className="flex items-center justify-between">
              <Label>{t("accounting.journal.lines")}</Label>
              <Button
                variant="outline"
                size="sm"
                type="button"
                onClick={() => setLines((p) => [...p, empty()])}
              >
                + {t("accounting.journal.addLine")}
              </Button>
            </div>
            <table className="w-full text-sm">
              <thead className="bg-muted text-left">
                <tr>
                  <th className="px-2 py-1 w-1/3">{t("accounting.journal.account")}</th>
                  <th className="px-2 py-1 text-right">{t("accounting.journal.debit")}</th>
                  <th className="px-2 py-1 text-right">{t("accounting.journal.credit")}</th>
                  <th className="px-2 py-1">{t("common.description")}</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {lines.map((line, i) => (
                  <tr key={i} className="border-t">
                    <td className="px-2 py-1">
                      <Input
                        className="font-mono text-xs"
                        value={line.accountId}
                        onChange={(e) => updateLine(i, { accountId: e.target.value })}
                        placeholder="UUID"
                      />
                    </td>
                    <td className="px-2 py-1">
                      <Input
                        type="number"
                        step="0.0001"
                        className="text-right"
                        value={line.debit}
                        onChange={(e) => updateLine(i, { debit: e.target.value })}
                      />
                    </td>
                    <td className="px-2 py-1">
                      <Input
                        type="number"
                        step="0.0001"
                        className="text-right"
                        value={line.credit}
                        onChange={(e) => updateLine(i, { credit: e.target.value })}
                      />
                    </td>
                    <td className="px-2 py-1">
                      <Input
                        value={line.description}
                        onChange={(e) => updateLine(i, { description: e.target.value })}
                      />
                    </td>
                    <td className="px-2 py-1">
                      {lines.length > 2 && (
                        <Button
                          type="button"
                          variant="ghost"
                          size="sm"
                          onClick={() =>
                            setLines((p) => p.filter((_, idx) => idx !== i))
                          }
                        >
                          {t("accounting.journal.removeLine")}
                        </Button>
                      )}
                    </td>
                  </tr>
                ))}
                <tr className="border-t bg-muted/50 font-medium">
                  <td className="px-2 py-2 text-right">Σ</td>
                  <td className="px-2 py-2 text-right">{totalDebit.toLocaleString("tr-TR")}</td>
                  <td className="px-2 py-2 text-right">{totalCredit.toLocaleString("tr-TR")}</td>
                  <td className="px-2 py-2">
                    {!balanced && (
                      <span className="text-destructive text-sm">
                        {t("accounting.journal.imbalance")}
                      </span>
                    )}
                    {balanced && (
                      <span className="text-primary text-sm">✓ dengeli</span>
                    )}
                  </td>
                  <td></td>
                </tr>
              </tbody>
            </table>
          </div>

          {apiError && (
            <p className="text-sm text-destructive" role="alert">{apiError.message}</p>
          )}
          {mutation.data && (
            <p className="text-sm text-primary">
              ✓ Postlandı: {mutation.data.transactionId}
            </p>
          )}

          <div className="flex justify-end">
            <Button
              onClick={() => mutation.mutate()}
              disabled={!balanced || mutation.isPending}
            >
              {t("accounting.journal.post")}
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}
