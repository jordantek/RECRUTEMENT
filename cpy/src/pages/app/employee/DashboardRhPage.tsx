import PageTitle from '@/components/seo/pageTitle';
import { UserCog } from 'lucide-react';
import { useEffect } from "react";
import usePageTitleStore from "@/contexts/usePageTitleStore.ts";
import { Icon } from "@tabler/icons-react";

export function DashboardRhPage() {
  useEffect(() => {
    usePageTitleStore
      .getState()
      .setTitle("Tableau ", "Tableau de bord RH", UserCog as Icon);
  }, []);

  return (
    <>
      <PageTitle title="Tableau de bord RH" />
      
    </>
  );
}
