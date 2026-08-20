import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Search, Columns, ArrowDownUp } from "lucide-react";
import { useEffect, useState } from "react";
import apiService from "@/api/apiService";
import apiRoutes from "@/api/apiRoutes";
import { useAuth } from "@/lib/auth";
import FormModal from "@/components/useful/form-modal";
import { Icon } from "@tabler/icons-react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { DynamicTable3 } from "@/components/tables/dynamic-table-3";
import DeleteModal from "@/components/useful/delete-modal";

type ColonneAffichageType = {
  id: number;
  libelle: string;
  description?: string;
};

export default function ColonneAffichageTab() {
  const { user } = useAuth();
  const [isLoading, setIsLoading] = useState(false);
  const [colonnes, setColonnes] = useState<ColonneAffichageType[]>([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [isOpenFormModal, setIsOpenFormModal] = useState(false);
  const [loadingSubmit, setLoadingSubmit] = useState(false);
  const [sortOrder, setSortOrder] = useState<"asc" | "desc">("asc");
  const [selectedColonne, setSelectedColonne] = useState<ColonneAffichageType | null>(null);
  const [isOpenDelete, setIsOpenDelete] = useState(false);

  const fetchData = async () => {
    try {
      setIsLoading(true);
      const res = await apiService.get({
        url: apiRoutes.admin.app.contrat.colonneAffichage.list
      }, {
        userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "",
        hasNoSuccessModal: true
      });
      setColonnes(res.data || []);
    } catch (error) {
      if (error instanceof Error) {
        apiService.handleError(error.message, { hasNoFailureModal: true });
      }
    } finally {
      setIsLoading(false);
    }
  };

  const colonneFields = [
    {
      tag: "libelle",
      label: "Libellé de la colonne",
      type: "text",
      placeholder: "Ex: GAIN, RETENUES LEGALES...",
      size: "col-span-12",
      required: true,
    },
    {
      tag: "description",
      label: "Description (optionnelle)",
      type: "text",
      placeholder: "Description de la colonne",
      size: "col-span-12",
      required: false,
    },
  ];

  const colonneSchema = z.object({
    libelle: z.string().min(1, "Le libellé est requis"),
    description: z.string().optional(),
  });

  const form = useForm<z.infer<typeof colonneSchema>>({
    resolver: zodResolver(colonneSchema),
    defaultValues: {
      libelle: "",
      description: "",
    },
  });

  const { reset } = form;

  const onSubmit = async (data: z.infer<typeof colonneSchema>) => {
    setLoadingSubmit(true);
    try {
      if (selectedColonne) {
        // Mise à jour
        await apiService.put({
          url: `${apiRoutes.admin.app.contrat.colonneAffichage.create}${selectedColonne.id}`,
          body: data
        }, {
          userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "",
          hasNoSuccessModal: false
        });
      } else {
        // Création
        await apiService.post({
          url: apiRoutes.admin.app.contrat.colonneAffichage.create,
          body: { libelle: data.libelle }
        }, {
          userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "",
          hasNoSuccessModal: false
        });
      }
      await fetchData();
      setIsOpenFormModal(false);
      reset();
      setSelectedColonne(null);
    } catch (error) {
      if (error instanceof Error) {
        apiService.handleError(error.message, { hasNoFailureModal: false });
      }
    } finally {
      setLoadingSubmit(false);
    }
  };

  const handleEdit = (colonne: ColonneAffichageType) => {
    setSelectedColonne(colonne);
    reset({
      libelle: colonne.libelle,
      description: colonne.description || ""
    });
    setIsOpenFormModal(true);
  };

  const handleDelete = (colonne: ColonneAffichageType) => {
    setSelectedColonne(colonne);
    setIsOpenDelete(true);
  };

  const handleConfirmDelete = async () => {
    if (!selectedColonne) return;
    
    try {
      setLoadingSubmit(true);
      await apiService.remove({
        url: `${apiRoutes.admin.app.contrat.colonneAffichage.create}${selectedColonne.id}`
      }, {
        userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "",
        hasNoSuccessModal: false
      });
      await fetchData();
    } catch (error) {
      if (error instanceof Error) {
        apiService.handleError(error.message);
      }
    } finally {
      setLoadingSubmit(false);
      setIsOpenDelete(false);
      setSelectedColonne(null);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const filteredColonnes = [...colonnes]
    .filter(c => c.libelle.toLowerCase().includes(searchQuery.toLowerCase()))
    .sort((a, b) => sortOrder === "asc" 
      ? a.libelle.localeCompare(b.libelle) 
      : b.libelle.localeCompare(a.libelle));

  const columns = [
    { 
      key: "libelle", 
      label: "Libellé",
      className: "font-medium"
    }
  ];

  return (
    <div className="flex flex-col gap-4">
      <DeleteModal
        title="⚠️ Supprimer cette colonne ?"
        description={`La colonne "${selectedColonne?.libelle}" sera définitivement supprimée.`}
        isOpen={isOpenDelete}
        isetIsOpen={setIsOpenDelete}
        isDeleteLoading={loadingSubmit}
        onDelete={handleConfirmDelete}
        onCancel={() => {
          setSelectedColonne(null);
          setIsOpenDelete(false);
        }}
      />

      <FormModal
        icon={Columns as Icon}
        title={selectedColonne ? "Modifier la colonne" : "Ajouter une colonne"}
        description={selectedColonne 
          ? "Modifiez les informations de la colonne d'affichage" 
          : "Ajoutez une nouvelle colonne d'affichage pour les bulletins de paie"}
        isOpen={isOpenFormModal}
        setIsOpen={setIsOpenFormModal}
        form={form}
        fields={colonneFields}
        onSubmit={onSubmit}
        isSubmitLoading={loadingSubmit}
        onClose={() => {
          reset();
          setIsOpenFormModal(false);
          setSelectedColonne(null);
        }}
      />

      <div className="flex items-center justify-between bg-background mx-5">
        <div>
          <h2 className="text-lg font-semibold">Colonnes d'affichage</h2>
          <p className="text-xs text-muted-foreground mb-4">
            Gérez les colonnes affichées dans les bulletins de paie
          </p>
        </div>
        <div className="flex gap-3">
          <div className="relative">
            <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
            <Input
              placeholder="Rechercher une colonne..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-8 w-[250px]"
            />
          </div>
          <Button
            variant="outline"
            size="sm"
            onClick={() => setSortOrder(prev => (prev === "asc" ? "desc" : "asc"))}
          >
            <ArrowDownUp className="h-4 w-4 mr-1" />
            {sortOrder === "asc" ? "A → Z" : "Z → A"}
          </Button>
          <Button
            variant="outline"
            size="sm"
            onClick={() => {
              setSelectedColonne(null);
              setIsOpenFormModal(true);
            }}
          >
            Ajouter une nouvelle
          </Button>
        </div>
      </div>

      <div className="m-5">
        <DynamicTable3
          columns={columns}
          data={filteredColonnes}
          isLoading={isLoading}
          onAdd={() => {
            setSelectedColonne(null);
            setIsOpenFormModal(true);
          }}
          onEdit={handleEdit}
          onDelete={handleDelete}
          onFilter={(query: string) => setSearchQuery(query)}
          filterPlaceholder="Rechercher une colonne..."
        />
      </div>
    </div>
  );
}