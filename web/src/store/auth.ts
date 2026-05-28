/*
 * Turquaz Resurrected — GPLv3
 * Oturum durumu: JWT + kullanıcı bilgisi. localStorage'a kalıcılaşır.
 */
import { create } from "zustand";
import { persist } from "zustand/middleware";

interface AuthSession {
  accessToken: string;
  userId: string;
  companyId: string;
  username: string;
  expiresInSeconds: number;
  loginAt: number;
}

interface AuthStore {
  session: AuthSession | null;
  accessToken: string | null;
  setSession: (s: AuthSession) => void;
  clear: () => void;
  isAuthenticated: () => boolean;
}

export const useAuthStore = create<AuthStore>()(
  persist(
    (set, get) => ({
      session: null,
      accessToken: null,
      setSession: (session) =>
        set({ session, accessToken: session.accessToken }),
      clear: () => set({ session: null, accessToken: null }),
      isAuthenticated: () => {
        const s = get().session;
        if (!s) return false;
        const ageSec = (Date.now() - s.loginAt) / 1000;
        return ageSec < s.expiresInSeconds;
      },
    }),
    { name: "turquaz-auth" },
  ),
);
