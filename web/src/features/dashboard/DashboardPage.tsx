/*
 * Turquaz Resurrected — GPLv3
 */
import { useQuery } from "@tanstack/react-query";
import { useTranslation } from "react-i18next";
import { Link } from "react-router-dom";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { listBankCards } from "@/features/bank/api";
import { listBills } from "@/features/bills/api";
import { listCheques } from "@/features/cheques/api";
import { listCurrentCards } from "@/features/currentCards/api";
import { listInventoryCards, listWarehouses } from "@/features/inventory/api";

export function DashboardPage() {
  const { t } = useTranslation();

  const cur = useQuery({ queryKey: ["currentCards"], queryFn: listCurrentCards });
  const inv = useQuery({ queryKey: ["inventoryCards"], queryFn: listInventoryCards });
  const wh = useQuery({ queryKey: ["warehouses"], queryFn: listWarehouses });
  const bank = useQuery({ queryKey: ["bankCards"], queryFn: listBankCards });
  const bills = useQuery({ queryKey: ["bills"], queryFn: listBills });
  const cheques = useQuery({ queryKey: ["cheques", "all"], queryFn: () => listCheques() });

  const tiles = [
    { label: t("nav.currentCards"), count: cur.data?.length, to: "/current-cards" },
    { label: t("nav.inventoryCards"), count: inv.data?.length, to: "/inventory/cards" },
    { label: t("nav.warehouses"), count: wh.data?.length, to: "/inventory/warehouses" },
    { label: t("nav.bank"), count: bank.data?.length, to: "/bank/cards" },
    { label: t("nav.bills"), count: bills.data?.length, to: "/bills" },
    { label: t("nav.cheques"), count: cheques.data?.length, to: "/cheques" },
  ];

  return (
    <div className="container py-6 space-y-4">
      <div>
        <h1 className="text-2xl font-semibold">{t("nav.dashboard")}</h1>
        <p className="text-muted-foreground text-sm mt-1">
          {t("app.welcome")} — özet bilgileri aşağıda görebilir veya menüden modüllere geçebilirsiniz.
        </p>
      </div>
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {tiles.map((tile) => (
          <Link key={tile.to} to={tile.to}>
            <Card className="hover:bg-accent transition-colors cursor-pointer">
              <CardHeader>
                <CardTitle className="text-base text-muted-foreground">{tile.label}</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="text-3xl font-semibold">
                  {tile.count !== undefined ? tile.count : "—"}
                </div>
              </CardContent>
            </Card>
          </Link>
        ))}
      </div>
    </div>
  );
}
