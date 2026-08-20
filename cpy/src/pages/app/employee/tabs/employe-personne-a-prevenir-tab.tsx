import { Button } from "@/components/ui/button.tsx";
import { useEffect, useState } from "react";
import { EmployeeType, PersonneAPrevenirType } from "@/types/employee/EmployeeType.ts";
import { useAuth } from "@/lib/auth.ts";
import apiRoutes from "@/api/apiRoutes.ts";
import apiService from "@/api/apiService.ts";
import {DynamicTable3} from "@/components/tables/dynamic-table-3.tsx";
import {UserPlus} from "lucide-react";
import FormModal from "@/components/useful/form-modal.tsx";
import {Icon} from "@tabler/icons-react";
import {useForm} from "react-hook-form";
import {personneAPrevenirScheme, PresonneAPrevenirDataForm} from "@/validators-forms/personne-a-prevenir-schema.ts";
import {zodResolver} from "@hookform/resolvers/zod";
import {z} from "zod";
import {Relation} from "@/types/UtilsTypes.ts";
import {form} from "framer-motion/client";
import DeleteModal from "@/components/useful/delete-modal.tsx";

interface EmployeeChildrenManagementProps {
    employe: EmployeeType;
}

export default function EmployePersonneAPrevenirTab({employe}: EmployeeChildrenManagementProps) {
    const { user ,logout} = useAuth();
    const [personnes, setPersonnes] = useState<PersonneAPrevenirType[]>([]);
    const [personnesAdapted, setPersonnesAdapted] = useState<any[]>([]);
    const [isOpenPersonneAddModal , setIsOpenPersonneAddModal]=useState(false)
    const [isOpenPersonneDeleteModal , setIsOpenPersonneDeleteModal]=useState(false)
    const [loadingData, setLoadingData] = useState(false);
    const [lienParents,setLienParents]=useState<Relation[]>([])
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [selectedPersonne, setSelectedPersonne] = useState<PersonneAPrevenirType | null>(null);
    const [isFetchSetting,setIsFetchSetting]=useState(false)

    // Sécurité : ne rien afficher si employe ou employe.id n'est pas défini
    if (!employe?.id) return null;

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

    const handleEdit = (row: { id: number }) => {
        const personne = personnes.find(p => p.id === row.id);
        if (personne) {
            setSelectedPersonne(personne)
            const lienp = lienParents.find(l => l.libelle === personne.lienParenteLibelle);
            formPersonneAPrevenir.reset({
                ...personne,
                lienParente: lienp?.id.toString()??"0", // transforme l'objet en ID attendu par le select
            });
            setIsOpenPersonneAddModal(true);
        }
    };

    const handleDelete = (row: { id: number }) => {
        const personne = personnes.find(p => p.id === row.id);
        if (personne) {
            setSelectedPersonne(personne)
            setIsOpenPersonneDeleteModal(true)
        }
    };

    const handleOnCreate = async (data:z.infer<typeof personneAPrevenirScheme>) =>{
        setIsSubmitting(true);
        try {

            await apiService.post(
                {
                    url: apiRoutes.admin.app.employee.personnesAPrevenir.create,
                    body: JSON.stringify({ ...data, lienParente: { id: data.lienParente },employe: { id: employe.id } }),
                    headers: { "Content-Type": "application/json" },
                },
                {
                    userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                    hasNoSuccessModal: false,
                    onTokenExpired: logout,
                }
            );
            resetPersonneAPrevenir()
            setPersonnes([]);
            setPersonnesAdapted([]);
            fetchPersonne();
            setIsOpenPersonneAddModal(false)

        } catch (error) {
            if (error instanceof Error) {
                apiService.handleError(error.message, { form });
            }
        } finally {
            setIsSubmitting(false);
        }


    }

    const handleOnUpdate = async (data:z.infer<typeof personneAPrevenirScheme>) =>{
        setIsSubmitting(true);
        try {

            await apiService.put(
                {
                    url: apiRoutes.admin.app.employee.personnesAPrevenir.update,
                    params: { id: selectedPersonne?.id },
                    body: JSON.stringify({ ...data, lienParente: { id: data.lienParente },employe: { id: employe.id } }),
                    headers: { "Content-Type": "application/json" },
                },
                {
                    userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                    hasNoSuccessModal: false,
                    onTokenExpired: logout,
                }
            );
            resetPersonneAPrevenir()
            setSelectedPersonne(null)
            setPersonnes([]);
            setPersonnesAdapted([]);
            await fetchData();
            setIsOpenPersonneAddModal(false)
        } catch (error) {
            if (error instanceof Error) {
                apiService.handleError(error.message, { form });
            }
        }
        finally {
            setIsSubmitting(false);
        }
    }

    const handleOnDelete = async () => {
        if (selectedPersonne) {
            setIsSubmitting(true);
            try {
                await apiService.remove(
                    {
                        url: apiRoutes.admin.app.employee.personnesAPrevenir.delete,
                        params: { id: selectedPersonne?.id },
                    },
                    {
                        userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                        hasNoSuccessModal: false,
                        onTokenExpired: logout,
                    }
                );
                setIsOpenPersonneDeleteModal(false);
                setPersonnes([]);
                setPersonnesAdapted([]);
                fetchPersonne();

            } catch (error) {
                if (error instanceof Error) {
                    apiService.handleError(error.message, { hasNoFailureModal: false });
                }
            } finally {
                setIsSubmitting(false);
            }
        }
    };

    const fetchData = async () => {
        try {
            setIsFetchSetting(true);
            const [lien_parents] = await Promise.all([
                apiService.get(
                    { url: apiRoutes.admin.app.employee.lienParent.list },
                    { userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "", hasNoSuccessModal: true }
                ),
            ]);
            setLienParents(lien_parents.data || []);
        } catch (error) {
            if (error instanceof Error) {
                apiService.handleError(error.message, { hasNoFailureModal: true });
            }
        } finally {
            setIsFetchSetting(false);
        }
    };
    
    const fetchPersonne = async () => {
        try {
            setLoadingData(true);
            const response = await apiService.get(
                {
                    url: `${apiRoutes.admin.app.employee.personnesAPrevenir.list}/${employe.id}`,
                },
                {
                    userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
                    hasNoSuccessModal: true,
                }
            );
            const data: PersonneAPrevenirType[] = response.data;
            setPersonnes(data);
            setPersonnesAdapted(
                data.map((p) => ({
                    id: p.id,
                    nomPrenom: p.nomPrenom,
                    telephone: p.telephone,
                    adresse: p.adresse,
                    email: p.email,
                    lienParenteLibelle: p.lienParenteLibelle,
                }))
            );
        } catch (error) {
            console.error("Erreur de chargement des personnes à prévenir", error);
        } finally {
            setLoadingData(false);
        }
    };

    useEffect(() => {
        setPersonnes([]);
        setPersonnesAdapted([]);
        fetchPersonne();
    }, [employe]);

    useEffect(() => {
        fetchData();
    }, []);

    return (
        <div className="font-inter space-y-3">
            <FormModal
                icon={UserPlus as Icon}
                title={selectedPersonne ? "Modifier une personne à prévenir" : "Ajouter une personne à prévenir"}
                description={selectedPersonne ? "Remplissez les informations pour modifier une personne à prévenir" : "Remplissez les informations pour ajouter une personne à prévenir"}
                isOpen={isOpenPersonneAddModal}
                setIsOpen={setIsOpenPersonneAddModal}
                form={formPersonneAPrevenir}
                fields={personneAPrevenirFields}
                onSubmit={selectedPersonne ? handleOnUpdate : handleOnCreate}
                isSubmitLoading={isSubmitting}
                onSubmitSuccess={()=>{
                    setPersonnes([]);
                    setPersonnesAdapted([]);
                    fetchPersonne();
                }}
                loading={isFetchSetting}
                onClose={() => {
                    resetPersonneAPrevenir();
                    setIsOpenPersonneAddModal(false);
                }}
            />

            <DeleteModal
                title={"⚠️ Etes-vous sur de vouloir supprimer ?"}
                 description={`Cette action est irréversible. En cliquant sur supprimer, vous supprimerez ${selectedPersonne?.nomPrenom ?? "cette personne à prévenir"} de cette list et ses informations seront perdues.`}
                 isOpen={isOpenPersonneDeleteModal}
                 isetIsOpen={setIsOpenPersonneDeleteModal}
                 isDeleteLoading={isSubmitting}
                 onDelete={handleOnDelete}
                 onCancel={()=>{
                     setSelectedPersonne(null)
                     setIsOpenPersonneDeleteModal(false)
                 }}
            />

            <div className="flex items-center justify-between bg-background px-4 ">
                <div>
                    <h2 className="text-lg font-semibold">Liste des personnes à prévenir pour l'employé</h2>
                    <p className="text-xs text-muted-foreground">
                        Vous pouvez ajouter ou supprimer les personnes à prévenir associées à cet employé.
                    </p>
                </div>
                <div className="mt-2">
                    <Button
                        size="sm"
                        onClick={handleAddPersonne}>
                        <UserPlus className="w-4 h-4"/>
                        Ajouter une personne à prévenir
                    </Button>
                </div>
            </div>
            <div className="bg-background overflow-hidden rounded-md px-4">
                <DynamicTable3
                    columns={[
                        { key: "nomPrenom", label: "Nom & Prénom" },
                        { key: "telephone", label: "Téléphone" },
                        { key: "adresse", label: "Adresse" },
                        { key: "email", label: "Email" },
                        { key: "lienParenteLibelle", label: "Lien de parenté" },
                    ]}
                    data={personnesAdapted}
                    onEdit={handleEdit}
                    onDelete={ handleDelete}
                    isLoading={loadingData}
                />
            </div>
        </div>
    );
}