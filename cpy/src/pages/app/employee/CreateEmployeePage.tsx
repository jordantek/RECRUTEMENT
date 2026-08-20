import PageTitle from "@/components/seo/pageTitle";
import {AlertTriangle, Baby, User, UserPlus} from "lucide-react";
import { useEffect, useState } from "react";
import usePageTitleStore from "@/contexts/usePageTitleStore";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Form } from "@/components/ui/form";
import ButtonWithLoading from "@/components/ui/button-with-loading";
import {BindFormItem, FieldOption} from "@/components/forms/bind-form-item";
import { useAuth } from "@/lib/auth";
import apiRoutes from "@/api/apiRoutes";
import apiService from "@/api/apiService";
import Lottie from "lottie-react";
import successAnimation from "@/lottiesfiles/sucees.json";
import { useNavigate } from "react-router-dom";
import { routeHelpers } from "@/helpers/routeHelpers";
import { Button } from "@/components/ui/button";
import { employeeSchema } from "@/pages/app/employee/validatorInputEmployee";

// Animation
import { motion } from "framer-motion";
import {z} from "zod";
import {DynamicTable} from "@/components/tables/dynamic-table.tsx";
import {FormStepper} from "@/components/useful/form-stepper.tsx";
import {
    EnfantFormData_, enfantSchema_,
} from "@/components/layout/employee/employee-childre/childrenValidator.ts";
import FormModal from "@/components/useful/form-modal.tsx";
import {Icon} from "@tabler/icons-react";
import {personneAPrevenirScheme, PresonneAPrevenirDataForm} from "@/validators-forms/personne-a-prevenir-schema.ts";
import { format } from "date-fns";
import {Relation} from "@/types/UtilsTypes.ts";
import useCompanyStore from "@/contexts/CompanyContext.ts";

// Fonction utilitaire pour formater en string YYYY-MM-DD
function formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

// Fonction utilitaire pour formater un numéro de téléphone au format attendu par le backend
// Backend attend : ^[0-9]{3}_[0-9]{8,15}$  (ex: 229_0191162385)
function formatTelephone(tel: string): string {
    if (!tel) return tel;
    const digits = tel.replace(/\D/g, ""); // enlève tout sauf les chiffres
    const indicatif = digits.slice(0, 3);
    const numero = digits.slice(3);
    return `${indicatif}_${numero}`;
}

