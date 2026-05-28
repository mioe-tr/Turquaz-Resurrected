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
import { createOrder, deliverOrder, listOrders } from "./api";

const schema = z.object({
  type: z.coerce.number().int().min(1).max(2),
  documentNo: z.coerce.number().int().nonnegative(),
  orderDate: z.string(),
  dueDate: z.string(),
  deliverDate: z.string(),
  currentCardId: z.string().uuid(),
  billId: z.string().uuid(),
  definition: z.string().min(1).max(250),
  discountRatePercent: z.coerce.number().int().nonnegative(),
  vatPercent: z.coerce.number().int().nonnegative(),
  discountAmount: z.coerce.number().nonnegative(),
  charges: z.coerce.number().nonnegative(),
  vatAmount: z.coerce.number().nonnegative(),
  totalAmount: z.coerce.number().nonnegative(),
});
type FormValues = z.infer<typeof schema>;

export function OrdersPage() {
  const { t } = useTranslation();
  const qc = useQueryClient();
  const [show, setShow] = useState(false);

  const { data } = useQuery({ queryKey: ["orders"], queryFn: listOrders });
  const { register, handleSubmit, reset, formState: { errors } } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      type: 1, documentNo: 0,
      orderDate: "", dueDate: "", deliverDate: "",
      currentCardId: "", billId: "", definition: "",
      discountRatePercent: 0, vatPercent: 0,
      discountAmount: 0, charges: 0, vatAmount: 0, totalAmount: 0,
    },
  });

  const create = useMutation({
    mutationFn: createOrder,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["orders"] });
      reset();
      setShow(false);
      toast.success(t("orders.title") + " — " + t("common.created"));
    },
    onError: (e) => toast.apiError(e),
  });
  const deliver = useMutation({
    mutationFn: deliverOrder,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["orders"] });
      toast.success(t("common.deliver"));
    },
    onError: (e) => toast.apiError(e),
  });

  const apiError = extractApiError(create.error || deliver.error);

  return (
    <div className="container py-6 space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold">{t("orders.title")}</h1>
        <div className="flex gap-2">
          {data && data.length > 0 && (
            <Button
              variant="outline"
              onClick={() =>
                downloadExcel("siparisler", "Siparişler", [
                  { header: t("orders.docNo"), value: (o) => o.documentNo ?? 0 },
                  { header: t("orders.type"), value: (o) => (o.type === 1 ? t("bills.sales") : t("bills.purchase")) },
                  { header: t("orders.orderDate"), value: (o) => o.orderDate },
                  { header: t("orders.deliverDate"), value: (o) => o.deliverDate },
                  { header: t("orders.totalAmount"), value: (o) => o.totalAmount },
                  { header: t("orders.delivered"), value: (o) => (o.delivered ? "✓" : "") },
                ], data)
              }
            >
              {t("common.exportExcel")}
            </Button>
          )}
          <Button onClick={() => setShow((v) => !v)}>
            {show ? t("common.cancel") : t("orders.new")}
          </Button>
        </div>
      </div>

      {show && (
        <Card>
          <CardHeader><CardTitle>{t("orders.new")}</CardTitle></CardHeader>
          <CardContent>
            <form
              className="grid grid-cols-1 md:grid-cols-3 gap-3"
              onSubmit={handleSubmit((v) => create.mutate(v))}
            >
              <div className="space-y-1.5">
                <Label htmlFor="type">{t("orders.type")}</Label>
                <select
                  id="type" {...register("type")}
                  className="h-10 w-full rounded-md border border-input bg-background px-3 text-sm"
                >
                  <option value={1}>{t("bills.sales")}</option>
                  <option value={2}>{t("bills.purchase")}</option>
                </select>
              </div>
              {([
                ["documentNo", "orders.docNo", "number"],
                ["orderDate", "orders.orderDate", "date"],
                ["dueDate", "orders.dueDate", "date"],
                ["deliverDate", "orders.deliverDate", "date"],
                ["definition", "common.description", "text"],
                ["discountRatePercent", "orders.discount", "number"],
                ["vatPercent", "orders.vat", "number"],
                ["discountAmount", "orders.discountAmount", "number"],
                ["charges", "orders.charges", "number"],
                ["vatAmount", "orders.vatAmount", "number"],
                ["totalAmount", "orders.totalAmount", "number"],
              ] as const).map(([f, l, type]) => (
                <div key={f} className="space-y-1.5">
                  <Label htmlFor={f}>{t(l)}</Label>
                  <Input
                    id={f} type={type}
                    step={type === "number" ? "any" : undefined}
                    {...register(f)}
                  />
                  {errors[f] && <p className="text-sm text-destructive">{errors[f]?.message}</p>}
                </div>
              ))}
              {([
                ["currentCardId", "orders.currentCard"],
                ["billId", "orders.billId"],
              ] as const).map(([f, l]) => (
                <div key={f} className="space-y-1.5">
                  <Label htmlFor={f}>{t(l)}</Label>
                  <Input id={f} className="font-mono text-xs" {...register(f)} />
                  {errors[f] && <p className="text-sm text-destructive">{errors[f]?.message}</p>}
                </div>
              ))}

              {apiError && (
                <p className="text-sm text-destructive md:col-span-3">{apiError.message}</p>
              )}
              <div className="md:col-span-3 flex gap-2 justify-end">
                <Button type="button" variant="outline" onClick={() => setShow(false)}>
                  {t("common.cancel")}
                </Button>
                <Button type="submit" disabled={create.isPending}>{t("common.save")}</Button>
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
                  <th className="px-4 py-2">{t("orders.docNo")}</th>
                  <th className="px-4 py-2">{t("orders.type")}</th>
                  <th className="px-4 py-2">{t("orders.orderDate")}</th>
                  <th className="px-4 py-2">{t("orders.deliverDate")}</th>
                  <th className="px-4 py-2 text-right">{t("orders.totalAmount")}</th>
                  <th className="px-4 py-2">{t("orders.delivered")}</th>
                  <th className="px-4 py-2 text-right">{t("common.actions")}</th>
                </tr>
              </thead>
              <tbody>
                {data.map((o) => (
                  <tr key={o.id} className="border-t">
                    <td className="px-4 py-2 font-mono">{o.documentNo}</td>
                    <td className="px-4 py-2">
                      {o.type === 1 ? t("bills.sales") : t("bills.purchase")}
                    </td>
                    <td className="px-4 py-2">{o.orderDate}</td>
                    <td className="px-4 py-2">{o.deliverDate}</td>
                    <td className="px-4 py-2 text-right">{o.totalAmount.toLocaleString("tr-TR")}</td>
                    <td className="px-4 py-2">{o.delivered ? "✓" : "—"}</td>
                    <td className="px-4 py-2 text-right">
                      {!o.delivered && (
                        <Button
                          size="sm" variant="ghost"
                          onClick={() => deliver.mutate(o.id)} disabled={deliver.isPending}
                        >
                          {t("common.deliver")}
                        </Button>
                      )}
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
