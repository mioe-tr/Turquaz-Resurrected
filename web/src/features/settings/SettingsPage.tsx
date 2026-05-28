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
import { cn } from "@/lib/utils";
import { downloadExcel } from "@/lib/excelExport";
import { toast } from "@/lib/toast";
import {
  changePassword,
  createCurrency,
  createExchangeRate,
  getCompany,
  listCurrencies,
  listExchangeRates,
  listUsers,
  updateCompany,
  type Currency,
  type ExchangeRate,
} from "./api";

type Tab = "profile" | "company" | "users" | "currencies" | "rates" | "password";

export function SettingsPage() {
  const { t } = useTranslation();
  const [tab, setTab] = useState<Tab>("profile");

  const tabs: { id: Tab; label: string }[] = [
    { id: "profile", label: t("settings.profile") },
    { id: "company", label: t("settings.company") },
    { id: "users", label: t("settings.users") },
    { id: "currencies", label: t("settings.currencies") },
    { id: "rates", label: t("settings.rates") },
    { id: "password", label: t("settings.password") },
  ];

  return (
    <div className="container py-6 space-y-4">
      <h1 className="text-2xl font-semibold">{t("settings.title")}</h1>
      <div className="border-b">
        <nav className="flex gap-1">
          {tabs.map((b) => (
            <button
              key={b.id}
              type="button"
              onClick={() => setTab(b.id)}
              className={cn(
                "px-4 py-2 text-sm font-medium transition-colors border-b-2 -mb-px",
                tab === b.id
                  ? "border-primary text-primary"
                  : "border-transparent text-muted-foreground hover:text-foreground",
              )}
            >
              {b.label}
            </button>
          ))}
        </nav>
      </div>
      {tab === "profile" && <ProfileTab />}
      {tab === "company" && <CompanyTab />}
      {tab === "users" && <UsersTab />}
      {tab === "currencies" && <CurrenciesTab />}
      {tab === "rates" && <ExchangeRatesTab />}
      {tab === "password" && <PasswordTab />}
    </div>
  );
}

// ----- Profil -----

function ProfileTab() {
  const { t } = useTranslation();
  return (
    <Card>
      <CardHeader>
        <CardTitle>{t("settings.profile")}</CardTitle>
      </CardHeader>
      <CardContent>
        <p className="text-sm text-muted-foreground">
          {t("settings.profileNote")}
        </p>
      </CardContent>
    </Card>
  );
}

// ----- Şirket -----

const companySchema = z.object({
  name: z.string().min(1).max(250),
  address: z.string().max(250),
  telephone: z.string().max(100),
  fax: z.string().max(100),
});

function CompanyTab() {
  const { t } = useTranslation();
  const qc = useQueryClient();
  const q = useQuery({ queryKey: ["company"], queryFn: getCompany });
  const { register, handleSubmit, reset, formState: { errors } } = useForm<z.infer<typeof companySchema>>({
    resolver: zodResolver(companySchema),
    values: q.data
      ? {
          name: q.data.name ?? "",
          address: q.data.address ?? "",
          telephone: q.data.telephone ?? "",
          fax: q.data.fax ?? "",
        }
      : undefined,
  });
  const mutation = useMutation({
    mutationFn: updateCompany,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["company"] });
      toast.success(t("settings.saved"));
    },
    onError: (e) => toast.apiError(e),
  });
  const apiError = extractApiError(mutation.error);

  return (
    <Card>
      <CardHeader>
        <CardTitle>{t("settings.company")}</CardTitle>
      </CardHeader>
      <CardContent>
        <form
          className="grid grid-cols-1 md:grid-cols-2 gap-4"
          onSubmit={handleSubmit((v) => mutation.mutate(v))}
        >
          {(
            [
              ["name", "settings.companyName"],
              ["address", "settings.companyAddress"],
              ["telephone", "settings.companyTelephone"],
              ["fax", "settings.companyFax"],
            ] as const
          ).map(([f, l]) => (
            <div key={f} className="space-y-1.5">
              <Label htmlFor={f}>{t(l)}</Label>
              <Input id={f} {...register(f)} />
              {errors[f] && <p className="text-sm text-destructive">{errors[f]?.message}</p>}
            </div>
          ))}
          {mutation.isSuccess && (
            <p className="text-sm text-primary md:col-span-2">✓ {t("settings.saved")}</p>
          )}
          {apiError && (
            <p className="text-sm text-destructive md:col-span-2">{apiError.message}</p>
          )}
          <div className="md:col-span-2 flex justify-end gap-2">
            <Button type="button" variant="outline" onClick={() => reset()}>
              {t("common.cancel")}
            </Button>
            <Button type="submit" disabled={mutation.isPending}>
              {t("common.save")}
            </Button>
          </div>
        </form>
      </CardContent>
    </Card>
  );
}