export function CreateEmployeePage() {
    const { user, logout } = useAuth();
    const {setShowCompanySelect} = useCompanyStore();
    const navigate = useNavigate();

    const [enfants, setEnfants] = useState<EnfantFormData_[]>([]);
    const [personnesAPrevenir, setPersonnesAPrevenir] = useState<PresonneAPrevenirDataForm[]>([]);

    const [loadingSubmit, setLoadingSubmit] = useState(false);
    const [successSubmit, setSuccessSubmit] = useState(false);
    const [currentStep, setCurrentStep] = useState(0);

    const [isOpenEnfantAddModal , setIsOpenEnfantAddModal]=useState(false)
    const [isOpenPersonneAddModal , setIsOpenPersonneAddModal]=useState(false)

    const [errorFetch, setErrorFetch] = useState<string | null>(null);
    const [isFetchSetting,setIsFetchSetting]=useState(false)
    const [lienParents,setLienParents]=useState<Relation[]>([])
    const stMatrimoni = [
        { value: "CELIBATAIRE_SANS_ENFANT", label: "CÉLIBATAIRE SANS ENFANT" },
        { value: "CELIBATAIRE_AVEC_ENFANT", label: "CÉLIBATAIRE AVEC ENFANT" },
        { value: "MARIE", label: "MARIÉ(E)" },
        { value: "DIVORCE", label: "DIVORCÉ(E)" }
    ];

    // Formulaire global
    const form = useForm<z.infer<typeof employeeSchema>>({
        resolver: zodResolver(employeeSchema),
        defaultValues: {}
    });
    const employeeFields = [
        {
            tag: "matricule",
            label: "Matricule",
            input_type: "text",
            size: "col-span-6",
            placeholder: "",
            required: true
        },
        {
            tag: "profession",
            label: "Profession",
            input_type: "text",
            size: "col-span-6",
            required: true
        },

        {
            tag: "nom",
            label: "Nom",
            input_type: "text",
            size: "col-span-6",
            placeholder: "",
            required: true
        },
        {
            tag: "prenom",
            label: "Prénoms",
            input_type: "text",
            size: "col-span-6",
            placeholder: "",
            required: true
        },
        {
            tag: "date_naissance",
            label: "Date de naissance",
            input_type: "date",
            size: "col-span-6",
            required: true
        },
        {
            tag: "lieu_naissance",
            label: "Lieu de naissance",
            input_type: "text",
            size: "col-span-6",
            placeholder: "",
            required: true },
        {
            tag: "titre",
            label: "Civilité",
            input_type: "select",
            size: "col-span-4",
            required: true, options: ["MONSIEUR", "MADAME", "MADEMOISELLE"].map(v => ({ label: v, value: v }))
        },
        {
            tag: "sexe",
            label: "Sexe",
            input_type: "select",
            size: "col-span-4",
            required: true,
            options: ["MASCULIN", "FEMININ"].map(v => ({ label: v, value: v }))
        },
        {
            tag: "situationMatrimoniale",
            label: "Situation matrimoniale",
            input_type: "select",
            size: "col-span-4",
            required: true,
            options: stMatrimoni.map(v => ({ label: v.label, value: v.value }))
        },

        {
            tag: "numero_ifu",
            label: "Numéro IFU",
            input_type: "text",
            size: "col-span-6",
            placeholder: "",
            required: true
        },
        {
            tag: "numero_cnss",
            label: "Numéro CNSS",
            input_type: "text",
            size: "col-span-6",
            required: true
        },
        {
            tag: "telephone",
            label: "Téléphone",
            input_type: "phone",
            size: "col-span-6",
            required: true
        },
        {
            tag: "email",
            label: "Email",
            input_type: "email",
            size: "col-span-6",
            required: true
        },
        {
            tag: "quartier",
            label: "Quartier",
            input_type: "text",
            size: "col-span-6",
            required: true
        },
        {
            tag: "nationalite",
            label: "Nationalité",
            input_type: "text",
            size: "col-span-6",
            required: true
        },
    ];

    const enfantFild = [
        {
            tag: "nom_",
            label: "Nom",
            input_type: "text",
            size: "col-span-12",
            required: true,
        },
        {
            tag: "prenom_",
            label: "Prénom",
            input_type: "text",
            size: "col-span-12",
            required: true,
        },
        {
            tag: "sexe_",
            label: "Sexe",
            input_type: "select",
            size: "col-span-12",
            required: true,
            options: ["MASCULIN", "FEMININ"].map((v) => ({ label: v, value: v })),
        },
        {
            tag: "dateNaissance_",
            label: "Date de naissance",
            input_type: "date",
            size: "col-span-6",
            required: true,
        },
        {
            tag: "lieuNaissance_",
            label: "Lieu de naissance",
            input_type: "text",
            size: "col-span-6",
            placeholder: "Ex: Cotonou",
            required: true,
        },
    ];
    const formEnfant = useForm<EnfantFormData_>({
        resolver: zodResolver(enfantSchema_),
        defaultValues: { },
        shouldFocusError: true,
    });
    const resetEnfant= formEnfant.reset;
    const handleAddEnfant = () => {
        setIsOpenEnfantAddModal(true)
    };

    const handleAddEnfantOnSubmit = (_data: z.infer<typeof enfantSchema_>) => {

        const formattedData = {
            ..._data,
            dateNaissance_: formatDate(new Date(_data.dateNaissance_))
        };

        setEnfants([...enfants, formattedData]);
        resetEnfant();
        setIsOpenEnfantAddModal(false);
    };


    const personneAPrevenirFields = [
        {
            tag: "nomPrenom",
            label: "Nom et prénom",
            input_type: "text",
            size: "col-span-12",
            required: true,
        },
        {
            tag: "lienParente",
            label: "Lien de parenté",
            input_type: "select",
            size: "col-span-12",
            required: true,
            options: lienParents.map((l) => ({
                value: l.id.toString(),
                label: l.libelle,
            })),
        },
        {
            tag: "telephone",
            label: "Téléphone",
            input_type: "phone",
            size: "col-span-12",
            required: true,
            placeholder: "Ex: 0700000000",
        },
        {
            tag: "adresse",
            label: "Adresse",
            input_type: "text",
            size: "col-span-12",
            required: false,
            placeholder: "Ex: 123 Rue Exemple, Ville",
        },
        {
            tag: "email",
            label: "Email",
            input_type: "email",
            size: "col-span-12",
            required: false,
            placeholder: "exemple@email.com",
        },
    ];
    const formPersonneAPrevenir = useForm<PresonneAPrevenirDataForm>({
        resolver: zodResolver(personneAPrevenirScheme),
        defaultValues: { },
        shouldFocusError: true,
    });
    const resetPersonneAPrevenir= formPersonneAPrevenir.reset;
    const handleAddPersonne = () => {
        setIsOpenPersonneAddModal(true);
    };
    const handleAddPersonneOnSubmit = (data:z.infer<typeof personneAPrevenirScheme>) =>{
        setPersonnesAPrevenir([...personnesAPrevenir, data]);
        resetPersonneAPrevenir()
        setIsOpenPersonneAddModal(false)
    }

    // Liste des étapes avec leurs champs associés
    const steps: {
        title: string;
        fields: Array<keyof z.infer<typeof employeeSchema>>;
        content?: React.ReactNode;
    }[] = [
        {
            title: "Informations personnelles",
            fields: ["matricule","nom", "prenom",  "titre", "date_naissance", "lieu_naissance", "sexe","situationMatrimoniale","numero_ifu", "numero_cnss", "profession"],
        },
        {
            title: "Coordonnées",
            fields: ["telephone", "email", "quartier", "nationalite"],
        },
        {
            title: "Enfants",
            fields: [],
            content: (
                <DynamicTable
                    columns={[
                        { key: "nom_", label: "Nom" },
                        { key: "prenom_", label: "Prénoms" },
                        { key: "sexe_", label: "Sexe" },
                        { key: "dateNaissance_", label: "Date de naissance" },
                        { key: "lieuNaissance_", label: "Lieu de naissance" },
                    ]}
                    data={enfants}
                    onAdd={handleAddEnfant}
                    onDelete={(row) =>
                        setEnfants((prev) => prev.filter((e) => e !== row))
                    }
                />
            ),
        },
        {
            title: "Personnes à prévenir",
            fields: [],
            content: (
                <DynamicTable
                    columns={[
                        { key: "nomPrenom", label: "Nom & Prénoms" },
                        { key: "lienParente", label: "Lien" },
                        { key: "telephone", label: "Tel" },
                        { key: "adresse", label: "Adresse" },
                        { key: "email", label: "Email" },

                    ]}
                    data={personnesAPrevenir}
                    onAdd={handleAddPersonne}
                    onDelete={(row) =>
                        setPersonnesAPrevenir((prev) => prev.filter((e) => e !== row))
                    }
                />
            ),
        },
    ];

    const onSubmit = async (data: z.infer<typeof employeeSchema>) => {
        try {
            setLoadingSubmit(true);

            const enfantsTransforms = enfants.map((e) => ({
                nom: e.nom_,
                prenom: e.prenom_,
                sexe: e.sexe_,
                dateNaissance: e.dateNaissance_,
                lieuNaissance: e.lieuNaissance_,
            }));


            // ✅ FIX : le backend attend "lienParenteId" (string) directement,
            // pas un objet imbriqué "lienParente: { id: ... }"
            // ✅ FIX : le téléphone doit respecter le format attendu par le backend (229_XXXXXXXXXX)
            const transformedPersonnes = personnesAPrevenir.map(personne => ({
                nomPrenom: personne.nomPrenom,
                lienParenteId: personne.lienParente,
                telephone: formatTelephone(personne.telephone),
                adresse: personne.adresse,
                email: personne.email,
            }));

            data.date_naissance=new Date(data.date_naissance)

            // ✅ FIX : le payload ne doit plus être imbriqué sous "employe",
            // le backend attend tous les champs de l'employé directement à la racine
            const dataToSubmit = {
                ...data,
                date_naissance: format(data.date_naissance, "yyyy-MM-dd"),
                telephone: formatTelephone(data.telephone),
                enfants: enfantsTransforms,
                personnesAPrevenir: transformedPersonnes,
            }
           /* body: JSON.stringify(dataToSubmit),
                headers: {
                "Content-Type": "application/json",
            }*/
            console.log(dataToSubmit)
            await apiService.post({
                url: apiRoutes.admin.app.employee.create,
                body: JSON.stringify(dataToSubmit),
                headers: {
                    "Content-Type": "application/json"
                }
            }, {
                userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                hasNoSuccessModal: true,
                onTokenExpired: logout
            });

            form.reset();
            setSuccessSubmit(true);
        } catch (error) {
            console.error("Erreur lors de la soumission du formulaire :", error);
            if (error instanceof Error) {
                apiService.handleError(error.message, { form });
            } else {
                apiService.handleError("Erreur inconnue", { form });
            }
        } finally {
            setLoadingSubmit(false);
        }
    };
    const fetchData = async () => {
        try {
            setIsFetchSetting(true);
            setErrorFetch(null);  // reset erreur avant fetch
            const [lien_parents] = await Promise.all([
                apiService.get(
                    { url: apiRoutes.admin.app.employee.lienParent.list },
                    { userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "", hasNoSuccessModal: true }
                ),
            ]);
            setLienParents(lien_parents.data || []);
        } catch (error) {
            if (error instanceof Error) {
                setErrorFetch(error.message);
                apiService.handleError(error.message, { hasNoFailureModal: true });
            } else {
                setErrorFetch("Une erreur inconnue est survenue");
            }
        } finally {
            setIsFetchSetting(false);
        }
    };
    useEffect(() => {
        usePageTitleStore.getState().setTitle("Ajouter un employé", "Ajoutez un nouvel employé à l'entreprise.", User as any);
        setShowCompanySelect(false);
        fetchData()
    }, []);

    const goToNextStep = async () => {
        const currentFields = steps[currentStep].fields as Array<keyof z.infer<typeof employeeSchema>>;
        const isValid = await form.trigger(currentFields);
      /*  const currentFields = steps[currentStep].fields;
        const isValid = await form.trigger(currentFields);*/
        if (isValid) {
            setCurrentStep(prev => Math.min(prev + 1, steps.length - 1));
        }
    };

    const goToPrevStep = () => {
        setCurrentStep(prev => Math.max(prev - 1, 0));
    };
    const SkeletonLoader = () => (
        <div className="animate-pulse space-y-2">
            <div className="h-6 bg-gray-300 rounded w-3/4"></div>
            <div className="h-6 bg-gray-300 rounded w-5/6"></div>
            <div className="h-6 bg-gray-300 rounded w-2/3"></div>
        </div>
    );
    return (
        <>
            {isFetchSetting && <SkeletonLoader />}
            {errorFetch && (
                <div className="flex flex-col items-center justify-center space-y-4 p-6 border border-red-400 rounded-md bg-red-50 text-red-700">
                    <AlertTriangle size={48} />
                    <p>Erreur lors du chargement des données : {errorFetch}</p>
                    <button
                        onClick={fetchData}
                        className="px-4 py-2 rounded bg-red-600 text-white hover:bg-red-700 transition"
                    >
                        Réessayer
                    </button>
                </div>
            )}
            {!isFetchSetting && !errorFetch && (
                <>
                    <FormModal
                    icon={Baby as Icon}
                    title={"Ajouter un enfant"}
                    description={"Remplissez les informations pour ajouter une nouvelle banque."}
                    isOpen={isOpenEnfantAddModal}
                    setIsOpen={setIsOpenEnfantAddModal}
                    form={formEnfant}
                    fields={enfantFild}
                    onSubmit={handleAddEnfantOnSubmit}
                    //isSubmitLoading={loadingSubmit}
                    onClose={() => {
                        resetEnfant();
                        setIsOpenEnfantAddModal(false);
                    }}
                />
                <FormModal
                    icon={UserPlus as Icon}
                    title={"Ajouter une personne a prévenir"}
                    description={"Remplissez les informations pour ajouter une personne à prévenir"}
                    isOpen={isOpenPersonneAddModal}
                    setIsOpen={setIsOpenPersonneAddModal}
                    form={formPersonneAPrevenir}
                    fields={personneAPrevenirFields}
                    onSubmit={handleAddPersonneOnSubmit}
                    onClose={() => {
                        resetPersonneAPrevenir();
                        setIsOpenPersonneAddModal(false);
                    }}
                />

                <PageTitle title="Tableau de bord" />
                {loadingSubmit && (
                    <div className="fixed inset-0 z-50 bg-black/50 flex items-center justify-center">
                        <div className="text-white text-center">
                            <svg className="animate-spin h-10 w-10 mx-auto mb-4" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                                <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v4a4 4 0 00-4 4H4z" />
                            </svg>
                            <p>Traitement en cours...</p>
                        </div>
                    </div>
                )}
                {successSubmit && (
                    <div className="fixed inset-0 z-50 bg-black/40 backdrop-blur-sm flex items-center justify-center px-4">
                        <div className="w-full max-w-[300px] bg-white dark:bg-zinc-900 rounded-2xl shadow-2xl p-6 text-center space-y-6 animate-fade-in">
                            <div className="w-35 h-25 mx-auto">
                                <Lottie animationData={successAnimation} loop={false} />
                            </div>
                            <h2 className="text-base font-semibold text-zinc-800 dark:text-zinc-100">
                                Employé enregistré avec succès !
                            </h2>
                            <Button variant="outline" onClick={() => navigate(routeHelpers.dashboard.employee.index)}>
                                OK
                            </Button>
                        </div>
                    </div>
                )}

                <div className="space-y-6 p-5">
                    <Form {...form}>
                        <form onSubmit={form.handleSubmit(onSubmit)} className="mb-10 px-44 ">
                            <div className={"p-4  rounded-lg border bg-white"}>
                                {/* Stepper */}
                                <FormStepper
                                    steps={steps}
                                    currentStep={currentStep}
                                    onStepChange={setCurrentStep}
                                />
                                {/* Champs de l'étape avec animation */}
                                <motion.div
                                    key={currentStep}
                                    initial={{opacity: 0, y: 10}}
                                    animate={{opacity: 1, y: 0}}
                                    exit={{opacity: 0, y: -10}}
                                    transition={{duration: 0.3, ease: "easeInOut"}}
                                    className="grid  gap-2 p-2 rounded-lg w-full "
                                >
                                    {steps[currentStep].content ? (
                                        steps[currentStep].content
                                    ) : (
                                        <div className="grid grid-cols-12 gap-2 w-full">
                                            {employeeFields
                                                .filter(field => steps[currentStep].fields.includes(field.tag as keyof z.infer<typeof employeeSchema>))
                                                .map((field,index) => (
                                                    <BindFormItem
                                                        key={field.tag}
                                                        option={field as FieldOption}
                                                        form={form}
                                                        tag={field.tag}
                                                        readonly={false}
                                                        index={index}
                                                    />
                                                ))}
                                        </div>
                                    )}
                                </motion.div>

                                {/* Boutons navigation */}
                                <div className="flex justify-end mt-6 space-x-2">
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
                                            classList=""
                                        />
                                    )}
                                </div>
                            </div>
                        </form>
                    </Form>
                </div>
            </>
            )}
        </>
    );
}