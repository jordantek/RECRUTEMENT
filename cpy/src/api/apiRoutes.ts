const apiRoutes = {
    auth: {
        login: "/api/auth/login",
    },
    admin: {
        auth: {
            login: "/api/auth/login",
        },
        app: {
            staff: {
                index: "/api/admin/personnel/list",
                create: "/api/admin/personnel/create",

            },
            notifications: {
                list: "/api/common/notifications/list",
                unreadList: "/api/common/notification/list/unread",
                markAsRead: "/api/common/notifications/mark-as-read",
            },
            company: {
                list: "/api/arh/companies/list",
                create: "/api/arh/companies/create",
                employees:{
                  listActif:"/api/paie/contrats-employes/actifs"
                },
                departement: {
                    list_byCompany: "/api/arh/departements",
                    create: "/api/arh/departements",
                    poste: {
                        list_byCompany: "/api/arh/postes",
                        create: "/api/arh/postes",
                    }
                }
            },
            employee: {
                list: "/api/employes",
                list_without_contract:"/api/employes/sans-contrat",
                create: "/api/employes-global",

                categories: {
                    list: "/api/categories_employes",
                    create: "/api/categories_employes/create",
                    update: "/api/categories_employes/",
                    delete: "/api/categories_employes/",
                },
                areaActivity: {
                    list: "/api/common/activity-areas/list",
                    create: "/api/common/activity-areas",
                    update: "/api/common/activity-areas/",
                    delete: "/api/common/activity-areas/",
                },
                diplomas: {
                    list: "/api/diplomes",
                    create: "/api/diplomes",
                    update: "/api/diplomes/",
                    delete: "/api/diplomes/",
                },
                children: {
                    list: "/api/arh/employes/enfant/by-employe/",
                    create: "/api/arh/employes/enfant/create",
                    update:"/api/arh/employes/enfant/update",
                    delete:"/api/arh/employes/enfant/delete"
                },
                personnesAPrevenir: {
                    list: "/api/arh/employes/personnes-a-prevenir/by-employe-id",
                    create: "/api/arh/employes/personnes-a-prevenir/create",
                    update:"/api/arh/employes/personnes-a-prevenir",
                    delete:"/api/arh/employes/personnes-a-prevenir"
                },
                
                lienParent:{
                    list: "/api/liens_parente",
                },
                contrats:{
                  list:"/api/employe/contrats/list/"
                },
                absences: {
                    create: "/api/administration/absences",
                    list: "/api/administration/absences",
                    list_by_employee: "/api/administration/absences/employe",
                    list_by_company: "/api/administration/absences/entreprise",
                    motifsList: "/api/arh/motifs-absence",
                    type: "/api/administration/type-absences",
                    update: "/api/administration/absences/",
                    delete: "/api/administration/absences/",
                },
                formations: {
                    list: "/api/administration/formations",
                    listByCompany: (companyId: number) =>
                    `/api/administration/formations/par-entreprise/${companyId}`,
                    create: "/api/administration/formations",
                    update: (companyId: number) =>
                    `/api/administration/formations/${companyId}`,
                    delete: "/api/administration/formations",
                    list_by_employee:"/api/administration/formations/par-employe/"
                    //delete: "/api/administration/formations/",

                },
                creditConge: {
                    list: "/api/administration/credit-conges",
                    listByCompany: (companyId: number) =>
                    `/api/administration/credit-conges/par-entreprise/${companyId}`,
                    create: "/api/administration/credit-conges",
                    update: "/api/administration/credit-conges",
                    soldeConges: (companyId: number) =>
                    `/api/administration/absences/solde-conge/entreprise/${companyId}`,
                },
            },  

            contrat: {
                list_byCompany:"/api/paie/contrats-employes/par-entreprise",
                create: "/api/enregistrement-contrat",

                natureContrat: {
                    list: "/api/arh/nature-contrats",
                    create: "/api/arh/nature-contrat/create",
                },
                banks: {
                    list: "/api/arh/banques",
                    create: "/api/arh/banques",
                    update: "/api/arh/banques/",
                    delete: "/api/arh/banques/"
                },
                rubriques:{
                    list: "/api/paie/rubriques",
                    listByCompany: "/api/paie/montant-rubriques/entreprise/liste",
                    create: "/api/paie/rubriques",
                    variableList: "/api/paie/rubriques/variables",
                },
                
                modeDePaiement: {
                    list: "/api/arh/mode-paiements",
                    create: "/api/arh/mode-paiements",
                    update: "/api/arh/mode-paiements/",
                    delete: "/api/arh/mode-paiements/"
                },
                tva: {
                    list: "/api/common/tva/list",
                    create: "/api/common/tva/create",
                },
                niveauAffichage: {
                    list: "/api/paie/niveau-affichages",
                   create: "/api/arh/niveau-affichages/create",
                },
                colonneAffichage: {
                    list: "/api/paie/colonne-affichages",
                   create: "/api/paie/colonne-affichages",
                },
            },

            sanction:{
                list_byCompany:"/api/administration/sanctions/par-entreprise/",
                list_by_employee: "/api/administration/sanctions/par-employe/",
                create:"/api/administration/sanctions",
                update:"/api/administration/sanctions",
                delete:"/api/administration/sanctions"
            },

            institutionFinancier:{
               list:"/api/paie/institutions",
               create:"/api/paie/institutions",
                update:"/api/paie/institutions/",
                delete:"/api/paie/institutions/"

            },

            mensualite:{
              list:"/api/paie/mensualites",
              create:"/api/paie/mensualites",
              update:"/api/paie/mensualites",
              delete:"/api/paie/mensualites/delete",
               list_byCompany:"/api/paie/mensualites/par-entreprise/",
                solder:"/api/paie/mensualites/solder"

            },

            prelevementMensuel:{
                list:"/api/paie/prelevements/liste",
                list_byCompany:"/api/paie/prelevements/par-entreprise/"
            },

            accidentsTravail:{
                list_byCompany:"/api/administration/accidents-travail/par-entreprise/",
                list_by_employee: "/api/administration/accidents-travail/par-employe/",
                create:"/api/administration/accidents-travail",
                update:"/api/administration/accidents-travail",
                delete:"/api/administration/accidents-travail"
            },

            evenementsSociaux:{
                list_byCompany:"/api/administration/evenements-socials/par-entreprise/",
                list_by_employee: "/api/administration/evenements-socials/par-employe/",
                create:"/api/administration/evenements-socials",
                update:"/api/administration/evenements-socials",
                delete:"/api/administration/evenements-socials"

            },

            workingTime:{
                list: "/api/paie/temps_de_travail",
                listByCompany: (companyId: number) =>
                    `/api/paie/temps_de_travail/entreprise/${companyId}/liste`,
                listByCompanyByMonth: (companyId: number, month: string) => 
                    `/api/paie/temps_de_travail/entreprise/${companyId}?mois=${month}`,
                saveByCompany: (companyId: number, month: string) =>
                    `/api/paie/temps_de_travail/entreprise/${companyId}/temps-travail?mois=${month}`
            },

            allowanceAndBonus: {
                listByCompany: "/api/paie/montant-rubriques/entreprise/nouveau",
                saveByCompany: (companyId: number) =>
                    `/api/paie/montant-rubriques/entreprise/${companyId}/montants-rubriques`,
            },

            leaveAllowance: {
                calculate: "/api/indemnite-conges/calcul",
                save: "/api/indemnite-conges/allocation/save",
            },

            avance: {
                listByCompany: (companyId: number) =>
                    `/api/paie/avances/par-entreprise/${companyId}`,
                create: "/api/paie/avances",
            },

            traitementSalaire:{
                apercu_avant:"/api/traitement-salaire/apercu-avant",
                calcul_salaire:"/api/traitement-salaire/calculer-salaire",
                apercu_apres:"/api/traitement-salaire/apercu-apres",
                all_log:"/api/traitement-salaire/log-taraitement",
                validation:"/api/traitement-salaire/validated",
                reset:"/api/traitement-salaire/reset-log"
            },

            acompte: {
                listByCompany: (companyId: number) =>
                `/api/paie/acomptes/par-entreprise/${companyId}`,
                listByCompanyByMonth: "/api/paie/acomptes/entreprise/nouveau",
                saveByCompany: (companyId: number) =>
                `/api/paie/acomptes/entreprise/${companyId}/acomptes`,
            },

            rubriquesVariables: {
                listByCompany: (companyId: number) =>
                `/api/paie/rubriques/variables/${companyId}`,
            },

            rubriquesFixes: {
                listByCompany: (companyId: number) =>
                `/api/paie/rubriques/fixes/${companyId}`,
            },


        },
        utils:{
            vatRate: {
                list: "/api/common/tva/list",
                create: "/api/common/tva/create",
                update: "/api/common/tva/update",
                delete: "/api/common/tva/delete",
            },
            activityAreas: {
                list: "/api/common/activity-areas/list",
                create: "/api/common/activity-areas/create",
                update: "/api/common/activity-areas/update",
                delete: "/api/common/activity-areas/delete",
            }
        }
    }
}

export default apiRoutes