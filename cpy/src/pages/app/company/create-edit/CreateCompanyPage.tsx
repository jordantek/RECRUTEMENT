import PageTitle from '@/components/seo/pageTitle.tsx';
import { Building } from 'lucide-react';
import { useEffect, useState } from "react";
import usePageTitleStore from "@/contexts/usePageTitleStore.ts";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Form } from "@/components/ui/form.tsx";
import ButtonWithLoading from "@/components/ui/button-with-loading.tsx";
import { BindFormItem, FieldOption } from "@/components/forms/bind-form-item.tsx";
import { useAuth } from "@/lib/auth.ts";
import {ActivityAreaType, BankType, VatRate} from "@/types/UtilsTypes.ts";
import apiRoutes from "@/api/apiRoutes.ts";
import apiService from "@/api/apiService.ts";
import Lottie from "lottie-react";
import successAnimation from "@/lottiesfiles/sucees.json";
import { useNavigate } from 'react-router-dom';
import { routeHelpers } from "@/helpers/routeHelpers.ts";
import { Button } from "@/components/ui/button.tsx";
import { motion } from "framer-motion";
import {Skeleton} from "@/components/ui/skeleton.tsx";
import {companySchema} from "@/pages/app/company/create-edit/validatorInputCompany.ts";
import {z} from "zod";
import {FormStepper} from "@/components/useful/form-stepper.tsx";
import useCompanyStore from '@/contexts/CompanyContext';

