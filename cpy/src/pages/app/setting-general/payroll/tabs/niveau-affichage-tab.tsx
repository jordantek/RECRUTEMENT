import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Search, Layers, ArrowDownUp } from "lucide-react";
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

type NiveauAffichageType = {
  id: number;
  libelle: string;
  description?: string;
};

export default function NiveauAffichageTab() {
  const { user } = useAuth();
  const [isLoading, setIsLoading] = useState(false);
  const [niveaux, setNiveaux] = useState<NiveauAffichageType[]>([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [isOpenFormModal, setIsOpenFormModal] = useState(false);
  const [loadingSubmit, setLoadingSubmit] = useState(false);
  const [sortOrder, setSortOrder] = useState<"asc" | "desc">("asc");
  const [selectedNiveau, setSelectedNiveau] = useState<NiveauAffichageType | null>(null);
  const [isOpenDelete, setIsOpenDelete] = useState(false);

  const fetchData = async () => {
    try {
      setIsLoading(true);
      const res = await apiService.get({
        url: apiRoutes.admin.app.contrat.niveauAffichage.list
      }, {
        userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "",
        hasNoSuccessModal: true
      });
      setNiveaux(res.data || []);
    } catch (error) {
      if (error instanceof Error) {
        apiService.handleError(error.message, { hasNoFailureModal: true });
      }
    } finally {
      setIsLoading(false);
    }
  };

  const niveauFields = [
    {
      tag: "libelle",
      label: "Libellé du niveau",
      type: "text",
      placeholder: "Ex: AVANT SALAIRE BRUT...",
      size: "col-span-12",
      required: true,
    },
    {
      tag: "description",
      label: "Description (optionnelle)",
      type: "text",
      placeholder: "Description du niveau d'affichage",
      size: "col-span-12",
      required: false,
    },
  ];

  const niveauSchema = z.object({
    libelle: z.string().min(1, "Le libellé est requis"),
    description: z.string().optional(),
  });

  const form = useForm<z.infer<typeof niveauSchema>>({
    resolver: zodResolver(niveauSchema),
    defaultValues: {
      libelle: "",
      description: "",
    },
  });

  const { reset } = form;

  const onSubmit = async (data: z.infer<typeof niveauSchema>) => {
    setLoadingSubmit(true);
    try {
      if (selectedNiveau) {
        await apiService.put({
          url: `${apiRoutes.admin.app.contrat.niveauAffichage.create}${selectedNiveau.id}`,
          body: data
        }, {
          userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "",
          hasNoSuccessModal: false
        });
      } else {
        await apiService.post({
          url: apiRoutes.admin.app.contrat.niveauAffichage.create,
          body: data
        }, {
          userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "",
          hasNoSuccessModal: false
        });
      }
      await fetchData();
      setIsOpenFormModal(false);
      reset();
      setSelectedNiveau(null);
    } catch (error) {
      if (error instanceof Error) {
        apiService.handleError(error.message, { hasNoFailureModal: false });
      }
    } finally {
      setLoadingSubmit(false);
    }
  };

  const handleEdit = (niveau: NiveauAffichageType) => {
    setSelectedNiveau(niveau);
    reset({
      libelle: niveau.libelle,
      description: niveau.description || ""
    });
    setIsOpenFormModal(true);
  };

  const handleDelete = (niveau: NiveauAffichageType) => {
    setSelectedNiveau(niveau);
    setIsOpenDelete(true);
  };

  const handleConfirmDelete = async () => {
    if (!selectedNiveau) return;
    
    try {
      setLoadingSubmit(true);
      await apiService.remove({
        url: `${apiRoutes.admin.app.contrat.niveauAffichage.create}${selectedNiveau.id}`
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
      setSelectedNiveau(null);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const filteredNiveaux = [...niveaux]
    .filter(n => n.libelle.toLowerCase().includes(searchQuery.toLowerCase()))
    .sort((a, b) => sortOrder === "asc" 
      ? a.libelle.localeCompare(b.libelle) 
      : b.libelle.localeCompare(a.libelle));

  const columns = [
    { 
      key: "libelle", 
      label: "Niveau d'affichage",
      className: "font-medium"
    }
  ];

  return (
    <div className="flex flex-col gap-4">
      <DeleteModal
        title="⚠️ Supprimer ce niveau ?"
        description={`Le niveau "${selectedNiveau?.libelle}" sera définitivement supprimé.`}
        isOpen={isOpenDelete}
        isetIsOpen={setIsOpenDelete}
        isDeleteLoading={loadingSubmit}
        onDelete={handleConfirmDelete}
        onCancel={() => {
          setSelectedNiveau(null);
          setIsOpenDelete(false);
        }}
      />

      <FormModal
        icon={Layers as Icon}
        title={selectedNiveau ? "Modifier le niveau" : "Ajouter un niveau"}
        description={selectedNiveau 
          ? "Modifiez les informations du niveau d'affichage" 
          : "Ajoutez un nouveau niveau d'affichage pour les bulletins de paie"}
        isOpen={isOpenFormModal}
        setIsOpen={setIsOpenFormModal}
        form={form}
        fields={niveauFields}
        onSubmit={onSubmit}
        isSubmitLoading={loadingSubmit}
        onClose={() => {
          reset();
          setIsOpenFormModal(false);
          setSelectedNiveau(null);
        }}
      />

      <div className="flex items-center justify-between bg-background mx-5">
        <div>
          <h2 className="text-lg font-semibold">Niveaux d'affichage</h2>
          <p className="text-xs text-muted-foreground mb-4">
            Gérez les niveaux d'affichage dans les bulletins de paie
          </p>
        </div>
        <div className="flex gap-3">
          <div className="relative">
            <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
            <Input
              placeholder="Rechercher un niveau..."
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
              setSelectedNiveau(null);
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
          data={filteredNiveaux}
          isLoading={isLoading}
          onAdd={() => {
            setSelectedNiveau(null);
            setIsOpenFormModal(true);
          }}
          onEdit={handleEdit}
          onDelete={handleDelete}
          onFilter={(query: string) => setSearchQuery(query)}
          filterPlaceholder="Rechercher un niveau..."
        />
      </div>
    </div>
  );
}