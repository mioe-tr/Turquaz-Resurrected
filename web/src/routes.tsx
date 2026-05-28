/*
 * Turquaz Resurrected — GPLv3
 */
import type { JSX } from "react";
import { Navigate, Route, Routes, useLocation } from "react-router-dom";
import { AppLayout } from "@/AppLayout";
import { AccountBalancePage } from "@/features/accounting/AccountBalancePage";
import { JournalEntryPage } from "@/features/accounting/JournalEntryPage";
import { TrialBalancePage } from "@/features/accounting/TrialBalancePage";
import { LoginPage } from "@/features/auth/LoginPage";
import { BankCardsPage } from "@/features/bank/BankCardsPage";
import { BillsPage } from "@/features/bills/BillsPage";
import { ConsignmentsPage } from "@/features/bills/ConsignmentsPage";
import { OrdersPage } from "@/features/bills/OrdersPage";
import { CashCardsPage } from "@/features/cash/CashCardsPage";
import { ChequesPage } from "@/features/cheques/ChequesPage";
import { CurrentCardsPage } from "@/features/currentCards/CurrentCardsPage";
import { DashboardPage } from "@/features/dashboard/DashboardPage";
import { InventoryCardsPage } from "@/features/inventory/InventoryCardsPage";
import { StockOnHandPage } from "@/features/inventory/StockOnHandPage";
import { WarehousesPage } from "@/features/inventory/WarehousesPage";
import { InventoryProfitPage } from "@/features/reports/InventoryProfitPage";
import { useAuthStore } from "@/store/auth";

function ProtectedRoute({ children }: { children: JSX.Element }) {
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated());
  const location = useLocation();
  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location.pathname }} replace />;
  }
  return children;
}

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />
      <Route
        element={
          <ProtectedRoute>
            <AppLayout />
          </ProtectedRoute>
        }
      >
        <Route path="/" element={<DashboardPage />} />
        <Route path="/current-cards" element={<CurrentCardsPage />} />
        <Route path="/inventory/cards" element={<InventoryCardsPage />} />
        <Route path="/inventory/warehouses" element={<WarehousesPage />} />
        <Route path="/inventory/stock" element={<StockOnHandPage />} />
        <Route path="/accounting/journal" element={<JournalEntryPage />} />
        <Route path="/accounting/trial-balance" element={<TrialBalancePage />} />
        <Route path="/accounting/account-balance" element={<AccountBalancePage />} />
        <Route path="/bank/cards" element={<BankCardsPage />} />
        <Route path="/cash/cards" element={<CashCardsPage />} />
        <Route path="/cheques" element={<ChequesPage />} />
        <Route path="/bills" element={<BillsPage />} />
        <Route path="/orders" element={<OrdersPage />} />
        <Route path="/consignments" element={<ConsignmentsPage />} />
        <Route path="/reports/inventory-profit" element={<InventoryProfitPage />} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
