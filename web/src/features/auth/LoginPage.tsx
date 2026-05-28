/*
 * Turquaz Resurrected — GPLv3
 */
import { useMutation } from "@tanstack/react-query";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { Navigate, useLocation, useNavigate } from "react-router-dom";
import { z } from "zod";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { extractApiError } from "@/lib/api";
import { useAuthStore } from "@/store/auth";
import { login } from "./api";

const schema = z.object({
  username: z.string().min(1),
  password: z.string().min(1),
});

type LoginForm = z.infer<typeof schema>;

export function LoginPage() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const location = useLocation();
  const setSession = useAuthStore((s) => s.setSession);
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated());
  const from = (location.state as { from?: string } | null)?.from ?? "/";

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<LoginForm>({ resolver: zodResolver(schema) });

  const mutation = useMutation({
    mutationFn: (form: LoginForm) => login(form.username, form.password),
    onSuccess: (resp) => {
      setSession({
        accessToken: resp.accessToken,
        userId: resp.userId,
        companyId: resp.companyId,
        username: resp.username,
        expiresInSeconds: resp.expiresInSeconds,
        loginAt: Date.now(),
      });
      navigate(from, { replace: true });
    },
  });

  if (isAuthenticated) {
    return <Navigate to={from} replace />;
  }

  const apiError = extractApiError(mutation.error);

  return (
    <div className="min-h-screen flex items-center justify-center p-4 bg-secondary">
      <Card className="w-full max-w-sm">
        <CardHeader>
          <CardTitle>{t("app.title")}</CardTitle>
        </CardHeader>
        <CardContent>
          <form
            className="space-y-4"
            onSubmit={handleSubmit((data) => mutation.mutate(data))}
          >
            <div className="space-y-1.5">
              <Label htmlFor="username">{t("auth.username")}</Label>
              <Input id="username" autoComplete="username" {...register("username")} />
              {errors.username && (
                <p className="text-sm text-destructive">{errors.username.message}</p>
              )}
            </div>
            <div className="space-y-1.5">
              <Label htmlFor="password">{t("auth.password")}</Label>
              <Input
                id="password"
                type="password"
                autoComplete="current-password"
                {...register("password")}
              />
              {errors.password && (
                <p className="text-sm text-destructive">{errors.password.message}</p>
              )}
            </div>
            {apiError && (
              <p className="text-sm text-destructive" role="alert">
                {apiError.message}
              </p>
            )}
            <Button type="submit" className="w-full" disabled={isSubmitting || mutation.isPending}>
              {t("auth.loginButton")}
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  );
}