export function CreateCompanyPage() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();
    const [vatRates, setVatRates] = useState<VatRate[]>([]);
    const {fetchCompanyList}=useCompanyStore();
    const [areaActivitiesOptions, setAreaActivitiesOptions] = useState<{ value: string; label: string }[]>([]);
    const [bankOptions,setBankOptions]=useState<{ value: string; label: string }[]>([]);
    const [loadingSubmit, setLoadingSubmit] = useState(false);
    const [successSubmit, setSuccessSubmit] = useState(false);
    const [isFetchSetting, setIsFetchSetting] = useState(false);

    // Etape actuelle
    const [currentStep, setCurrentStep] = useState(0);

    // Champs du formulaire
    const identityFields = [
        {
            tag: "name",
            label: "Raison sociale",
            input_type: "text",
            size: "col-span-6",
            placeholder: "Ex: Tech Solutions",
            required: true,
        },
        {
            tag: "country",
            label: "Pays",
            input_type: "text",
            size: "col-span-6",
            placeholder: "Ex: Bénin",
            required: false,
        },
        {
            tag: "address",
            label: "Adresse",
            input_type: "text",
            size: "col-span-6",
            placeholder: "123 Rue des Entrepreneurs, Cotonou",
            required: false,
        },
        {
            tag: "nss",
            label: "Numéro de sécurité sociale (NSS)",
            input_type: "text",
            size: "col-span-6",
            placeholder: "Saisir NSS",
            required: true,
        },
        {
            tag: "rss",
            label: "Risque sécuritaire social (RSS)",
            input_type: "select",
            size: "col-span-6",
            placeholder: "0",
            required: true,
            options: [{ value: "1", label: "1 %" }, { value: "2", label: "2 %" },{ value: "3", label: "3 %" },{ value: "4", label: "4 %" }]
        },
        {
            tag: "rccm",
            label: "RCCM",
            input_type: "text",
            size: "col-span-6",
            placeholder: "RB/COT/22B19345",
            required: true,
        },
        {
            tag: "ifu",
            label: "IFU",
            input_type: "text",
            size: "col-span-6",
            placeholder: "3201513265492",
            required: true,
        },
        {
            tag: "tvaVal",
            label: "TVA (%)",
            input_type: "select",
            size: "col-span-6 text-black",
            placeholder: "18",
            required: true,
            options: vatRates.map((v) => ({
                value: v.rate.toFixed(2),
                label: `${v.rate.toFixed(2)} %`,
            }))
        }
    ];

    const activityFields = [
        {
            tag: "activityAreas",
            label: "Domaines d'activité",
            input_type: "multiselect",
            size: "col-span-12",
            placeholder: "Choisissez un ou plusieurs domaines",
            required: false,
            options: areaActivitiesOptions,
        },
        {
            tag: "banqueIds",
            label: "Banques",
            input_type: "multiselect",
            size: "col-span-12",
            placeholder: "Choisissez un ou plusieurs banques",
            required: false,
            options: bankOptions,
        },
        {
            tag: "email",
            label: "Email",
            input_type: "email",
            size: "col-span-6",
            placeholder: "contact@entreprise.com",
            required: true,
        },
        {
            tag: "phone",
            label: "Téléphone",
            input_type: "phone",
            size: "col-span-6",
            placeholder: "+22900000000",
            required: true,
        },
        {
            tag: "webSite",
            label: "Site web",
            input_type: "link",
            size: "col-span-6",
            placeholder: "www.entreprise.com",
            required: false,
        }
    ];

    const directorFields = [
        {
            tag: "directorName",
            label: "Nom du directeur",
            input_type: "text",
            size: "col-span-6",
            placeholder: "Jean Dupont",
            required: true,
        },
        {
            tag: "directorEmail",
            label: "Email du directeur",
            input_type: "email",
            size: "col-span-6",
            placeholder: "jean.dupont@email.com",
            required: true,
        },
        {
            tag: "directorPhone",
            label: "Téléphone du directeur",
            input_type: "phone",
            size: "col-span-6",
            placeholder: "+22900000000",
            required: true,
        }
    ];

    // Fusionner tous les champs
    const allFields = [...identityFields, ...activityFields, ...directorFields];

    // Liste des étapes
    const steps = [
        {//Légal & Fiscal
            title: "Identité & Fiscal",
            fields: identityFields.map(f => f.tag)
        },
        {
            title: "Domaine d'activité & contact",
            fields: activityFields.map(f => f.tag)
        },

        {
            title: "Directeur",
            fields: directorFields.map(f => f.tag)
        }
    ];

    const form = useForm<z.infer<typeof companySchema>>({
        resolver: zodResolver(companySchema),
        defaultValues: {}
    });

    // Soumission finale
    const onSubmit = async (data: any) => {
        try {
            setLoadingSubmit(true);
            await apiService.post(
                { url: apiRoutes.admin.app.company.create, body: data },
                {
                    userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                    hasNoSuccessModal: true,
                    onTokenExpired: logout
                }
            );
            setSuccessSubmit(true);
            fetchCompanyList(user??{ type: "", token: "" }); // Rafraîchir la liste des entreprises
        } catch (error) {
            if (error instanceof Error) {
                apiService.handleError(error.message, { form });
            }
        } finally {
            setLoadingSubmit(false);
        }
    };

    const fetchData = async () => {
        try {
            setIsFetchSetting(true);
            const [vatRate, activitiesArea,banks] = await Promise.all([
                apiService.get({ url: apiRoutes.admin.utils.vatRate.list }, { userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "", hasNoSuccessModal: true }),
                apiService.get({ url: apiRoutes.admin.utils.activityAreas.list }, { userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "",hasNoSuccessModal: true }),
                apiService.get({ url: apiRoutes.admin.app.contrat.banks.list }, { userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "", hasNoSuccessModal: true }),
            ]);
            setVatRates(vatRate.data || []);
            setAreaActivitiesOptions((activitiesArea.data || []).map((area: ActivityAreaType) => ({ value: area.name, label: area.name })));
            setBankOptions((banks.data||[]).map((bank:BankType)=>({value:bank.id.toString(),label:bank.name})))
        } catch (error) {
            if (error instanceof Error) {
                apiService.handleError(error.message, { hasNoFailureModal: true });
            }
        } finally {
            setIsFetchSetting(false);
        }
    };

    useEffect(() => {
        usePageTitleStore.getState().setTitle("Ajouter une entreprise", "Ajoutez une entreprise pour attribuer des employés.", Building as any);
        fetchData();
    }, []);

    const goToNextStep = async () => {
        const currentFields = steps[currentStep].fields;
        const isValid = await form.trigger(currentFields as any);
        if (isValid) {
            setCurrentStep(prev => prev + 1);
        }
    };

    const goToPrevStep = () => {
        setCurrentStep(prev => Math.max(prev - 1, 0));
    };

    return (
        <>
            <PageTitle title="Tableau de bord" />
            {/* Overlay loading */}
            {loadingSubmit && (
                <div className="fixed inset-0 z-50 bg-black/50 flex items-center justify-center">
                    <div className="text-white text-center">
                        <svg className="animate-spin h-10 w-10 mx-auto mb-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                            <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z"></path>
                        </svg>
                        <p>Traitement en cours...</p>
                    </div>
                </div>
            )}

            {/* Modal succès */}
            {successSubmit && (
                <div className="fixed inset-0 z-50 bg-black/40 backdrop-blur-sm flex items-center justify-center px-4">
                    <div className="w-full max-w-[300px] bg-white dark:bg-zinc-900 rounded-2xl shadow-2xl p-6 text-center space-y-6 animate-fade-in">
                        <div className="w-35 h-25 mx-auto">
                            <Lottie animationData={successAnimation} loop={false} />
                        </div>
                        <h2 className="text-base font-semibold text-zinc-800 dark:text-zinc-100">
                            Entreprise enregistrée avec succès !
                        </h2>
                        <Button variant={"outline"} onClick={() => navigate(routeHelpers.dashboard.company.index)}>
                            OK
                        </Button>
                    </div>
                </div>
            )}

            <div className="space-y-6 p-5">
                {isFetchSetting ? (
                    <div className="space-y-6">
                        {[...Array(5)].map((_, i) => <Skeleton key={i} className="h-10 col-span-6" />)}
                    </div>
                ) : (
                    <Form {...form}>
                        <form onSubmit={form.handleSubmit(onSubmit)} className="mb-10 px-44">
                            <div className=" border rounded-lg space-y-4  bg-white">
                                {/* Stepper */}
                              <div className={"px-4 pt-2"}>
                                  <FormStepper
                                      steps={steps}
                                      currentStep={currentStep}
                                      onStepChange={setCurrentStep}
                                  />
                              </div>
                                <hr/>

                                {/* Contenu de l'étape animée */}
                                <motion.div
                                    key={currentStep}
                                    initial={{ opacity: 0, y: 10 }}
                                    animate={{ opacity: 1, y: 0 }}
                                    exit={{ opacity: 0, y: -10 }}
                                    transition={{ duration: 0.3 }}
                                    className="grid grid-cols-12 gap-2 px-4"
                                >
                                    {allFields
                                        .filter(field => steps[currentStep].fields.includes(field.tag))
                                        .map((field, index) => (
                                            <BindFormItem
                                                key={field.tag}
                                                option={field as FieldOption}
                                                form={form}
                                                tag={field.tag}
                                                readonly={false}
                                                index={index}
                                            />
                                        ))}
                                </motion.div>

                                {/* Boutons navigation */}
                                <div className="flex justify-end mt-6 space-x-2 px-4 pb-2">
                                    {currentStep > 0 && (
                                        <Button type="button" variant="outline" onClick={goToPrevStep}>
                                            Précédent
                                        </Button>
                                    )}
                                    {currentStep < steps.length - 1 ? (
                                        <Button type="button" onClick={goToNextStep}>
                                            Suivant
                                        </Button>
                                    ) : (
                                        <ButtonWithLoading
                                            type="submit"
                                            title="Enregistrer"
                                            loading={loadingSubmit}
                                            classList="bg-blue-500 text-white"
                                        />
                                    )}
                                </div>
                            </div>
                        </form>
                    </Form>
                )}
            </div>
        </>
    );
}