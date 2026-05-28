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
import { createConsignment, listConsignments, printConsignment } from "./api";

const schema = z.object({
  type: z.coerce.number().int().min(1).max(2),
  date: z.string(),
  documentNo: z.string().min(1).max(50),
  referenceBillNo: z.string().min(1).max(50),
  definition: z.string().min(1).max(250),
  currentCardId: z.string().uuid(),
  exchangeRateId: z.string().uuid(),
  engineSequenceId: z.string().uuid(),
});
type FormValues = z.infer<typeof schema>;

export function ConsignmentsPage() {
  const { t } = useTranslation();
  const qc = useQueryClient();
  const [show, setShow] = useState(false);

  const { data } = useQuery({ queryKey: ["consignments"], queryFn: listConsignments });

  const { register, handleSubmit, reset, formState: { errors } } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      type: 1, date: "",
      documentNo: "", referenceBillNo: "", definition: "",
      currentCardId: "", exchangeRateId: "", engineSequenceId: "",
    },
  });

  const create = useMutation({
    mutationFn: createConsignment,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["consignments"] });
      reset();
      setShow(false);
      toast.success(t("consignments.title") + " — " + t("common.created"));
    },
    onError: (e) => toast.apiError(e),
  });
  const print = useMutation({
    mutationFn: printConsignment,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["consignments"] });
      toast.success(t("common.print"));
    },
    onError: (e) => toast.apiError(e),
  });

  const apiError = extractApiError(create.error || print.error);

  return (
    <div className="container py-6 space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-semibold">{t("consignments.title")}</h1>
        <div className="flex gap-2">
          {data && data.length > 0 && (
            <Button
              variant="outline"
              onClick={() =>
                downloadExcel("konsinye", "Konsinye", [
                  { header: t("consignments.docNo"), value: (c) => c.documentNo },
                  { header: t("consignments.type"), value: (c) => (c.type === 1 ? t("consignments.out") : t("consignments.in")) },
                  { header: t("consignments.date"), value: (c) => c.date },
                  { header: t("consignments.referenceBillNo"), value: (c) => c.referenceBillNo },
                  { header: t("consignments.printed"), value: (c) => (c.printed ? "✓" : "") },
                ], data)
              }
            >
              {t("common.exportExcel")}
            </Button>
          )}
          <Button onClick={() => setShow((v) => !v)}>
            {show ? t("common.cancel") : t("consignments.new")}
          </Button>
        </div>
      </div>

      {show && (
        <Card>
          <CardHeader><CardTitle>{t("consignments.new")}</CardTitle></CardHeader>
          <CardContent>
            <form
              className="grid grid-cols-1 md:grid-cols-3 gap-3"
              onSubmit={handleSubmit((v) => create.mutate(v))}
            >
              <div className="space-y-1.5">
                <Label htmlFor="type">{t("consignments.type")}</Label>
                <select
                  id="type" {...register("type")}
                  className="h-10 w-full rounded-md border border-input bg-background px-3 text-sm"
                >
                  <option value={1}>{t("consignments.out")}</option>
                  <option value={2}>{t("consignments.in")}</option>
                </select>
              </div>
              <div className="space-y-1.5">
                <Label htmlFor="date">{t("consignments.date")}</Label>
                <Input id="date" type="date" {...register("date")} />
              </div>
              <div className="space-y-1.5">
                <Label htmlFor="documentNo">{t("consignments.docNo")}</Label>
                <Input id="documentNo" {...register("documentNo")} />
                {errors.documentNo && (
                  <p className="text-sm text-destructive">{errors.documentNo.message}</p>
                )}
              </div>
              <div className="space-y-1.5">
                <Label htmlFor="referenceBillNo">{t("consignments.referenceBillNo")}</Label>
                <Input id="referenceBillNo" {...register("referenceBillNo")} />
              </div>
              <div className="space-y-1.5 md:col-span-2">
                <Label htmlFor="definition">{t("consignments.definition")}</Label>
                <Input id="definition" {...register("definition")} />
              </div>
              {([
                ["currentCardId", "consignments.currentCard"],
                ["exchangeRateId", "consignments.exchangeRateId"],
                ["engineSequenceId", "consignments.sequenceId"],
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
                  <th className="px-4 py-2">{t("consignments.docNo")}</th>
                  <th className="px-4 py-2">{t("consignments.type")}</th>
                  <th className="px-4 py-2">{t("consignments.date")}</th>
                  <th className="px-4 py-2">{t("consignments.referenceBillNo")}</th>
                  <th className="px-4 py-2">{t("consignments.printed")}</th>
                  <th className="px-4 py-2 text-right">{t("common.actions")}</th>
                </tr>
              </thead>
              <tbody>
                {data.map((c) => (
                  <tr key={c.id} className="border-t">
                    <td className="px-4 py-2 font-mono">{c.documentNo}</td>
                    <td className="px-4 py-2">
                      {c.type === 1 ? t("consignments.out") : t("consignments.in")}
                    </td>
                    <td className="px-4 py-2">{c.date}</td>
                    <td className="px-4 py-2 font-mono">{c.referenceBillNo}</td>
                    <td className="px-4 py-2">{c.printed ? "✓" : "—"}</td>
                    <td className="px-4 py-2 text-right">
                      {!c.printed && (
                        <Button
                          size="sm" variant="ghost"
                          onClick={() => print.mutate(c.id)} disabled={print.isPending}
                        >
                          {t("common.print")}
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
