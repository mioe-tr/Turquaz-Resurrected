/*
 * Turquaz Resurrected — GPLv3
 */
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { describe, expect, it } from "vitest";
import { LoginPage } from "@/features/auth/LoginPage";
import "@/i18n";

describe("LoginPage", () => {
  it("kullanıcı adı ve parola alanlarını gösterir", () => {
    const qc = new QueryClient();
    render(
      <QueryClientProvider client={qc}>
        <MemoryRouter>
          <LoginPage />
        </MemoryRouter>
      </QueryClientProvider>,
    );
    expect(screen.getByLabelText("Kullanıcı Adı")).toBeInTheDocument();
    expect(screen.getByLabelText("Parola")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Giriş" })).toBeInTheDocument();
  });
});
