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
import { createWarehouse, listWarehouses } from "./api";

const schema = z.object({
  code: z.string().min(1).max(25),
  name: z.string().min(1).max(50),
  address: z.string().max(250),
  city: z.string().max(25),
  telephone: z.string().max(25),
  description: z.string().max(250),
});
type FormValues = z.infer<typeof schema>;

export function WarehousesPage() {
  const { t } = useTranslation();
  const qc = useQueryClient();
  const [show, setShow] = useState(false);

  const { data, isLoading } = useQuery({
    queryKey: ["warehouses"],
    queryFn: listWarehouses,
  });

  const { register, handleSubmit, reset, formState: { errors } } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { code: "", name: "", address: "", city: "", telephone: "", description: "" },
  });

  const mutation = useMutation({
    mutationFn: createWarehouse,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["warehouses"] });
      reset();
      setShow(false);
    },
  });

  const apiError = extractApiError(mutation.error);

  return (
    <div className="container py-6 space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold">{t("inventory.warehouses.title")}</h1>
        <Button onClick={() => setShow((v) => !v)}>
          {show ? t("common.cancel") : t("inventory.warehouses.new")}
        </Button>
      </div>

      {show && (
        <Card>
          <CardHeader><CardTitle>{t("inventory.warehouses.new")}</CardTitle></CardHeader>
          <CardContent>
            <form
              className="grid grid-cols-1 md:grid-cols-2 gap-4"
              onSubmit={handleSubmit((v) => mutation.mutate(v))}
            >
              {([
                ["code", "inventory.warehouses.code"],
                ["name", "inventory.warehouses.name"],
                ["address", "inventory.warehouses.address"],
                ["city", "inventory.warehouses.city"],
                ["telephone", "inventory.warehouses.telephone"],
                ["description", "inventory.warehouses.description"],
              ] as const).map(([f, l]) => (
                <div key={f} className="space-y-1.5">
                  <Label htmlFor={f}>{t(l)}</Label>
                  <Input id={f} {...register(f)} />
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
                <Button type="submit" disabled={mutation.isPending}>{t("common.save")}</Button>
              </div>
            </form>
          </CardContent>
        </Card>
      )}

      <Card>
        <CardContent className="p-0">
          {isLoading && <div className="p-6">{t("common.loading")}</div>}
          {data && data.length === 0 && (
            <div className="p-6 text-muted-foreground">{t("common.empty")}</div>
          )}
          {data && data.length > 0 && (
            <table className="w-full text-sm">
              <thead className="bg-muted text-left">
                <tr>
                  <th className="px-4 py-2">{t("inventory.warehouses.code")}</th>
                  <th className="px-4 py-2">{t("inventory.warehouses.name")}</th>
                  <th className="px-4 py-2">{t("inventory.warehouses.city")}</th>
                  <th className="px-4 py-2">{t("inventory.warehouses.telephone")}</th>
                </tr>
              </thead>
              <tbody>
                {data.map((w) => (
                  <tr key={w.id} className="border-t">
                    <td className="px-4 py-2 font-mono">{w.code}</td>
                    <td className="px-4 py-2">{w.name}</td>
                    <td className="px-4 py-2">{w.city}</td>
                    <td className="px-4 py-2">{w.telephone}</td>
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
