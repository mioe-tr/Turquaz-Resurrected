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
import { createCashCard, listCashCards } from "./api";

const schema = z.object({
  name: z.string().min(1).max(250),
  definition: z.string().max(250),
  accountingAccountsId: z.string().uuid(),
});
type FormValues = z.infer<typeof schema>;

export function CashCardsPage() {
  const { t } = useTranslation();
  const qc = useQueryClient();
  const [show, setShow] = useState(false);

  const { data } = useQuery({ queryKey: ["cashCards"], queryFn: listCashCards });
  const { register, handleSubmit, reset, formState: { errors } } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { name: "", definition: "", accountingAccountsId: "" },
  });
  const mutation = useMutation({
    mutationFn: createCashCard,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["cashCards"] });
      reset();
      setShow(false);
    },
  });

  const apiError = extractApiError(mutation.error);

  return (
    <div className="container py-6 space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold">{t("cash.cards.title")}</h1>
        <Button onClick={() => setShow((v) => !v)}>
          {show ? t("common.cancel") : t("cash.cards.new")}
        </Button>
      </div>

      {show && (
        <Card>
          <CardHeader><CardTitle>{t("cash.cards.new")}</CardTitle></CardHeader>
          <CardContent>
            <form
              className="grid grid-cols-1 md:grid-cols-2 gap-4"
              onSubmit={handleSubmit((v) => mutation.mutate(v))}
            >
              <div className="space-y-1.5">
                <Label htmlFor="name">{t("cash.cards.name")}</Label>
                <Input id="name" {...register("name")} />
                {errors.name && <p className="text-sm text-destructive">{errors.name.message}</p>}
              </div>
              <div className="space-y-1.5">
                <Label htmlFor="definition">{t("cash.cards.definition")}</Label>
                <Input id="definition" {...register("definition")} />
              </div>
              <div className="space-y-1.5 md:col-span-2">
                <Label htmlFor="accountingAccountsId">{t("cash.cards.accountingAccount")}</Label>
                <Input
                  id="accountingAccountsId"
                  className="font-mono text-xs"
                  {...register("accountingAccountsId")}
                />
                {errors.accountingAccountsId && (
                  <p className="text-sm text-destructive">{errors.accountingAccountsId.message}</p>
                )}
              </div>
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
                  <th className="px-4 py-2">{t("cash.cards.name")}</th>
                  <th className="px-4 py-2">{t("cash.cards.definition")}</th>
                  <th className="px-4 py-2">{t("cash.cards.accountingAccount")}</th>
                </tr>
              </thead>
              <tbody>
                {data.map((c) => (
                  <tr key={c.id} className="border-t">
                    <td className="px-4 py-2">{c.name}</td>
                    <td className="px-4 py-2">{c.definition}</td>
                    <td className="px-4 py-2 font-mono text-xs">{c.accountingAccountsId}</td>
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
