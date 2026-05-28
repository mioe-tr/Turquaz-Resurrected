/*
 * Turquaz Resurrected — GPLv3
 */
import { useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { z } from "zod";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { extractApiError } from "@/lib/api";
import { downloadExcel } from "@/lib/excelExport";
import { toast } from "@/lib/toast";
import { createCheque, listCheques } from "./api";

const schema = z.object({
  chequeNo: z.string().min(1).max(50),
  portfolioNo: z.string().min(1).max(30),
  bankId: z.string().uuid(),
  currencyId: z.string().uuid(),
  exchangeRateId: z.string().uuid(),
  exchangeRate: z.coerce.number().positive(),
  bankName: z.string().min(1).max(100),
  bankBranchName: z.string().min(1).max(100),
  bankAccountNo: z.string().max(100),
  amount: z.coerce.number().positive(),
  debtor: z.string().min(1).max(100),
  paymentPlace: z.string().max(50),
  dueDate: z.string(),
  valueDate: z.string(),
  type: z.coerce.number().int().min(1).max(2),
});
type FormValues = z.infer<typeof schema>;

export function ChequesPage() {
  const { t } = useTranslation();
  const qc = useQueryClient();
  const [show, setShow] = useState(false);
  const [filter, setFilter] = useState<"all" | "received" | "given">("all");

  const { data } = useQuery({
    queryKey: ["cheques", filter],
    queryFn: () =>
      listCheques(filter === "received" ? 1 : filter === "given" ? 2 : undefined),
  });

  const { register, handleSubmit, reset, formState: { errors } } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      chequeNo: "", portfolioNo: "",
      bankId: "", currencyId: "", exchangeRateId: "", exchangeRate: 1,
      bankName: "", bankBranchName: "", bankAccountNo: "",
      amount: 0, debtor: "", paymentPlace: "",
      dueDate: "", valueDate: "", type: 1,
    },
  });

  const mutation = useMutation({
    mutationFn: createCheque,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["cheques"] });
      reset();
      setShow(false);
      toast.success(t("cheques.title") + " — " + t("common.created"));
    },
    onError: (e) => toast.apiError(e),
  });
  const apiError = extractApiError(mutation.error);

  return (
    <div className="container py-6 space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold">{t("cheques.title")}</h1>
        <div className="flex gap-2">
          <select
            value={filter}
            onChange={(e) => setFilter(e.target.value as "all" | "received" | "given")}
            className="h-10 rounded-md border border-input bg-background px-3 text-sm"
          >
            <option value="all">{t("common.actions")}</option>
            <option value="received">{t("cheques.received")}</option>
            <option value="given">{t("cheques.given")}</option>
          </select>
          {data && data.length > 0 && (
            <Button
              variant="outline"
              onClick={() =>
                downloadExcel("cek_senet", "Çek/Senet", [
                  { header: t("cheques.chequeNo"), value: (c) => c.chequeNo },
                  { header: t("cheques.debtor"), value: (c) => c.debtor },
                  { header: t("cheques.amount"), value: (c) => c.amount },
                  { header: t("cheques.dueDate"), value: (c) => c.dueDate },
                  { header: t("cheques.type"), value: (c) => (c.type === 1 ? t("cheques.received") : t("cheques.given")) },
                ], data)
              }
            >
              {t("common.exportExcel")}
            </Button>
          )}
          <Button onClick={() => setShow((v) => !v)}>
            {show ? t("common.cancel") : t("cheques.new")}
          </Button>
        </div>
      </div>

      {show && (
        <Card>
          <CardHeader><CardTitle>{t("cheques.new")}</CardTitle></CardHeader>
          <CardContent>
            <form
              className="grid grid-cols-1 md:grid-cols-3 gap-3"
              onSubmit={handleSubmit((v) => mutation.mutate(v))}
            >
              {([
                ["chequeNo", "cheques.chequeNo", "text"],
                ["portfolioNo", "cheques.portfolioNo", "text"],
                ["amount", "cheques.amount", "number"],
                ["debtor", "cheques.debtor", "text"],
                ["paymentPlace", "cheques.paymentPlace", "text"],
                ["bankName", "cheques.bankName", "text"],
                ["bankBranchName", "cheques.branch", "text"],
                ["bankAccountNo", "cheques.accountNo", "text"],
                ["dueDate", "cheques.dueDate", "date"],
                ["valueDate", "cheques.valueDate", "date"],
                ["bankId", "cheques.bankId", "text"],
                ["currencyId", "cheques.currencyId", "text"],
                ["exchangeRateId", "cheques.exchangeRateId", "text"],
                ["exchangeRate", "cheques.exchangeRate", "number"],
              ] as const).map(([f, l, type]) => (
                <div key={f} className="space-y-1.5">
                  <Label htmlFor={f}>{t(l)}</Label>
                  <Input
                    id={f}
                    type={type}
                    step={type === "number" ? "any" : undefined}
                    className={
                      ["bankId", "currencyId", "exchangeRateId"].includes(f as string)
                        ? "font-mono text-xs" : ""
                    }
                    {...register(f)}
                  />
                  {errors[f] && <p className="text-sm text-destructive">{errors[f]?.message}</p>}
                </div>
              ))}
              <div className="space-y-1.5">
                <Label htmlFor="type">{t("cheques.type")}</Label>
                <select
                  id="type"
                  {...register("type")}
                  className="h-10 w-full rounded-md border border-input bg-background px-3 text-sm"
                >
                  <option value={1}>{t("cheques.received")}</option>
                  <option value={2}>{t("cheques.given")}</option>
                </select>
              </div>

              {apiError && (
                <p className="text-sm text-destructive md:col-span-3">{apiError.message}</p>
              )}
              <div className="md:col-span-3 flex gap-2 justify-end">
                <Button type="button" variant="outline" onClick={() => setShow(false)}>
                  {t("common.cancel")}
                </Button>
                <Button type="submit" disabled={mutation.isPending}>{t("common.save")}</Button>
              </div>
            </form>
          </CardContent>
        </Card>
      )}

      <Card>
        <CardContent className="p-0">
          {data && data.length === 0 && (
            <div className="p-6 text-muted-foreground">{t("common.empty")}</div>
          )}
          {data && data.length > 0 && (
            <table className="w-full text-sm">
              <thead className="bg-muted text-left">
                <tr>
                  <th className="px-4 py-2">{t("cheques.chequeNo")}</th>
                  <th className="px-4 py-2">{t("cheques.debtor")}</th>
                  <th className="px-4 py-2 text-right">{t("cheques.amount")}</th>
                  <th className="px-4 py-2">{t("cheques.dueDate")}</th>
                  <th className="px-4 py-2">{t("cheques.type")}</th>
                </tr>
              </thead>
              <tbody>
                {data.map((c) => (
                  <tr key={c.id} className="border-t">
                    <td className="px-4 py-2 font-mono">{c.chequeNo}</td>
                    <td className="px-4 py-2">{c.debtor}</td>
                    <td className="px-4 py-2 text-right">{c.amount.toLocaleString("tr-TR")}</td>
                    <td className="px-4 py-2">{c.dueDate}</td>
                    <td className="px-4 py-2">
                      {c.type === 1 ? t("cheques.received") : t("cheques.given")}
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
