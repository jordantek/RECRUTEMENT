"use client"

import { useEffect } from "react"
import PageTitle from "@/components/seo/pageTitle"
import usePageTitleStore from "@/contexts/usePageTitleStore"
import { Icon } from "@tabler/icons-react"
import { User2Icon, Calendar, BookOpen, FileText, FilePlus, Gavel, FileSearch   , Pencil } from "lucide-react"
import { motion } from 'framer-motion'
import { Button } from "@/components/ui/button.tsx"

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

export default function EmployeeHomePage() {
  useEffect(() => {
    usePageTitleStore
      .getState()
      .setTitle("Employés", "Informations de l'employé", User2Icon as Icon)
  }, [])

  return (
    <>
      <PageTitle title="Employé" />
      <div className="p-6 -mt-10 bg-gradient-to-b from-gray-50  to-gray-50- min-h-[calc(100vh-64px)] scale-[0.9] shadow-xl">
        <div className="max-w-7xl mx-auto space-y-6"> {/* Réduit à space-y-8 */}

          {/* Haut de page */}
          <div className="flex gap-6 ml-16 relative">
            {/* Bouton modifier en haut à droite */}
            <div className="absolute -top-4 -right-2 group mt-3">
              <Button
                size="icon"
                className="rounded-full bg-blue-900 hover:bg-blue-700 transition-colors border border-blue-900 shadow-sm"
                variant="ghost"
                onClick={() => {
                  // Action modifier ici
                }}
              >
                <div className="relative">
                  <Pencil className="h-4 w-4 text-white transition-transform duration-200 group-hover:rotate-12 group-hover:scale-110" />
                  <span className="absolute -bottom-7 left-1/2 transform -translate-x-1/2 bg-blue-900 text-white text-xs px-2 py-1 rounded whitespace-nowrap opacity-0 group-hover:opacity-100 transition-opacity">
                    Modifier
                  </span>
                </div>
              </Button>
            </div>

            {/* Profil */}
            <div className="bg-white border border-blue-900 rounded-2xl w-60 p-4 text-white flex flex-col items-center h-fit">
              <div className="bg-white border border-blue-900 rounded-full w-36 h-36 mb-2 flex items-center justify-center"> {/* Réduit mb-3 à mb-2 */}
                <User2Icon className="w-20 h-20 text-blue-900" />
              </div>
              <div className="text-xl font-extrabold text-blue-900">Fatou SOW</div>
              <div className="text-md font-medium text-blue-900">fatou.sow@gmail.com</div>
            </div>

            {/* Infos du poste */}
            <div className="flex-1 space-y-3 mt-10">
              <div className="grid grid-cols-2 gap-5">
                {/* Colonne 1 - Identifiants Administratifs */}
                <div className="bg-white p-5 rounded-xl border border-gray-200 shadow-sm">
                  <h3 className="text-lg font-bold text-blue-900 mb-2">
                    Identifiants Administratifs
                  </h3>
                  <div className="space-y-1 text-zinc-600">
                    <p>
                      <span className="font-semibold">Poste :</span> Comptable
                    </p>
                    <p>
                      <span className="font-semibold">Matricule :</span> A8394r03S3
                    </p>
                    <p>
                      <span className="font-semibold">IFU :</span> 121131FIOFDSSP
                    </p>
                    <p>
                      <span className="font-semibold">CNSS :</span> 239720222
                    </p>
                  </div>
                </div>

                {/* Colonne 2 - Coordonnées de contact */}
                <div className="bg-white p-5 rounded-xl border border-gray-200 shadow-sm">
                  <h3 className="text-lg font-bold text-blue-900 mb-2">
                    Coordonnées de contact
                  </h3>
                  <div className="space-y-1 text-zinc-600">
                    <p>
                      <span className="font-semibold">Téléphone :</span> 0022989293760
                    </p>
                    <p>
                      <span className="font-semibold">Email :</span> port@gmail.com
                    </p>
                    <p>
                      <span className="font-semibold">Domicile :</span> AKPAKPA
                    </p>
                  </div>
                </div>
              </div>
            </div>
          </div>

          {/* Section des cartes fonctionnelles */}
          <div className="mt-6">
            <h2 className="text-2xl font-bold text-blue-900 mb-3 ml-16">
              Actions rapides
            </h2>
            
            {/* Conteneur principal des cartes */}
            <div className="flex flex-col items-center px-16">
              {/* Première ligne avec 3 cartes */}
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-5 mb-3 w-full">
                {[
                  {
                    title: "Mes demandes",
                    desc: "Demandes de congés et absences",
                    icon: <Calendar size={22} className="text-blue-900" />,
                    
                  },
                  {
                    title: "Bulletins de paie",
                    desc: "Consultez vos bulletins de paie",
                    icon: <FileSearch size={22} className="text-blue-900" />,
                  },
                  
                  {
                    title: "Contrats",
                    desc: "Vos contrats de travail",
                    icon: <FileText size={22} className="text-blue-900" />,
                  },
                ].map((card, index) => (
                  <motion.div
                    key={card.title}
                    custom={index}
                    initial="hidden"
                    animate="visible"
                    variants={cardVariants}
                    className="w-full"
                  >
                    <div className="bg-white p-3 rounded-xl border border-gray-200 shadow-sm hover:shadow-md transition-shadow cursor-pointer h-32 flex flex-col justify-center">
                      <div className="flex flex-col items-center text-center space-y-2">
                        <div className="p-2 bg-blue-50 rounded-full flex items-center justify-center">
                          {card.icon}
                        </div>
                        <h3 className="text-xl font-bold text-blue-900">{card.title}</h3>
                        <p className="text-zinc-600 text-xs px-2 line-clamp-2">{card.desc}</p>
                      </div>
                    </div>
                  </motion.div>
                ))}
              </div>

              {/* Deuxième ligne avec 3 cartes centrées */}
              <div className="grid grid-cols-1 sm:grid-cols-3 gap-5 w-full mb-12">
                {[
                  {
                    title: "Formation",
                    desc: "Vos formations suivies et programmés",
                    icon: <BookOpen size={22} className="text-blue-900" />,
                  },
                  {
                    title: "Sanctions",
                    desc: "Historique de sanctions disciplinaires",
                    icon: <Gavel size={22} className="text-blue-900" />,
                  },
                  {
                    title: "Modèles de documents",
                    desc: "Documents administratifs",
                    icon: <FilePlus size={22} className="text-blue-900" />,
                  },
                  
                ].map((card, index) => (
                  <motion.div
                    key={card.title}
                    custom={index + 3}
                    initial="hidden"
                    animate="visible"
                    variants={cardVariants}
                    className="w-full"
                  >
                    <div className="bg-white p-3 rounded-xl border border-gray-200 shadow-sm hover:shadow-md transition-shadow cursor-pointer h-32 flex flex-col justify-center">
                      <div className="flex flex-col items-center text-center space-y-2">
                        <div className="p-2 bg-blue-50 rounded-full flex items-center justify-center">
                          {card.icon}
                        </div>
                        <h3 className="text-xl font-bold text-blue-900">{card.title}</h3>
                        <p className="text-zinc-600 text-xs px-2 line-clamp-2">{card.desc}</p>
                      </div>
                    </div>
                  </motion.div>
                ))}
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  )
}