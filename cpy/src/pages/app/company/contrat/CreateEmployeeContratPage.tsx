import PageTitle from '@/components/seo/pageTitle.tsx';
import {FileSignature} from 'lucide-react';
import { useEffect, useState } from "react";
import usePageTitleStore from "@/contexts/usePageTitleStore.ts";
import { Icon } from "@tabler/icons-react";
import * as z from 'zod';
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { Form } from "@/components/ui/form.tsx";
import ButtonWithLoading from "@/components/ui/button-with-loading.tsx";
import {BindFormItem, FieldOption} from "@/components/forms/bind-form-item.tsx";
import { useAuth } from "@/lib/auth.ts";
import apiRoutes from "@/api/apiRoutes.ts";
import apiService from "@/api/apiService.ts";
import Lottie from "lottie-react";
import successAnimation from "@/lottiesfiles/sucees.json";
import {useNavigate, useParams} from "react-router-dom";
import { routeHelpers } from "@/helpers/routeHelpers.ts";
import { Button } from "@/components/ui/button.tsx";
import AddEmployeeCategoryModal from "@/components/layout/settings/add-employee-category.tsx"
import { EmployeeType, EmployeeCategory } from "@/types/employee/EmployeeType.ts";
import {AddElementSalaireModal} from "@/components/layout/contracts/add-element-salaire-modal.tsx";
import {AddEmployeeDiplomaModal} from "@/components/layout/contracts/add-employee-diploma-modal.tsx";
import {contratdefaultValues, contratSchema} from "@/pages/app/company/contrat/validatorInputContract.ts";
import {
    BankType,
    DepartmentType,
    DiplomeType, ModeDePaiementType,
    NatureContratType,
    PosteType,
    RubriqueSalaireType
} from "@/types/UtilsTypes.ts";
import useCompanyStore from "@/contexts/CompanyContext.ts";

export type ContratFormData = z.infer<typeof contratSchema>

