import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { RefreshCw, Search, Landmark } from "lucide-react";
import { useEffect, useState, useMemo } from "react";
import FormModal from "@/components/useful/form-modal";
import apiService from "@/api/apiService";
import apiRoutes from "@/api/apiRoutes";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useAuth } from "@/lib/auth";
import { Icon } from "@tabler/icons-react";
import { DynamicTable3 } from "@/components/tables/dynamic-table-3";
import DeleteModal from "@/components/useful/delete-modal";
import { RubriquePaieSchema, RubriquePaieFormValues } from "./validatorRubriquePaie";

interface RubriquePaie {
  id: number;
  libelle: string;
  nature: "AVANTAGE" | "RETENUE";
  rubriqueImposable: "OUI" | "NON";
  niveauAffichage?: {
    id: number;
    libelle: string;
  };
  colonneAffichage?: {
    id: number;
    libelle: string;
  };
  calculeAuProrataTempsTravail: boolean;
  calculeAPartirCoefficient: "OUI" | "NON";
  calculeAPartirSalaireBrut: "OUI" | "NON";
  coefficient: number;
  partPatronale: "OUI" | "NON";
  rubriqueSysteme: "OUI" | "NON";
  numeroOrdre: number;
}

interface SelectOption {
  value: number;
  label: string;
}