// ----- Kullanıcılar -----

function UsersTab() {
  const { t } = useTranslation();
  const { data, isLoading } = useQuery({ queryKey: ["users"], queryFn: listUsers });

  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between">
        <CardTitle>{t("settings.users")}</CardTitle>
        {data && (
          <Button
            variant="outline"
            size="sm"
            onClick={() =>
              downloadExcel(
                "kullanicilar",
                "Kullanıcılar",
                [
                  { header: t("settings.username"), value: (u) => u.username },
                  { header: t("settings.realName"), value: (u) => u.realName },
                  { header: t("common.description"), value: (u) => u.description },
                ],
                data,
              )
            }
          >
            {t("common.exportExcel")}
          </Button>
        )}
      </CardHeader>
      <CardContent className="p-0">
        {isLoading && <div className="p-6">{t("common.loading")}</div>}
        {data && data.length === 0 && (
          <div className="p-6 text-muted-foreground">{t("common.empty")}</div>
        )}
        {data && data.length > 0 && (
          <table className="w-full text-sm">
            <thead className="bg-muted text-left">
              <tr>
                <th className="px-4 py-2">{t("settings.username")}</th>
                <th className="px-4 py-2">{t("settings.realName")}</th>
                <th className="px-4 py-2">{t("common.description")}</th>
              </tr>
            </thead>
            <tbody>
              {data.map((u) => (
                <tr key={u.id} className="border-t">
                  <td className="px-4 py-2 font-mono">{u.username}</td>
                  <td className="px-4 py-2">{u.realName}</td>
                  <td className="px-4 py-2">{u.description}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </CardContent>
    </Card>
  );
}

// ----- Para Birimleri -----

const currencySchema = z.object({
  name: z.string().min(1).max(100),
  abbreviation: z.string().min(1).max(10),
  country: z.string().min(1).max(100),
  defaultCurrency: z.boolean().optional(),
  constant: z.boolean().optional(),
});

function CurrenciesTab() {
  const { t } = useTranslation();
  const qc = useQueryClient();
  const q = useQuery({ queryKey: ["currencies"], queryFn: listCurrencies });
  const { register, handleSubmit, reset, formState: { errors } } = useForm<z.infer<typeof currencySchema>>({
    resolver: zodResolver(currencySchema),
    defaultValues: {
      name: "", abbreviation: "", country: "",
      defaultCurrency: false, constant: false,
    },
  });
  const mutation = useMutation({
    mutationFn: createCurrency,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["currencies"] });
      reset();
      toast.success(t("settings.currencies") + " — " + t("common.created"));
    },
    onError: (e) => toast.apiError(e),
  });
  const apiError = extractApiError(mutation.error);

  return (
    <div className="space-y-4">
      <Card>
        <CardHeader>
          <CardTitle>{t("settings.newCurrency")}</CardTitle>
        </CardHeader>
        <CardContent>
          <form
            className="grid grid-cols-1 md:grid-cols-3 gap-4"
            onSubmit={handleSubmit((v) => mutation.mutate(v))}
          >
            {(
              [
                ["name", "settings.currencyName"],
                ["abbreviation", "settings.currencyAbbr"],
                ["country", "settings.currencyCountry"],
              ] as const
            ).map(([f, l]) => (
              <div key={f} className="space-y-1.5">
                <Label htmlFor={f}>{t(l)}</Label>
                <Input id={f} {...register(f)} />
                {errors[f] && <p className="text-sm text-destructive">{errors[f]?.message}</p>}
              </div>
            ))}
            <label className="flex items-center gap-2 text-sm">
              <input type="checkbox" {...register("defaultCurrency")} />
              {t("settings.isDefault")}
            </label>
            <label className="flex items-center gap-2 text-sm">
              <input type="checkbox" {...register("constant")} />
              {t("settings.isConstant")}
            </label>
            <div className="flex justify-end">
              <Button type="submit" disabled={mutation.isPending}>
                {t("common.save")}
              </Button>
            </div>
            {apiError && (
              <p className="text-sm text-destructive md:col-span-3">{apiError.message}</p>
            )}
          </form>
        </CardContent>
      </Card>

      <Card>
        <CardHeader className="flex flex-row items-center justify-between">
          <CardTitle>{t("settings.currencies")}</CardTitle>
          {q.data && (
            <Button
              variant="outline"
              size="sm"
              onClick={() =>
                downloadExcel<Currency>(
                  "para_birimleri",
                  "Para Birimleri",
                  [
                    { header: t("settings.currencyName"), value: (c) => c.name },
                    { header: t("settings.currencyAbbr"), value: (c) => c.abbreviation },
                    { header: t("settings.currencyCountry"), value: (c) => c.country },
                    {
                      header: t("settings.isDefault"),
                      value: (c) => (c.defaultCurrency ? "✓" : ""),
                    },
                    {
                      header: t("settings.isConstant"),
                      value: (c) => (c.constant ? "✓" : ""),
                    },
                  ],
                  q.data,
                )
              }
            >
              {t("common.exportExcel")}
            </Button>
          )}
        </CardHeader>
        <CardContent className="p-0">
          {q.data && q.data.length === 0 && (
            <div className="p-6 text-muted-foreground">{t("common.empty")}</div>
          )}
          {q.data && q.data.length > 0 && (
            <table className="w-full text-sm">
              <thead className="bg-muted text-left">
                <tr>
                  <th className="px-4 py-2">{t("settings.currencyName")}</th>
                  <th className="px-4 py-2">{t("settings.currencyAbbr")}</th>
                  <th className="px-4 py-2">{t("settings.currencyCountry")}</th>
                  <th className="px-4 py-2">{t("settings.isDefault")}</th>
                  <th className="px-4 py-2">{t("settings.isConstant")}</th>
                </tr>
              </thead>
              <tbody>
                {q.data.map((c) => (
                  <tr key={c.id} className="border-t">
                    <td className="px-4 py-2">{c.name}</td>
                    <td className="px-4 py-2 font-mono">{c.abbreviation}</td>
                    <td className="px-4 py-2">{c.country}</td>
                    <td className="px-4 py-2">{c.defaultCurrency ? "✓" : ""}</td>
                    <td className="px-4 py-2">{c.constant ? "✓" : ""}</td>
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

// ----- Döviz Kurları -----

const rateSchema = z.object({
  baseCurrencyId: z.string().uuid(),
  exchangeCurrencyId: z.string().uuid(),
  exchangeRatio: z.coerce.number().positive(),
  date: z.string(),
});

function ExchangeRatesTab() {
  const { t } = useTranslation();
  const qc = useQueryClient();
  const q = useQuery({ queryKey: ["exchangeRates"], queryFn: listExchangeRates });
  const { register, handleSubmit, reset, formState: { errors } } = useForm<z.infer<typeof rateSchema>>({
    resolver: zodResolver(rateSchema),
    defaultValues: {
      baseCurrencyId: "", exchangeCurrencyId: "",
      exchangeRatio: 1, date: new Date().toISOString().slice(0, 10),
    },
  });
  const mutation = useMutation({
    mutationFn: createExchangeRate,
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ["exchangeRates"] });
      reset();
      toast.success(t("settings.rates") + " — " + t("common.created"));
    },
    onError: (e) => toast.apiError(e),
  });
  const apiError = extractApiError(mutation.error);

  return (
    <div className="space-y-4">
      <Card>
        <CardHeader>
          <CardTitle>{t("settings.newRate")}</CardTitle>
        </CardHeader>
        <CardContent>
          <form
            className="grid grid-cols-1 md:grid-cols-4 gap-4"
            onSubmit={handleSubmit((v) => mutation.mutate(v))}
          >
            {(
              [
                ["baseCurrencyId", "settings.baseCurrency"],
                ["exchangeCurrencyId", "settings.targetCurrency"],
              ] as const
            ).map(([f, l]) => (
              <div key={f} className="space-y-1.5">
                <Label htmlFor={f}>{t(l)}</Label>
                <Input id={f} className="font-mono text-xs" {...register(f)} />
                {errors[f] && <p className="text-sm text-destructive">{errors[f]?.message}</p>}
              </div>
            ))}
            <div className="space-y-1.5">
              <Label htmlFor="exchangeRatio">{t("settings.rate")}</Label>
              <Input id="exchangeRatio" type="number" step="0.0001" {...register("exchangeRatio")} />
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="date">{t("common.date")}</Label>
              <Input id="date" type="date" {...register("date")} />
            </div>
            <div className="md:col-span-4 flex justify-end">
              <Button type="submit" disabled={mutation.isPending}>
                {t("common.save")}
              </Button>
            </div>
            {apiError && (
              <p className="text-sm text-destructive md:col-span-4">{apiError.message}</p>
            )}
          </form>
        </CardContent>
      </Card>

      <Card>
        <CardHeader className="flex flex-row items-center justify-between">
          <CardTitle>{t("settings.rates")}</CardTitle>
          {q.data && (
            <Button
              variant="outline"
              size="sm"
              onClick={() =>
                downloadExcel<ExchangeRate>(
                  "doviz_kurlari",
                  "Döviz Kurları",
                  [
                    { header: t("common.date"), value: (r) => r.date },
                    { header: t("settings.baseCurrency"), value: (r) => r.baseCurrencyId },
                    { header: t("settings.targetCurrency"), value: (r) => r.exchangeCurrencyId },
                    { header: t("settings.rate"), value: (r) => r.exchangeRatio },
                  ],
                  q.data,
                )
              }
            >
              {t("common.exportExcel")}
            </Button>
          )}
        </CardHeader>
        <CardContent className="p-0">
          {q.data && q.data.length === 0 && (
            <div className="p-6 text-muted-foreground">{t("common.empty")}</div>
          )}
          {q.data && q.data.length > 0 && (
            <table className="w-full text-sm">
              <thead className="bg-muted text-left">
                <tr>
                  <th className="px-4 py-2">{t("common.date")}</th>
                  <th className="px-4 py-2">{t("settings.baseCurrency")}</th>
                  <th className="px-4 py-2">{t("settings.targetCurrency")}</th>
                  <th className="px-4 py-2 text-right">{t("settings.rate")}</th>
                </tr>
              </thead>
              <tbody>
                {q.data.map((r) => (
                  <tr key={r.id} className="border-t">
                    <td className="px-4 py-2">{r.date}</td>
                    <td className="px-4 py-2 font-mono text-xs">{r.baseCurrencyId}</td>
                    <td className="px-4 py-2 font-mono text-xs">{r.exchangeCurrencyId}</td>
                    <td className="px-4 py-2 text-right">{r.exchangeRatio}</td>
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

// ----- Parola Değiştirme -----

const passwordSchema = z
  .object({
    currentPassword: z.string().min(1),
    newPassword: z.string().min(8, "En az 8 karakter olmalı").max(100),
    confirmPassword: z.string().min(1),
  })
  .refine((data) => data.newPassword === data.confirmPassword, {
    message: "Yeni parola tekrarı eşleşmiyor",
    path: ["confirmPassword"],
  });

function PasswordTab() {
  const { t } = useTranslation();
  const { register, handleSubmit, reset, formState: { errors } } = useForm<z.infer<typeof passwordSchema>>({
    resolver: zodResolver(passwordSchema),
    defaultValues: { currentPassword: "", newPassword: "", confirmPassword: "" },
  });
  const mutation = useMutation({
    mutationFn: ({ currentPassword, newPassword }: { currentPassword: string; newPassword: string }) =>
      changePassword(currentPassword, newPassword),
    onSuccess: () => {
      reset();
      toast.success(t("settings.passwordChanged"));
    },
    onError: (e) => toast.apiError(e),
  });
  const apiError = extractApiError(mutation.error);

  return (
    <Card>
      <CardHeader>
        <CardTitle>{t("settings.password")}</CardTitle>
      </CardHeader>
      <CardContent>
        <form
          className="space-y-4 max-w-md"
          onSubmit={handleSubmit((v) =>
            mutation.mutate({ currentPassword: v.currentPassword, newPassword: v.newPassword }),
          )}
        >
          <div className="space-y-1.5">
            <Label htmlFor="currentPassword">{t("settings.currentPassword")}</Label>
            <Input id="currentPassword" type="password" {...register("currentPassword")} />
            {errors.currentPassword && (
              <p className="text-sm text-destructive">{errors.currentPassword.message}</p>
            )}
          </div>
          <div className="space-y-1.5">
            <Label htmlFor="newPassword">{t("settings.newPassword")}</Label>
            <Input id="newPassword" type="password" {...register("newPassword")} />
            {errors.newPassword && (
              <p className="text-sm text-destructive">{errors.newPassword.message}</p>
            )}
          </div>
          <div className="space-y-1.5">
            <Label htmlFor="confirmPassword">{t("settings.confirmPassword")}</Label>
            <Input id="confirmPassword" type="password" {...register("confirmPassword")} />
            {errors.confirmPassword && (
              <p className="text-sm text-destructive">{errors.confirmPassword.message}</p>
            )}
          </div>
          {mutation.isSuccess && (
            <p className="text-sm text-primary">✓ {t("settings.passwordChanged")}</p>
          )}
          {apiError && <p className="text-sm text-destructive">{apiError.message}</p>}
          <div className="flex justify-end">
            <Button type="submit" disabled={mutation.isPending}>
              {t("settings.changePassword")}
            </Button>
          </div>
        </form>
      </CardContent>
    </Card>
  );
}
