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
import { createCurrentCard, listCurrentCards } from "./api";

const schema = z.object({
  code: z.string().min(1).max(25),
  name: z.string().min(1).max(250),
  definition: z.string().max(250),
  address: z.string().max(250),
  taxDepartment: z.string().max(50),
  taxNumber: z.string().max(50),
  creditLimit: z.coerce.number().nonnegative(),
  riskLimit: z.coerce.number().nonnegative(),
  discountRate: z.coerce.number().nonnegative(),
  discountPayment: z.coerce.number().nonnegative(),
  daysToValue: z.coerce.number().int().nonnegative(),
});
type FormValues = z.infer<typeof schema>;

export function CurrentCardsPage() {
  const { t } = useTranslation();
  const qc = useQueryClient();
  const [showForm, setShowForm] = useState(false);

  const { data, isLoading, isError } = useQuery({
    queryKey: ["currentCards"],
    queryFn: listCurrentCards,
  });

  const { register, handleSubmit, reset, formState: { errors } } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      code: "", name: "", definition: "", address: "",
      taxDepartment: "", taxNumber: "",
      creditLimit: 0, riskLimit: 0, discountRate: 0, discountPayment: 0,
      daysToValue: 0,
    },
  });

  const mutation = useMutation({
    mutationFn: createCurrentCard,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["currentCards"] });
      reset();
      setShowForm(false);
      toast.success(t("currentCards.title") + " — " + t("common.created"));
    },
    onError: (e) => toast.apiError(e),
  });

  const apiError = extractApiError(mutation.error);

  return (
    <div className="container py-6 space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold">{t("currentCards.title")}</h1>
        <div className="flex gap-2">
          {data && data.length > 0 && (
            <Button
              variant="outline"
              onClick={() =>
                downloadExcel(
                  "cari_kartlar",
                  "Cari Kartlar",
                  [
                    { header: t("currentCards.code"), value: (c) => c.code },
                    { header: t("currentCards.name"), value: (c) => c.name },
                    { header: t("currentCards.taxNumber"), value: (c) => c.taxNumber },
                    { header: t("currentCards.creditLimit"), value: (c) => c.creditLimit },
                    { header: t("currentCards.riskLimit"), value: (c) => c.riskLimit },
                  ],
                  data,
                )
              }
            >
              {t("common.exportExcel")}
            </Button>
          )}
          <Button onClick={() => setShowForm((v) => !v)}>
            {showForm ? t("currentCards.cancel") : t("currentCards.newCard")}
          </Button>
        </div>
      </div>

      {showForm && (
        <Card>
          <CardHeader>
            <CardTitle>{t("currentCards.newCard")}</CardTitle>
          </CardHeader>
          <CardContent>
            <form
              className="grid grid-cols-1 md:grid-cols-2 gap-4"
              onSubmit={handleSubmit((v) => mutation.mutate(v))}
            >
              {(
                [
                  ["code", "currentCards.code"],
                  ["name", "currentCards.name"],
                  ["definition", "currentCards.definition"],
                  ["address", "currentCards.address"],
                  ["taxDepartment", "currentCards.taxDepartment"],
                  ["taxNumber", "currentCards.taxNumber"],
                  ["creditLimit", "currentCards.creditLimit", "number"],
                  ["riskLimit", "currentCards.riskLimit", "number"],
                  ["discountRate", "currentCards.discountRate", "number"],
                  ["discountPayment", "currentCards.discountPayment", "number"],
                  ["daysToValue", "currentCards.daysToValue", "number"],
                ] as const
              ).map(([field, label, type]) => (
                <div key={field} className="space-y-1.5">
                  <Label htmlFor={field}>{t(label)}</Label>
                  <Input
                    id={field}
                    type={type ?? "text"}
                    step={type === "number" ? "any" : undefined}
                    {...register(field)}
                  />
                  {errors[field] && (
                    <p className="text-sm text-destructive">{errors[field]?.message}</p>
                  )}
                </div>
              ))}
              {apiError && (
                <p className="text-sm text-destructive md:col-span-2" role="alert">
                  {apiError.message}
                </p>
              )}
              <div className="md:col-span-2 flex gap-2 justify-end">
                <Button type="button" variant="outline" onClick={() => setShowForm(false)}>
                  {t("currentCards.cancel")}
                </Button>
                <Button type="submit" disabled={mutation.isPending}>
                  {t("currentCards.save")}
                </Button>
              </div>
            </form>
          </CardContent>
        </Card>
      )}

      <Card>
        <CardContent className="p-0">
          {isLoading && <div className="p-6">{t("common.loading")}</div>}
          {isError && <div className="p-6 text-destructive">{t("common.error")}</div>}
          {data && data.length === 0 && (
            <div className="p-6 text-muted-foreground">{t("common.empty")}</div>
          )}
          {data && data.length > 0 && (
            <table className="w-full text-sm">
              <thead className="bg-muted text-left">
                <tr>
                  <th className="px-4 py-2">{t("currentCards.code")}</th>
                  <th className="px-4 py-2">{t("currentCards.name")}</th>
                  <th className="px-4 py-2">{t("currentCards.taxNumber")}</th>
                  <th className="px-4 py-2 text-right">{t("currentCards.creditLimit")}</th>
                  <th className="px-4 py-2 text-right">{t("currentCards.riskLimit")}</th>
                </tr>
              </thead>
              <tbody>
                {data.map((c) => (
                  <tr key={c.id} className="border-t">
                    <td className="px-4 py-2 font-mono">{c.code}</td>
                    <td className="px-4 py-2">{c.name}</td>
                    <td className="px-4 py-2">{c.taxNumber}</td>
                    <td className="px-4 py-2 text-right">{c.creditLimit.toLocaleString("tr-TR")}</td>
                    <td className="px-4 py-2 text-right">{c.riskLimit.toLocaleString("tr-TR")}</td>
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
