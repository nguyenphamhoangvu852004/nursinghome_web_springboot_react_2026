import { RouterProvider } from "react-router";
import { router } from "@/routes/router";
import { QueryClientProvider } from "@tanstack/react-query";
import { queryClient } from "./lib/query-client";
import { Toaster } from "@/components/ui/sonner";
import { GlobalModal } from "./modals/global-modal";

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <RouterProvider router={router} />
      <GlobalModal />
      <Toaster position="top-right" richColors />
    </QueryClientProvider>
  );
}

export default App;
