/*
 * Turquaz Resurrected — GPLv3
 */
import { beforeEach, describe, expect, it } from "vitest";
import { useAuthStore } from "@/store/auth";

describe("auth store", () => {
  beforeEach(() => {
    useAuthStore.getState().clear();
  });

  it("session boşken authenticated değil", () => {
    expect(useAuthStore.getState().isAuthenticated()).toBe(false);
  });

  it("setSession sonrası token okunabilir ve authenticated true", () => {
    useAuthStore.getState().setSession({
      accessToken: "tk",
      userId: "u",
      companyId: "c",
      username: "alice",
      expiresInSeconds: 3600,
      loginAt: Date.now(),
    });
    expect(useAuthStore.getState().accessToken).toBe("tk");
    expect(useAuthStore.getState().isAuthenticated()).toBe(true);
  });

  it("süresi geçmişse authenticated false", () => {
    useAuthStore.getState().setSession({
      accessToken: "tk",
      userId: "u",
      companyId: "c",
      username: "alice",
      expiresInSeconds: 1,
      loginAt: Date.now() - 10_000,
    });
    expect(useAuthStore.getState().isAuthenticated()).toBe(false);
  });
});
