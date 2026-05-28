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
import { createBankCard, listBankCards } from "./api";

const schema = z.object({
  code: z.string().min(1).max(100),
  bankName: z.string().min(1).max(50),
  branchName: z.string().min(1).max(50),
  accountNo: z.string().min(1).max(50),
  definition: z.string().max(250),
  currencyId: z.string().uuid(),
});
type FormValues = z.infer<typeof schema>;

export function BankCardsPage() {
  const { t } = useTranslation();
  const qc = useQueryClient();
  const [show, setShow] = useState(false);

  const { data } = useQuery({ queryKey: ["bankCards"], queryFn: listBankCards });
  const { register, handleSubmit, reset, formState: { errors } } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      code: "", bankName: "", branchName: "", accountNo: "",
      definition: "", currencyId: "",
    },
  });
  const mutation = useMutation({
    mutationFn: createBankCard,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["bankCards"] });
      reset();
      setShow(false);
    },
  });

  const apiError = extractApiError(mutation.error);

  return (
    <div className="container py-6 space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold">{t("bank.cards.title")}</h1>
        <Button onClick={() => setShow((v) => !v)}>
          {show ? t("common.cancel") : t("bank.cards.new")}
        </Button>
      </div>

      {show && (
        <Card>
          <CardHeader><CardTitle>{t("bank.cards.new")}</CardTitle></CardHeader>
          <CardContent>
            <form
              className="grid grid-cols-1 md:grid-cols-2 gap-4"
              onSubmit={handleSubmit((v) => mutation.mutate(v))}
            >
              {([
                ["code", "bank.cards.code"],
                ["bankName", "bank.cards.bankName"],
                ["branchName", "bank.cards.branchName"],
                ["accountNo", "bank.cards.accountNo"],
                ["definition", "bank.cards.definition"],
                ["currencyId", "bank.cards.currencyId"],
              ] as const).map(([f, l]) => (
                <div key={f} className="space-y-1.5">
                  <Label htmlFor={f}>{t(l)}</Label>
                  <Input
                    id={f}
                    className={f === "currencyId" ? "font-mono text-xs" : ""}
                    {...register(f)}
                  />
                  {errors[f] && <p className="text-sm text-destructive">{errors[f]?.message}</p>}
                </div>
              ))}
              {apiError && (
                <p className="text-sm text-destructive md:col-span-2">{apiError.message}</p>
              )}
              <div className="md:col-span-2 flex gap-2 justify-end">
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
                  <th className="px-4 py-2">{t("bank.cards.code")}</th>
                  <th className="px-4 py-2">{t("bank.cards.bankName")}</th>
                  <th className="px-4 py-2">{t("bank.cards.branchName")}</th>
                  <th className="px-4 py-2">{t("bank.cards.accountNo")}</th>
                </tr>
              </thead>
              <tbody>
                {data.map((c) => (
                  <tr key={c.id} className="border-t">
                    <td className="px-4 py-2 font-mono">{c.code}</td>
                    <td className="px-4 py-2">{c.bankName}</td>
                    <td className="px-4 py-2">{c.branchName}</td>
                    <td className="px-4 py-2">{c.accountNo}</td>
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
