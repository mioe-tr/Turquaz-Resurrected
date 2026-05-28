/*
 * Turquaz Resurrected — GPLv3
 */
import type { JSX } from "react";
import { Navigate, Route, Routes, useLocation } from "react-router-dom";
import { AppLayout } from "@/AppLayout";
import { LoginPage } from "@/features/auth/LoginPage";
import { CurrentCardsPage } from "@/features/currentCards/CurrentCardsPage";
import { useAuthStore } from "@/store/auth";

function ProtectedRoute({ children }: { children: JSX.Element }) {
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated());
  const location = useLocation();
  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location.pathname }} replace />;
  }
  return children;
}

function Dashboard() {
  return (
    <div className="container py-6">
      <h1 className="text-2xl font-semibold">Pano</h1>
      <p className="text-muted-foreground mt-2">
        Soldaki menüden modül seçin.
      </p>
    </div>
  );
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
        <Route path="/" element={<Dashboard />} />
        <Route path="/current-cards" element={<CurrentCardsPage />} />
      </Route>
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
