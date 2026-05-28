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
import { closeBill, createBill, listBills, printBill } from "./api";

const schema = z.object({
  type: z.coerce.number().int().min(1).max(2),
  billDate: z.string(),
  dueDate: z.string(),
  documentNo: z.string().min(1).max(50),
  definition: z.string().min(1).max(250),
  currentCardId: z.string().uuid(),
  exchangeRateId: z.string().uuid(),
  engineSequenceId: z.string().uuid(),
});
type FormValues = z.infer<typeof schema>;

export function BillsPage() {
  const { t } = useTranslation();
  const qc = useQueryClient();
  const [show, setShow] = useState(false);

  const { data } = useQuery({ queryKey: ["bills"], queryFn: listBills });

  const { register, handleSubmit, reset, formState: { errors } } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      type: 1, billDate: "", dueDate: "",
      documentNo: "", definition: "",
      currentCardId: "", exchangeRateId: "", engineSequenceId: "",
    },
  });

  const create = useMutation({
    mutationFn: createBill,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["bills"] });
      reset();
      setShow(false);
      toast.success(t("bills.title") + " — " + t("common.created"));
    },
    onError: (e) => toast.apiError(e),
  });
  const print = useMutation({
    mutationFn: printBill,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["bills"] });
      toast.success(t("common.print"));
    },
    onError: (e) => toast.apiError(e),
  });
  const close = useMutation({
    mutationFn: closeBill,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["bills"] });
      toast.success(t("common.close"));
    },
    onError: (e) => toast.apiError(e),
  });

  const apiError = extractApiError(create.error || print.error || close.error);

  return (
    <div className="container py-6 space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold">{t("bills.title")}</h1>
        <div className="flex gap-2">
          {data && data.length > 0 && (
            <Button
              variant="outline"
              onClick={() =>
                downloadExcel("faturalar", "Faturalar", [
                  { header: t("bills.docNo"), value: (b) => b.documentNo },
                  { header: t("bills.type"), value: (b) => (b.type === 1 ? t("bills.sales") : t("bills.purchase")) },
                  { header: t("bills.billDate"), value: (b) => b.billDate },
                  { header: t("bills.dueDate"), value: (b) => b.dueDate },
                  { header: t("bills.printed"), value: (b) => (b.printed ? "✓" : "") },
                  { header: t("bills.open"), value: (b) => (b.open ? "✓" : "") },
                ], data)
              }
            >
              {t("common.exportExcel")}
            </Button>
          )}
          <Button onClick={() => setShow((v) => !v)}>
            {show ? t("common.cancel") : t("bills.new")}
          </Button>
        </div>
      </div>

      {show && (
        <Card>
          <CardHeader><CardTitle>{t("bills.new")}</CardTitle></CardHeader>
          <CardContent>
            <form
              className="grid grid-cols-1 md:grid-cols-3 gap-3"
              onSubmit={handleSubmit((v) => create.mutate(v))}
            >
              <div className="space-y-1.5">
                <Label htmlFor="type">{t("bills.type")}</Label>
                <select
                  id="type"
                  {...register("type")}
                  className="h-10 w-full rounded-md border border-input bg-background px-3 text-sm"
                >
                  <option value={1}>{t("bills.sales")}</option>
                  <option value={2}>{t("bills.purchase")}</option>
                </select>
              </div>
              <div className="space-y-1.5">
                <Label htmlFor="billDate">{t("bills.billDate")}</Label>
                <Input id="billDate" type="date" {...register("billDate")} />
              </div>
              <div className="space-y-1.5">
                <Label htmlFor="dueDate">{t("bills.dueDate")}</Label>
                <Input id="dueDate" type="date" {...register("dueDate")} />
              </div>
              <div className="space-y-1.5">
                <Label htmlFor="documentNo">{t("bills.docNo")}</Label>
                <Input id="documentNo" {...register("documentNo")} />
                {errors.documentNo && (
                  <p className="text-sm text-destructive">{errors.documentNo.message}</p>
                )}
              </div>
              <div className="space-y-1.5 md:col-span-2">
                <Label htmlFor="definition">{t("bills.definition")}</Label>
                <Input id="definition" {...register("definition")} />
              </div>
              {([
                ["currentCardId", "bills.currentCard"],
                ["exchangeRateId", "bills.exchangeRateId"],
                ["engineSequenceId", "bills.sequenceId"],
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
                  <th className="px-4 py-2">{t("bills.docNo")}</th>
                  <th className="px-4 py-2">{t("bills.type")}</th>
                  <th className="px-4 py-2">{t("bills.billDate")}</th>
                  <th className="px-4 py-2">{t("bills.dueDate")}</th>
                  <th className="px-4 py-2">{t("bills.printed")}</th>
                  <th className="px-4 py-2">{t("bills.open")}</th>
                  <th className="px-4 py-2 text-right">{t("common.actions")}</th>
                </tr>
              </thead>
              <tbody>
                {data.map((b) => (
                  <tr key={b.id} className="border-t">
                    <td className="px-4 py-2 font-mono">{b.documentNo}</td>
                    <td className="px-4 py-2">
                      {b.type === 1 ? t("bills.sales") : t("bills.purchase")}
                    </td>
                    <td className="px-4 py-2">{b.billDate}</td>
                    <td className="px-4 py-2">{b.dueDate}</td>
                    <td className="px-4 py-2">{b.printed ? "✓" : "—"}</td>
                    <td className="px-4 py-2">{b.open ? "✓" : "—"}</td>
                    <td className="px-4 py-2 text-right">
                      {!b.printed && (
                        <Button
                          size="sm" variant="ghost"
                          onClick={() => print.mutate(b.id)} disabled={print.isPending}
                        >
                          {t("common.print")}
                        </Button>
                      )}
                      {b.open && (
                        <Button
                          size="sm" variant="ghost"
                          onClick={() => close.mutate(b.id)} disabled={close.isPending}
                        >
                          {t("common.close")}
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
