import PageTitle from '@/components/seo/pageTitle';
import { UserCog,Building2, Users, Banknote, Settings, Coins, ClipboardList, FilePlus, Calculator,
} from 'lucide-react';
import { useEffect } from 'react';
import usePageTitleStore from '@/contexts/usePageTitleStore.ts';
import { Icon } from '@tabler/icons-react';
import { DashboardCard } from './dashboard-card';
import { routeHelpers } from "@/helpers/routeHelpers";
import { motion } from 'framer-motion';
import useCompanyStore from "@/contexts/CompanyContext.ts";

const cardVariants = {
  hidden: { opacity: 0, y: 20 },
  visible: (i: number) => ({
    opacity: 1,
    y: 0,
    transition: {
      delay: i * 0.1,
      duration: 0.4,
    },
  }),
};

export function DashboardPage() {
  useEffect(() => {
    usePageTitleStore
      .getState()
      .setTitle('Accueil', 'Talents Gest Paie', UserCog as Icon);
    useCompanyStore.getState().setShowCompanySelect(false);
  }, []);

  return (
    <>
      <PageTitle title="Tableau de bord" />

      {/* En-tête */}
      <motion.div
          className="w-full mt-5 t-8 text-center mb-10"
          initial={{opacity: 0, y: -20}}
          animate={{opacity: 1, y: 0}}
          transition={{duration: 0.5}}
      >

        {/* <h1 className="mb-4 text-3xl font-extrabold text-gray-900 dark:text-white md:text-3xl lg:text-5xl pt-8"><span
            className="text-transparent bg-clip-text bg-gradient-to-r to-blue-600 from-sky-400">TALENTS GEST</span> PAIE
        </h1>*/}
        <h1 className="mb-2 text-3xl font-extrabold text-gray-900 dark:text-white md:text-3xl lg:text-5xl pt-8">
          <span className="text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-pink-500">
            TALENTS GEST
          </span>{' '}
          PAIE
        </h1>
    {/*    <h1 className="mb-4 text-3xl font-extrabold text-gray-900 dark:text-white md:text-3xl lg:text-5xl pt-8">
  <span className="text-transparent bg-clip-text bg-gradient-to-r from-blue-600 to-orange-500">
    TALENTS GEST
  </span>{' '}
          PAIE
        </h1>*/}
        <p
            className="text-lg font-medium text-gray-500 lg:text-base sm:px-16 xl:px-48 dark:text-muted-foreground ">Sélectionnez
          une option pour
          commencer à gérer vos ressources humaines et la paie.
        </p>


      </motion.div>

      {/* Actions rapides */}
      <div className="w-full flex items-center justify-center scale-[1] mb-10">
        <div>
          <motion.div
              className="max-full mx-auto mb-5"
              initial={{opacity: 0}}
              animate={{ opacity: 1 }}
            transition={{ delay: 0.4, duration: 0.5 }}
          >
          
          </motion.div>

          <div className="bg-transparent rounded-xl grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6 ">
            {[
              {
                title: "Entreprises",
                desc: "Configurez et gérez les entités légales et structures organisationnelles.",
                icon: <Building2 size={50} />,
                to: routeHelpers.dashboard.company.index
              },
              {
                title: "Employés / STAFF",
                desc: "Ajoutez, modifiez et suivez les informations de vos employés.",
                icon: <Users size={50} />,
                to: routeHelpers.dashboard.employee.index
              },
              {
                title: "Traitement de la paie",
                desc: "Automatisez le calcul et la distribution des salaires.",
                icon: <Banknote size={50} />,
                to: routeHelpers.dashboard.payrollManagement.salaryProcessing
              },
              {
                title: "Eléments de rémunérations",
                desc: "Définissez et gérez les composantes salariales et les avantages.",
                icon: <Coins size={50} />,
                to: routeHelpers.dashboard.settings.payroll
              },
              {
                title: "Gestion administrative",
                desc: "Centralisez et simplifiez les processus administratifs RH.",
                icon: <ClipboardList size={50} />,
                to: routeHelpers.dashboard.administrativeManagement.index
              },
              {
                title: "Gestion du personnel",
                desc: "Organisez et suivez les carrières et mouvements du personnel.",
                icon: <UserCog size={50} />,
                to: routeHelpers.dashboard.staff.index
              },
              {
                title: "Simulation de salaire",
                desc: "Estimez les salaires selon les variables fiscales et sociales.",
                icon: <Calculator size={50} />,
                to: routeHelpers.dashboard.simulation.brutToNet
              },
              {
                title: "Modèles de documents",
                desc: "Créez et gérez des modèles de contrats et documents RH.",
                icon: <FilePlus size={50} />,
                to: routeHelpers.dashboard.docTemplate.index
              },
              {
                title: "Paramètres système",
                desc: "Personnalisez et paramétrez votre SIRH selon vos besoins.",
                icon: <Settings size={50} />,
                to: routeHelpers.dashboard.settings.index
              },
            ].map((card, index) => (
              <motion.div
                key={card.title}
                custom={index}
                initial="hidden"
                animate="visible"
                variants={cardVariants}
              >
                <DashboardCard
                  title={card.title}
                  description={card.desc}
                  icon={card.icon}
                  to={card.to}
                />
              </motion.div>
            ))}
          </div>
        </div>
      </div>
    </>
  );
}
