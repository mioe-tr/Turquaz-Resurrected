/*
 * Turquaz Resurrected — GPLv3
 */
import { useTranslation } from "react-i18next";
import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import { useAuthStore } from "@/store/auth";

interface NavGroup {
  label: string;
  items: { to: string; label: string; end?: boolean }[];
}

export function AppLayout() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const session = useAuthStore((s) => s.session);
  const clear = useAuthStore((s) => s.clear);

  const groups: NavGroup[] = [
    { label: "", items: [{ to: "/", label: t("nav.dashboard"), end: true }] },
    { label: t("nav.currentCards"), items: [{ to: "/current-cards", label: t("nav.currentCards") }] },
    {
      label: t("nav.inventory"),
      items: [
        { to: "/inventory/cards", label: t("nav.inventoryCards") },
        { to: "/inventory/warehouses", label: t("nav.warehouses") },
        { to: "/inventory/stock", label: t("nav.stockOnHand") },
      ],
    },
    {
      label: t("nav.accounting"),
      items: [
        { to: "/accounting/journal", label: t("nav.journal") },
        { to: "/accounting/trial-balance", label: t("nav.trialBalance") },
        { to: "/accounting/account-balance", label: t("nav.accountBalance") },
      ],
    },
    { label: t("nav.bank"), items: [{ to: "/bank/cards", label: t("nav.bank") }] },
    { label: t("nav.cash"), items: [{ to: "/cash/cards", label: t("nav.cash") }] },
    { label: t("nav.cheques"), items: [{ to: "/cheques", label: t("nav.cheques") }] },
    {
      label: t("nav.bills"),
      items: [
        { to: "/bills", label: t("nav.bills") },
        { to: "/orders", label: t("nav.orders") },
        { to: "/consignments", label: t("nav.consignments") },
      ],
    },
    { label: t("nav.reports"), items: [{ to: "/reports/inventory-profit", label: t("nav.inventoryProfit") }] },
  ];

  const linkClass = ({ isActive }: { isActive: boolean }) =>
    cn(
      "block px-3 py-1.5 rounded-md text-sm transition-colors",
      isActive
        ? "bg-primary text-primary-foreground font-medium"
        : "text-muted-foreground hover:bg-secondary hover:text-foreground",
    );

  return (
    <div className="min-h-screen flex flex-col">
      <header className="border-b">
        <div className="container flex h-14 items-center justify-between gap-4">
          <div className="font-bold text-lg">{t("app.title")}</div>
          <div className="flex items-center gap-3 text-sm">
            <span className="text-muted-foreground">
              {t("auth.loggedInAs", { name: session?.username ?? "?" })}
            </span>
            <Button
              variant="outline"
              size="sm"
              onClick={() => {
                clear();
                navigate("/login", { replace: true });
              }}
            >
              {t("nav.logout")}
            </Button>
          </div>
        </div>
      </header>
      <div className="flex-1 flex">
        <aside className="w-56 border-r p-3 space-y-4 overflow-auto">
          {groups.map((group, gi) => (
            <div key={gi}>
              {group.label && (
                <div className="px-3 py-1 text-xs uppercase tracking-wider text-muted-foreground">
                  {group.label}
                </div>
              )}
              <nav className="space-y-0.5">
                {group.items.map((item) => (
                  <NavLink key={item.to} to={item.to} end={item.end} className={linkClass}>
                    {item.label}
                  </NavLink>
                ))}
              </nav>
            </div>
          ))}
        </aside>
        <main className="flex-1 min-w-0">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
