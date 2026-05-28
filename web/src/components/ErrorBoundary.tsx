/*
 * Turquaz Resurrected — GPLv3
 */
import { Component, type ReactNode } from "react";

interface State {
  error: Error | null;
}

export class ErrorBoundary extends Component<{ children: ReactNode }, State> {
  state: State = { error: null };

  static getDerivedStateFromError(error: Error): State {
    return { error };
  }

  componentDidCatch(error: Error, info: { componentStack?: string | null }) {
    console.error("Yakalanmış hata:", error, info);
  }

  render() {
    if (this.state.error) {
      return (
        <div className="min-h-screen flex items-center justify-center p-6">
          <div className="max-w-md w-full rounded-lg border border-destructive/40 bg-destructive/5 p-6">
            <h1 className="text-xl font-semibold text-destructive">Beklenmeyen hata</h1>
            <p className="text-sm mt-2 text-muted-foreground">
              Uygulama bir hatayla karşılaştı. Sayfayı yenileyerek tekrar deneyin.
            </p>
            <pre className="text-xs mt-3 p-2 bg-background rounded border overflow-auto">
              {this.state.error.message}
            </pre>
            <button
              type="button"
              className="mt-4 inline-flex items-center justify-center rounded-md bg-primary text-primary-foreground h-9 px-4 text-sm font-medium"
              onClick={() => {
                this.setState({ error: null });
                window.location.reload();
              }}
            >
              Sayfayı Yenile
            </button>
          </div>
        </div>
      );
    }
    return this.props.children;
  }
}
