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
import { createInventoryCard, listInventoryCards } from "./api";

const schema = z.object({
  code: z.string().min(1).max(25),
  name: z.string().min(1).max(250),
  definition: z.string().max(250),
  minimumAmount: z.coerce.number().int().nonnegative(),
  maximumAmount: z.coerce.number().int().nonnegative(),
  vatRate: z.coerce.number().int().nonnegative(),
  discountPercent: z.coerce.number().int().nonnegative(),
  specialVatRate: z.coerce.number().int().nonnegative(),
});
type FormValues = z.infer<typeof schema>;

export function InventoryCardsPage() {
  const { t } = useTranslation();
  const qc = useQueryClient();
  const [show, setShow] = useState(false);

  const { data, isLoading, isError } = useQuery({
    queryKey: ["inventoryCards"],
    queryFn: listInventoryCards,
  });

  const { register, handleSubmit, reset, formState: { errors } } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      code: "", name: "", definition: "",
      minimumAmount: 0, maximumAmount: 0,
      vatRate: 0, discountPercent: 0, specialVatRate: 0,
    },
  });

  const mutation = useMutation({
    mutationFn: createInventoryCard,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["inventoryCards"] });
      reset();
      setShow(false);
    },
  });

  const apiError = extractApiError(mutation.error);

  return (
    <div className="container py-6 space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold">{t("inventory.cards.title")}</h1>
        <Button onClick={() => setShow((v) => !v)}>
          {show ? t("common.cancel") : t("inventory.cards.new")}
        </Button>
      </div>

      {show && (
        <Card>
          <CardHeader><CardTitle>{t("inventory.cards.new")}</CardTitle></CardHeader>
          <CardContent>
            <form
              className="grid grid-cols-1 md:grid-cols-2 gap-4"
              onSubmit={handleSubmit((v) => mutation.mutate(v))}
            >
              {([
                ["code", "inventory.cards.code"],
                ["name", "inventory.cards.name"],
                ["definition", "inventory.cards.definition"],
                ["minimumAmount", "inventory.cards.min", "number"],
                ["maximumAmount", "inventory.cards.max", "number"],
                ["vatRate", "inventory.cards.vatRate", "number"],
                ["discountPercent", "inventory.cards.discount", "number"],
                ["specialVatRate", "inventory.cards.specialVat", "number"],
              ] as const).map(([f, l, type]) => (
                <div key={f} className="space-y-1.5">
                  <Label htmlFor={f}>{t(l)}</Label>
                  <Input id={f} type={type ?? "text"} {...register(f)} />
                  {errors[f] && <p className="text-sm text-destructive">{errors[f]?.message}</p>}
                </div>
              ))}
              {apiError && (
                <p className="text-sm text-destructive md:col-span-2" role="alert">
                  {apiError.message}
                </p>
              )}
              <div className="md:col-span-2 flex gap-2 justify-end">
                <Button type="button" variant="outline" onClick={() => setShow(false)}>
                  {t("common.cancel")}
                </Button>
                <Button type="submit" disabled={mutation.isPending}>
                  {t("common.save")}
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
                  <th className="px-4 py-2">{t("inventory.cards.code")}</th>
                  <th className="px-4 py-2">{t("inventory.cards.name")}</th>
                  <th className="px-4 py-2 text-right">{t("inventory.cards.min")}</th>
                  <th className="px-4 py-2 text-right">{t("inventory.cards.max")}</th>
                  <th className="px-4 py-2 text-right">{t("inventory.cards.vatRate")}</th>
                </tr>
              </thead>
              <tbody>
                {data.map((c) => (
                  <tr key={c.id} className="border-t">
                    <td className="px-4 py-2 font-mono">{c.code}</td>
                    <td className="px-4 py-2">{c.name}</td>
                    <td className="px-4 py-2 text-right">{c.minimumAmount}</td>
                    <td className="px-4 py-2 text-right">{c.maximumAmount}</td>
                    <td className="px-4 py-2 text-right">%{c.vatRate}</td>
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