export default function RubriqueSettingTab() {
  const { user, logout } = useAuth();
  const [isFetchSetting, setIsFetchSetting] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [niveauAffichageOptions, setNiveauAffichageOptions] = useState<SelectOption[]>([]);
  const [colonneAffichageOptions, setColonneAffichageOptions] = useState<SelectOption[]>([]);
  const [rubriquePaies, setRubriquePaies] = useState<RubriquePaie[]>([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [loadingSubmit, setLoadingSubmit] = useState(false);
  const [isOpenFormModal, setIsOpenFormModal] = useState(false);
  const [selectedRubrique, setSelectedRubrique] = useState<RubriquePaie | null>(null);
  const [isOpenDelete, setIsOpenDelete] = useState(false);

  const rubriqueFild = useMemo(() => [
    {
      tag: "libelle",
      label: "Libellé de la rubrique",
      input_type: "text",
      size: "col-span-6",
      required: true,
    },
    {
      tag: "nature",
      label: "Nature de la rubrique",
      input_type: "select",
      size: "col-span-6",
      required: true,
      options: [
        { value: "AVANTAGE", label: "AVANTAGE" },
        { value: "RETENUE", label: "RETENUE" },
      ],
    },
    {
      tag: "numeroOrdre",
      label: "Numéro d'ordre",
      input_type: "number",
      size: "col-span-6",
      required: false,
    },
    {
      tag: "niveauAffichage.id",
      label: "Niveau d'affichage",
      input_type: "select",
      size: "col-span-6",
      required: true,
      options: niveauAffichageOptions,
      transformValue: (value: string) => ({ id: Number(value) }), // ✅ conversion string → object
    },
    {
      tag: "colonneAffichage.id",
      label: "Colonne d'affichage",
      input_type: "select",
      size: "col-span-6",
      required: true,
      options: colonneAffichageOptions,
      transformValue: (value: string) => ({ id: Number(value) }), // ✅ conversion string → object
    },
    {
      tag: "rubriqueImposable",
      label: "Imposable",
      input_type: "select",
      size: "col-span-6",
      required: true,
      options: [
        { value: "OUI", label: "Oui" },
        { value: "NON", label: "Non" },
      ],
    },
    {
      tag: "partPatronale",
      label: "Part patronale",
      input_type: "select",
      size: "col-span-6",
      required: true,
      options: [
        { value: "OUI", label: "Oui" },
        { value: "NON", label: "Non" },
      ],
    },
    {
      tag: "calculeAuProrataTempsTravail",
      label: "Calcul au prorata temps travail",
      input_type: "select",
      size: "col-span-6",
      required: true,
      options: [
        { value: "true", label: "Oui" },
        { value: "false", label: "Non" },
      ],
      transformValue: (value: string) => value === "true", // ✅ string → boolean
    },
    {
      tag: "calculeAPartirCoefficient",
      label: "Calculé à partir coefficient",
      input_type: "select",
      size: "col-span-6",
      required: true,
      options: [
        { value: "OUI", label: "Oui" },
        { value: "NON", label: "Non" },
      ],
    },
    {
      tag: "coefficient",
      label: "Coefficient",
      input_type: "number",
      size: "col-span-6",
      required: false,
    },
    {
      tag: "calculeAPartirSalaireBrut",
      label: "Calculé à partir salaire brut",
      input_type: "select",
      size: "col-span-6",
      required: true,
      options: [
        { value: "OUI", label: "Oui" },
        { value: "NON", label: "Non" },
      ],
    },
  ], [niveauAffichageOptions, colonneAffichageOptions]);
  
  const form = useForm<RubriquePaieFormValues>({
    resolver: zodResolver(RubriquePaieSchema),
    defaultValues: {
      numeroOrdre: 0,
      coefficient: 0,
      calculeAuProrataTempsTravail: false,
      niveauAffichage: { id: 0 },
      colonneAffichage: { id: 0 },
    },
  });

  const { reset } = form;
  
  const fetchSetting = async () => {
    try {
      setIsFetchSetting(true);
      
      const niveauxResponse = await apiService.get(
        { url: apiRoutes.admin.app.contrat.niveauAffichage.list },
        { userToken: `${user?.type} ${user?.token}` }
      );
      setNiveauAffichageOptions(
        niveauxResponse.data.map((item: any) => ({ 
          value: item.id.toString(),  // ✅ value en string
          label: item.libelle 
        }))
      );
  
      const colonnesResponse = await apiService.get(
        { url: apiRoutes.admin.app.contrat.colonneAffichage.list },
        { userToken: `${user?.type} ${user?.token}` }
      );
      setColonneAffichageOptions(
        colonnesResponse.data.map((item: any) => ({ 
          value: item.id.toString(),  // ✅ value en string
          label: item.libelle 
        }))
      );
  
    } catch (error) {
      if (error instanceof Error) {
        apiService.handleError(error.message, { hasNoFailureModal: true });
      }
    } finally {
      setIsFetchSetting(false);
    }
  };
  
  const fetchData = async () => {
    try {
      setIsLoading(true);
      const res = await apiService.get(
        { url: apiRoutes.admin.app.contrat.rubriques.list },
        { userToken: `${user?.type} ${user?.token}` }
      );
      setRubriquePaies(res.data || []);
    } catch (error) {
      if (error instanceof Error) {
        apiService.handleError(error.message, { hasNoFailureModal: true });
      }
    } finally {
      setIsLoading(false);
    }
  };

  const handleEdit = (rubrique: RubriquePaie) => {
    setSelectedRubrique(rubrique);
    reset({
      ...rubrique,
      niveauAffichage: { id: rubrique.niveauAffichage?.id || 0 },
      colonneAffichage: { id: rubrique.colonneAffichage?.id || 0 },
    });
    setIsOpenFormModal(true);
  };

  const handleDelete = async (rubrique: RubriquePaie) => {
    setSelectedRubrique(rubrique);
    setIsOpenDelete(true);
  };

  const handleConfirmDelete = async () => {
    if (!selectedRubrique) return;
    
    try {
      setLoadingSubmit(true);
      await apiService.remove(
        { url: `${apiRoutes.admin.app.contrat.rubriques.create}${selectedRubrique.id}` },
        { userToken: `${user?.type} ${user?.token}` }
      );
      fetchData();
    } catch (error) {
      if (error instanceof Error) {
        apiService.handleError(error.message);
      }
    } finally {
      setLoadingSubmit(false);
      setIsOpenDelete(false);
      setSelectedRubrique(null);
    }
  };

  const onSubmit = async (data: RubriquePaieFormValues) => {
    try {
      setLoadingSubmit(true);
      
      const apiCall = selectedRubrique 
        ? apiService.put(
            {
              url: `${apiRoutes.admin.app.contrat.rubriques.create}${selectedRubrique.id}`,
              body: JSON.stringify(data),
              headers: { "Content-Type": "application/json" },
            },
            { userToken: `${user?.type} ${user?.token}`, onTokenExpired: logout }
          )
        : apiService.post(
            {
              url: apiRoutes.admin.app.contrat.rubriques.create,
              body: JSON.stringify(data),
              headers: { "Content-Type": "application/json" },
            },
            { userToken: `${user?.type} ${user?.token}`, onTokenExpired: logout }
          );

      await apiCall;
      reset();
      setIsOpenFormModal(false);
      setSelectedRubrique(null);
      fetchData();
    } catch (error) {
      if (error instanceof Error) {
        apiService.handleError(error.message, { form });
      }
    } finally {
      setLoadingSubmit(false);
    }
  };

  useEffect(() => {
    fetchSetting();
    fetchData();
  }, []);

  const columns = useMemo(() => [
    { key: "libelle", label: "Libellé" },
    { key: "nature", label: "Nature" },
    {
      key: "niveauAffichage.libelle",
      label: "Niveau Affichage",
    },
    {
      key: "colonneAffichage.libelle",
      label: "Colonne affichage",
    },
    {
      key: "rubriqueSysteme",
      label: "Système",
      render: (value: "OUI" | "NON") => value,
    },
    {
      key: "partPatronale",
      label: "Part patronale",
      render: (value: "OUI" | "NON") => value,
    },
    {
      key: "coefficient",
      label: "Coefficient",
      render: (value: number) => value.toFixed(2),
    },
    {
      key: "rubriqueImposable",
      label: "Imposable",
      render: (value: "OUI" | "NON") => value,
    },
    {
      key: "numeroOrdre",
      label: "Ordre",
    },
    {
      key: "calculeAuProrataTempsTravail",
      label: "Prorata",
      render: (value: boolean) => value ? "OUI" : "NON",
    },
    {
      key: "calculeAPartirCoefficient",
      label: "Calc. Coefficient",
      render: (value: "OUI" | "NON") => value,
    },
    {
      key: "calculeAPartirSalaireBrut",
      label: "Calc. Salaire Brut",
      render: (value: "OUI" | "NON") => value,
    },
  ], []);

  const filteredRubriques = useMemo(() => (
    rubriquePaies.filter(r => r.libelle.toLowerCase().includes(searchQuery.toLowerCase()))
  ), [rubriquePaies, searchQuery]);

  return (
    <div className="flex flex-col gap-4">
      <DeleteModal
        title={"⚠️ Êtes-vous sûr de vouloir supprimer cette rubrique ?"}
        description={`La rubrique "${selectedRubrique?.libelle}" sera définitivement supprimée. Cette action est irréversible.`}
        isOpen={isOpenDelete}
        isetIsOpen={setIsOpenDelete}
        isDeleteLoading={loadingSubmit}
        onDelete={handleConfirmDelete}
        onCancel={() => {
          setSelectedRubrique(null);
          setIsOpenDelete(false);
        }}
      />
      <FormModal
        icon={Landmark as Icon}
        className="w-[800px] max-w-full"
        title={selectedRubrique ? "Modifier la rubrique" : "Ajouter une nouvelle rubrique"}
        description={selectedRubrique 
          ? "Modifiez les informations de la rubrique" 
          : "Remplissez le formulaire pour ajouter une rubrique"}
        isOpen={isOpenFormModal}
        setIsOpen={setIsOpenFormModal}
        form={form}
        fields={rubriqueFild}
        onSubmit={onSubmit}
        isSubmitLoading={loadingSubmit}
        onClose={() => {
          reset();
          setSelectedRubrique(null);
          setIsOpenFormModal(false);
        }}
        loading={isFetchSetting}
      />

      <div className="flex items-center justify-between bg-background mx-5">
        <div>
          <h2 className="text-lg font-semibold">Rubriques de paie</h2>
          <p className="text-xs text-muted-foreground mb-4">
            Gérez les rubriques de paie utilisées dans votre système.
          </p>
        </div>
        <div className="flex gap-3">
          <div className="relative">
            <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
            <Input
              placeholder="Rechercher une rubrique..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-8 w-[250px]"
            />
          </div>
          <Button variant="outline" size="sm" onClick={fetchData} disabled={isLoading}>
            <RefreshCw className={`h-4 w-4 mr-1 ${isLoading && "animate-spin"}`} />
            Rafraîchir
          </Button>
          <Button 
            variant="outline" 
            size="sm" 
            onClick={() => {
              setSelectedRubrique(null);
              setIsOpenFormModal(true);
            }}
          >
            Ajouter un nouveau
          </Button>
        </div>
      </div>

      <div className="m-5">
        <DynamicTable3
          columns={columns}
          data={filteredRubriques}
          isLoading={isLoading}
          onAdd={() => {
            setSelectedRubrique(null);
            setIsOpenFormModal(true);
          }}
          onEdit={handleEdit}
          onDelete={handleDelete}
          onFilter={(query: string) => setSearchQuery(query)}
          filterPlaceholder="Rechercher une rubrique..."
        />
      </div>
    </div>
  );
}