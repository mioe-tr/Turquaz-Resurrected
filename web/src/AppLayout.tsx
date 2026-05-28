/*
 * Turquaz Resurrected — GPLv3
 */
import { useTranslation } from "react-i18next";
import { NavLink, Outlet, useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import { useAuthStore } from "@/store/auth";

export function AppLayout() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const session = useAuthStore((s) => s.session);
  const clear = useAuthStore((s) => s.clear);

  const navItems = [
    { to: "/", label: t("nav.dashboard"), end: true },
    { to: "/current-cards", label: t("nav.currentCards") },
  ];

  const linkClass = ({ isActive }: { isActive: boolean }) =>
    cn(
      "px-3 py-2 rounded-md text-sm font-medium transition-colors",
      isActive
        ? "bg-primary text-primary-foreground"
        : "text-muted-foreground hover:bg-secondary hover:text-foreground",
    );

  return (
    <div className="min-h-screen flex flex-col">
      <header className="border-b">
        <div className="container flex h-14 items-center justify-between gap-4">
          <div className="font-bold text-lg">{t("app.title")}</div>
          <nav className="flex gap-1 flex-1 ml-4">
            {navItems.map((item) => (
              <NavLink key={item.to} to={item.to} end={item.end} className={linkClass}>
                {item.label}
              </NavLink>
            ))}
          </nav>
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
      <main className="flex-1">
        <Outlet />
      </main>
    </div>
  );
}
