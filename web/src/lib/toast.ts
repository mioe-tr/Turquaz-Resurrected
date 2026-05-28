/*
 * Turquaz Resurrected — GPLv3
 * Toast bildirimi sarmalayıcı. Sonner'ı doğrudan içe aktarmak yerine bunu
 * kullanırsak ileride kütüphane değiştirmek tek dosya değişikliği olur.
 */
import { toast as sonnerToast } from "sonner";
import { extractApiError } from "@/lib/api";

export const toast = {
  success: (message: string) => sonnerToast.success(message),
  error: (message: string) => sonnerToast.error(message),
  info: (message: string) => sonnerToast(message),

  /** API hatasını yakalar; mesaj alanını gösterir, yoksa fallback'i kullanır. */
  apiError: (err: unknown, fallback = "Bir hata oluştu") => {
    const api = extractApiError(err);
    sonnerToast.error(api?.message ?? fallback);
  },
};