export function CreateEmployeeContratPage() {
    const { user, logout } = useAuth();
    const { company_id, company_name } = useParams<{ company_id: string; company_name: string }>();
    const navigate = useNavigate();

    const [isFetchSetting, setIsFetchSetting] = useState(false);
    const [loadingSubmit, setLoadingSubmit] = useState(false);
    const [successSubmit, setSuccessSubmit] = useState(false);
    const [openAddCategoryModal, setOpenAddCategoryModal] = useState(false)
    const [employeesOptions, setEmployeesOptions] = useState<EmployeeType[]>([]);
    const [employeeCategoriesOptions, setEmployeeCategoriesOptions] = useState<EmployeeCategory[]>([]);
    const [departementsOptions, setDepartementsOptions] = useState<DepartmentType[]>([]);
    const [postesOptions, setPostesOptions] = useState<PosteType[]>([]);
    const [natureContratOptions, setNatureContratOptions] = useState<NatureContratType[]>([]);
    const [banksOptions, setbanksOptions] = useState<BankType[]>([]);
    const [rubriqueOptions, setRubriqueOptions] = useState<RubriqueSalaireType[]>([]);
    const [diplomaLibelleOptions, setDiplomaLibelleOptions] = useState<DiplomeType[]>([]);
    const [modeDePaiementOptions, setModeDePaiementOptions] = useState<ModeDePaiementType[]>([]);
    const [elementsSalaire, setElementsSalaire] = useState<{ rubriqueId: number; montant: number }[]>([]);
    const [employeeDiplomas, setEmployeeDiplomas] = useState<{employeId?:number, diplomeId:number , denomination: string; anneeObtention: number }[]>([]);

    const contratFieldsGrouped = {
        employe: [
            {
                tag: "employeId",
                label: "Sélectionner un employé",
                input_type: "select",
                size: "col-span-6",
                required: true,
                options: employeesOptions.map((employe) => ({
                    value: employe.id.toString(),
                    label: employe.nom + " " + employe.prenom,
                })),
            },
            {
                tag: "categorieEmployeId",
                label: "Catégorie d'employé",
                input_type: "select",
                size: "col-span-6",
                required: true,
                options: employeeCategoriesOptions.map((category) => ({
                    value: category.id.toString(),
                    label: category.name,
                })),
            },
        ],

        contrat: [
            {
                tag: "mouvementContrat",
                label: "Mouvement de contrat",
                input_type: "select",
                size: "col-span-4",
                required: true,
                options: ["NOUVEAU", "RENOUVELLEMENT"].map(v => ({ label: v, value: v }))
            },
            {
                tag: "typeContrat",
                label: "Type de contrat",
                input_type: "select",
                size: "col-span-4",
                required: true,
                options: ["CDI", "CDD"].map(v => ({ label: v, value: v }))
            },
            {
                tag: "natureContratId",
                label: "Nature du contrat",
                input_type: "select",
                size: "col-span-4",
                required: true,
                options: natureContratOptions.map((nature) => ({
                    value: nature.id.toString(),
                    label: nature.libelle,
                })),
            },
        ],

        poste: [
            {
                tag: "departementId",
                label: "Département",
                input_type: "select",
                size: "col-span-6",
                required: false,
                options: departementsOptions.map((departement) => ({
                    value: departement.id.toString(),
                    label: departement.libelle,
                })),
            },
            {
                tag: "posteId",
                label: "Poste",
                input_type: "select",
                size: "col-span-6",
                required: true,
                options: postesOptions.map((poste) => ({
                    value: poste.id.toString(),
                    label: poste.libelle,
                })),
            },
        ],

        paiement: [
            {
                tag: "modeDePaiementId",
                label: "Mode de paiement",
                input_type: "select",
                size: "col-span-4",
                required: true,
                options: modeDePaiementOptions.map((mode) => ({
                    value: mode.id.toString(),
                    label: mode.libelle,
                })),
            },
            {
                tag: "banqueId",
                label: "Banque",
                input_type: "select",
                size: "col-span-4",
                required: true,
                options: banksOptions.map((bank) => ({
                    value: bank.id.toString(),
                    label: bank.name,
                })),
            },
            {
                tag: "numeroCompte",
                label: "Numéro de compte",
                input_type: "text",
                size: "col-span-4",
                required: true,
            },
        ],

        dates: [
            {
                tag: "dateDebut",
                label: "Date de début",
                input_type: "date",
                size: "col-span-3",
                required: true,
            },
            {
                tag: "dateFin",
                label: "Date de fin",
                input_type: "date",
                size: "col-span-3",
                required: true,
            },
            {
                tag: "debutEssai",
                label: "Date de début d'essai",
                input_type: "date",
                size: "col-span-3",
                required: true,
            },
            {
                tag: "finEssai",
                label: "Date de fin d'essai",
                input_type: "date",
                size: "col-span-3",
                required: true,
            },
        ],

        montants: [
            {
                tag: "aibContratEmploye",
                label: "AIB Contrat employé",
                input_type: "number",
                size: "col-span-4",
                required: true,
            },
            {
                tag: "cautionContratEmploye",
                label: "Caution contrat employé",
                input_type: "number",
                size: "col-span-4",
                required: true,
            },
            {
                tag: "transfertContratEmploye",
                label: "Transfert contrat employé",
                input_type: "number",
                size: "col-span-4",
                required: true,
            },
        ]
    }

    const form = useForm<ContratFormData>({
        resolver: zodResolver(contratSchema),
        defaultValues: contratdefaultValues,
    });

    // Surveiller le type de contrat
    const typeContrat = form.watch("typeContrat");

    // Effet pour nettoyer la date de fin quand on passe en CDI
    useEffect(() => {
        if (typeContrat === "CDI") {
            form.setValue("dateFin", undefined);
        }
    }, [typeContrat, form]);

    const fetchPostes = async (departementId: number) => {
        try {
            setLoadingSubmit(true);
            const [postes] = await Promise.all([
                apiService.get({ url: apiRoutes.admin.app.company.departement.poste.list_byCompany ,params:{departementId:departementId}}, { userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "", hasNoSuccessModal: true }),
            ]);
            setPostesOptions(postes.data || []);
        } catch (error) {
            if (error instanceof Error) {
                apiService.handleError(error.message, { hasNoFailureModal: true });
            }
        } finally {
            setLoadingSubmit(false);
        }
    };

    const fetchData = async () => {
        try {
            setIsFetchSetting(true);
             const [employees,  categories,departements,natureContrat,banks,rubriques,diplomes,modePaiements ] = await Promise.all([
                    apiService.get({ url: apiRoutes.admin.app.employee.list_without_contract }, { userToken:user?.type && user?.token? `${user.type} ${user.token}` : "", hasNoSuccessModal: true }),
                    apiService.get({ url: apiRoutes.admin.app.employee.categories.list }, { userToken: user?.token ?? '', hasNoSuccessModal: true }),
                    apiService.get({ url: apiRoutes.admin.app.company.departement.list_byCompany ,params:{companyId:company_id}}, {userToken:  user?.type && user?.token? `${user.type} ${user.token}` : "",hasNoSuccessModal: true}),
                    apiService.get({ url: apiRoutes.admin.app.contrat.natureContrat.list }, {userToken:  user?.type && user?.token? `${user.type} ${user.token}` : "",hasNoSuccessModal: true}),
                    apiService.get({ url: apiRoutes.admin.app.contrat.banks.list }, {userToken:  user?.type && user?.token? `${user.type} ${user.token}` : "",hasNoSuccessModal: true}),
                    apiService.get({ url: apiRoutes.admin.app.contrat.rubriques.list }, {userToken:  user?.type && user?.token? `${user.type} ${user.token}` : "",hasNoSuccessModal: true}),
                    apiService.get({ url: apiRoutes.admin.app.employee.diplomas.list }, {userToken:  user?.type && user?.token? `${user.type} ${user.token}` : "",hasNoSuccessModal: true}),
                    apiService.get({ url: apiRoutes.admin.app.contrat.modeDePaiement.list }, {userToken:  user?.type && user?.token? `${user.type} ${user.token}` : "",hasNoSuccessModal: true})
                 ]
             );
            setEmployeesOptions(employees.data || []);
            setEmployeeCategoriesOptions(categories.data || []);
            setDepartementsOptions(departements.data || []);
            setNatureContratOptions(natureContrat.data || []);
            setbanksOptions(banks.data || []);
            setRubriqueOptions(rubriques.data || []);
            setDiplomaLibelleOptions(diplomes.data || []);
            setModeDePaiementOptions(modePaiements.data || []);
        } catch (error) {
            if (error instanceof Error) {
                apiService.handleError(error.message, { hasNoFailureModal: true });
            }
        } finally {
            setIsFetchSetting(false);
        }
    };

    const onSubmit = async (data: ContratFormData) => {
        const dataToSubmit = {
           contratEmploye: {
               companyId: company_id,
               ...data,
           },
           diplomes: employeeDiplomas.map((diplome) => ({
                employeId: data.employeId,
                diplomeId: diplome.diplomeId,
                denomination: diplome.denomination,
                anneeObtention: diplome.anneeObtention,
           })),
           rubriques: elementsSalaire.map((element) => ({
                rubriqueId: element.rubriqueId,
                montant: element.montant,
           })),
       }

        try {
            setLoadingSubmit(true);
            await apiService.post({
                url: apiRoutes.admin.app.contrat.create,
                body: JSON.stringify(dataToSubmit),
                headers: {
                    "Content-Type": "application/json",
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
    }

    useEffect(() => {
        usePageTitleStore.getState().setTitle(`Ajouter un nouveau contrat à ${company_name}`, "Octroyez un nouveau contrat à un employé.", FileSignature as Icon);
        useCompanyStore.getState().setShowCompanySelect(false);
        fetchData();
    }, []);

    const selectedDepartementId = form.watch("departementId");

    useEffect(() => {
        if (selectedDepartementId) {
            const selectedDepartement = departementsOptions.find(
                departement => departement.id.toString() === selectedDepartementId?.toString()
            );
            if (selectedDepartement) {
                fetchPostes(selectedDepartement.id);
            }
        }
    }, [selectedDepartementId, departementsOptions]);

    return (
        <>
            <PageTitle title="Contrat" />
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
                            Contrat enregistré avec succès !
                        </h2>
                        <Button variant="outline" onClick={() => navigate(routeHelpers.dashboard.company.index)}>
                            OK
                        </Button>
                    </div>
                </div>
            )}
            {
                !isFetchSetting && (
                    <div className="space-y-1 p-5">
                        <div className="h-svh">
                            <Form {...form}>
                                <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-8 mb-12">
                                    {/* Informations de l'employé */}
                                    <section className="bg-white p-4 rounded-lg shadow-sm border">
                                        <h3 className="text-lg font-semibold text-gray-800 border-b pb-2">Informations de l'employé</h3>
                                        <div className="grid grid-cols-12 gap-4 mt-4">
                                            {contratFieldsGrouped.employe.map((field, index) => (
                                                <BindFormItem
                                                    key={index}
                                                    option={field as FieldOption}
                                                    form={form}
                                                    tag={field.tag}
                                                    readonly={false}
                                                    index={index}
                                                />
                                            ))}
                                        </div>
                                    </section>

                                    {/* Diplômes */}
                                    <section className="bg-white p-4 rounded-lg shadow-sm border">
                                        <div className="flex justify-between items-center border-b pb-2">
                                            <h3 className="text-lg font-semibold">Diplômes</h3>
                                            <AddEmployeeDiplomaModal
                                                diplomas={diplomaLibelleOptions}
                                                onSubmit={(newDiplome) => setEmployeeDiplomas((prev) => [...prev, newDiplome])}
                                            />
                                        </div>
                                        {employeeDiplomas.length > 0 ? (
                                            <div className="mt-4 overflow-x-auto">
                                                <table className="w-full text-sm text-left">
                                                    <thead>
                                                    <tr className="text-gray-600 font-medium border-b">
                                                        <th className="py-2">Libellé</th>
                                                        <th className="py-2">Intitulé</th>
                                                        <th className="py-2">Année</th>
                                                        <th className="py-2 text-right">Actions</th>
                                                    </tr>
                                                    </thead>
                                                    <tbody>
                                                    {employeeDiplomas.map((element, index) => {
                                                        const diplome = diplomaLibelleOptions.find(d => d.id === element.diplomeId);
                                                        return (
                                                            <tr key={index} className="border-b hover:bg-gray-50">
                                                                <td className="py-2">{diplome?.name || "Inconnu"}</td>
                                                                <td className="py-2">{element.denomination}</td>
                                                                <td className="py-2">{element.anneeObtention}</td>
                                                                <td className="py-2 text-right">
                                                                    <button
                                                                        type="button"
                                                                        onClick={() => setEmployeeDiplomas(prev => prev.filter((_, i) => i !== index))}
                                                                        className="text-red-500 hover:underline text-xs"
                                                                    >
                                                                        Supprimer
                                                                    </button>
                                                                </td>
                                                            </tr>
                                                        );
                                                    })}
                                                    </tbody>
                                                </table>
                                            </div>
                                        ) : (
                                            <p className="text-sm text-gray-500 mt-4 text-center">Aucun diplôme ajouté.</p>
                                        )}
                                    </section>

                                    {/* Détails du contrat */}
                                    <section className="bg-white p-4 rounded-lg shadow-sm border">
                                        <h3 className="text-lg font-semibold text-gray-800 border-b pb-2">Détails du contrat</h3>
                                        <div className="grid grid-cols-12 gap-4 mt-4">
                                            {contratFieldsGrouped.contrat.map((field, index) => (
                                                <BindFormItem
                                                    key={index}
                                                    option={field as FieldOption}
                                                    form={form}
                                                    tag={field.tag}
                                                    readonly={false}
                                                    index={index}
                                                />
                                            ))}
                                        </div>
                                    </section>

                                    {/* Poste */}
                                    <section className="bg-white p-4 rounded-lg shadow-sm border">
                                        <h3 className="text-lg font-semibold text-gray-800 border-b pb-2">Poste</h3>
                                        <div className="grid grid-cols-12 gap-4 mt-4">
                                            {contratFieldsGrouped.poste.map((field, index) => (
                                                <BindFormItem
                                                    key={index}
                                                    option={field as FieldOption}
                                                    form={form}
                                                    tag={field.tag}
                                                    readonly={false}
                                                    index={index}
                                                />
                                            ))}
                                        </div>
                                    </section>

                                    {/* Informations de paiement */}
                                    <section className="bg-white p-4 rounded-lg shadow-sm border">
                                        <h3 className="text-lg font-semibold text-gray-800 border-b pb-2">Informations de paiement</h3>
                                        <div className="grid grid-cols-12 gap-4 mt-4">
                                            {contratFieldsGrouped.paiement.map((field, index) => (
                                                <BindFormItem
                                                    key={index}
                                                    option={field as FieldOption}
                                                    form={form}
                                                    tag={field.tag}
                                                    readonly={false}
                                                    index={index}
                                                />
                                            ))}
                                        </div>
                                    </section>

                                    {/* Dates du contrat */}
                                    <section className="bg-white p-4 rounded-lg shadow-sm border">
                                        <h3 className="text-lg font-semibold text-gray-800 border-b pb-2">Dates du contrat</h3>
                                        <div className="grid grid-cols-12 gap-4 mt-4">
                                            {contratFieldsGrouped.dates
                                                .filter(field => typeContrat !== "CDI" || field.tag !== "dateFin")
                                                .map((field, index) => (
                                                    <BindFormItem
                                                        key={index}
                                                        option={field as FieldOption}
                                                        form={form}
                                                        tag={field.tag}
                                                        readonly={false}
                                                        index={index}
                                                    />
                                                ))}
                                            {typeContrat === "CDI" && (
                                                <div className="col-span-3 flex items-center text-sm text-gray-500">
                                                </div>
                                            )}
                                        </div>
                                    </section>

                                    {/* Montants et autres informations */}
                                    <section className="bg-white p-4 rounded-lg shadow-sm border">
                                        <h3 className="text-lg font-semibold text-gray-800 border-b pb-2">Montants et autres informations</h3>
                                        <div className="grid grid-cols-12 gap-4 mt-4">
                                            {contratFieldsGrouped.montants.map((field, index) => (
                                                <BindFormItem
                                                    key={index}
                                                    option={field as FieldOption}
                                                    form={form}
                                                    tag={field.tag}
                                                    readonly={false}
                                                    index={index}
                                                />
                                            ))}
                                        </div>
                                    </section>

                                    {/* Éléments de salaire */}
                                    <section className="bg-white p-4 rounded-lg shadow-sm border">
                                        <div className="flex justify-between items-center border-b pb-2">
                                            <h3 className="text-lg font-semibold">Éléments de salaire</h3>
                                            <AddElementSalaireModal
                                                rubriques={rubriqueOptions}
                                                isOpen={false}
                                                setIsOpen={() => {}}
                                                onSubmit={(element) => setElementsSalaire((prev) => [...prev, element])}
                                            />
                                        </div>
                                        {elementsSalaire.length > 0 ? (
                                            <div className="mt-4 overflow-x-auto">
                                                <table className="w-full text-sm text-left">
                                                    <thead>
                                                    <tr className="text-gray-600 font-medium border-b">
                                                        <th className="py-2">Rubrique</th>
                                                        <th className="py-2">Montant</th>
                                                        <th className="py-2 text-right">Actions</th>
                                                    </tr>
                                                    </thead>
                                                    <tbody>
                                                    {elementsSalaire.map((element, index) => {
                                                        const rubrique = rubriqueOptions.find(r => r.id === element.rubriqueId);
                                                        return (
                                                            <tr key={index} className="border-b hover:bg-gray-50">
                                                                <td className="py-2">{rubrique?.libelle || "Rubrique inconnue"}</td>
                                                                <td className="py-2">{element.montant.toLocaleString()} FCFA</td>
                                                                <td className="py-2 text-right">
                                                                    <button
                                                                        type="button"
                                                                        onClick={() => setElementsSalaire(prev => prev.filter((_, i) => i !== index))}
                                                                        className="text-red-500 hover:underline text-xs"
                                                                    >
                                                                        Supprimer
                                                                    </button>
                                                                </td>
                                                            </tr>
                                                        );
                                                    })}
                                                    </tbody>
                                                </table>
                                            </div>
                                        ): (
                                            <p className="text-sm text-gray-500 mt-4 text-center">Aucun diplôme ajouté.</p>
                                        )}
                                    </section>

                                    {/* Bouton de soumission */}
                                    <div className="pt-6 flex justify-end">
                                        <ButtonWithLoading
                                            type="submit"
                                            classList="text-white px-6 py-3 rounded-md shadow w-full sm:w-1/4"
                                            title="Enregistrer"
                                            loading={loadingSubmit}
                                        />
                                    </div>
                                </form>
                            </Form>

                            <AddEmployeeCategoryModal
                                isOpen={openAddCategoryModal}
                                setIsOpen={setOpenAddCategoryModal}
                                refresh={() => {}}
                            />
                        </div>
                    </div>
                )
            }
        </>
    );
}