import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Search, Briefcase, ArrowDownUp } from "lucide-react";
import { useEffect, useState } from "react";
import apiService from "@/api/apiService";
import apiRoutes from "@/api/apiRoutes";
import { useAuth } from "@/lib/auth";
import FormModal from "@/components/useful/form-modal";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { DynamicTable3 } from "@/components/tables/dynamic-table-3";
import DeleteModal from "@/components/useful/delete-modal";
import { Icon } from "@tabler/icons-react";

type DomaineType = {
  id: string;
  name: string;
  description: string;
};

export default function DomaineSettingTab() {
  const { user } = useAuth();
  const [domaines, setDomaines] = useState<DomaineType[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [isOpenFormModal, setIsOpenFormModal] = useState(false);
  const [loadingSubmit, setLoadingSubmit] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [sortOrder, setSortOrder] = useState<"asc" | "desc">("asc");
  const [selectedDomaine, setSelectedDomaine] = useState<DomaineType | null>(null);
  const [isOpenDelete, setIsOpenDelete] = useState(false);

  const fetchData = async () => {
    setIsLoading(true);
    try {
      const res = await apiService.get({
        url: apiRoutes.admin.app.employee.areaActivity.list,
      }, {
        userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "",
        hasNoSuccessModal: true,
      });
      setDomaines(res.data || []);
    } catch (error) {
      if (error instanceof Error) {
        apiService.handleError(error.message, { hasNoFailureModal: true });
      }
    } finally {
      setIsLoading(false);
    }
  };

  const domaineFields = [
    {
      tag: "name",
      label: "Nom du domaine",
      type: "text",
      placeholder: "Ex: Informatique",
      size: "col-span-12",
      required: true,
    },
    {
      tag: "description",
      label: "Description",
      type: "text",
      placeholder: "Ex: Technologies de l'information",
      size: "col-span-12",
      required: false,
    },
  ];

  const domaineSchema = z.object({
    name: z.string().min(1, "Le nom est requis"),
    description: z.string().optional(),
  });

  const form = useForm<z.infer<typeof domaineSchema>>({
    resolver: zodResolver(domaineSchema),
    defaultValues: {
      name: "",
      description: "",
    },
  });

  const { reset } = form;

  const onSubmit = async (data: z.infer<typeof domaineSchema>) => {
    setLoadingSubmit(true);
    try {
      if (selectedDomaine) {
        // Mise à jour
        await apiService.put({
          url: `${apiRoutes.admin.app.employee.areaActivity.update}${selectedDomaine.id}`,
          body: data,
        }, {
          userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "",
          hasNoSuccessModal: false,
        });
      } else {
        // Création
        await apiService.post({
          url: apiRoutes.admin.app.employee.areaActivity.create,
          body: data,
        }, {
          userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "",
          hasNoSuccessModal: false,
        });
      }
      await fetchData();
      reset();
      setIsOpenFormModal(false);
      setSelectedDomaine(null);
    } catch (error) {
      if (error instanceof Error) {
        apiService.handleError(error.message, { hasNoFailureModal: false });
      }
    } finally {
      setLoadingSubmit(false);
    }
  };

  const handleEdit = (domaine: DomaineType) => {
    setSelectedDomaine(domaine);
    reset({
      name: domaine.name,
      description: domaine.description
    });
    setIsOpenFormModal(true);
  };

  const handleDelete = (domaine: DomaineType) => {
    setSelectedDomaine(domaine);
    setIsOpenDelete(true);
  };

  const handleConfirmDelete = async () => {
    if (!selectedDomaine) return;
    
    try {
      setLoadingSubmit(true);
      await apiService.remove({
        url: `${apiRoutes.admin.app.employee.areaActivity.delete}${selectedDomaine.id}`,
      }, {
        userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "",
        hasNoSuccessModal: false,
      });
      await fetchData();
    } catch (error) {
      if (error instanceof Error) {
        apiService.handleError(error.message);
      }
    } finally {
      setLoadingSubmit(false);
      setIsOpenDelete(false);
      setSelectedDomaine(null);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const filteredDomaines = [...domaines]
    .filter(d => 
      d.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      d.description.toLowerCase().includes(searchQuery.toLowerCase())
    )
    .sort((a, b) => 
      sortOrder === "asc" 
        ? a.name.localeCompare(b.name) 
        : b.name.localeCompare(a.name)
    );

  const columns = [
    { 
      key: "name", 
      label: "Nom",
      className: "font-medium"
    },
    { 
      key: "description", 
      label: "Description",
      className: "text-muted-foreground"
    },
  ];

  return (
    <div className="flex flex-col gap-4">
      <DeleteModal
        title="⚠️ Supprimer ce domaine ?"
        description={`Le domaine "${selectedDomaine?.name}" sera définitivement supprimé.`}
        isOpen={isOpenDelete}
        isetIsOpen={setIsOpenDelete}
        isDeleteLoading={loadingSubmit}
        onDelete={handleConfirmDelete}
        onCancel={() => {
          setSelectedDomaine(null);
          setIsOpenDelete(false);
        }}
      />

      <FormModal
        icon={Briefcase as Icon}
        title={selectedDomaine ? "Modifier le domaine" : "Ajouter un domaine"}
        description={selectedDomaine 
          ? "Modifiez les informations du domaine" 
          : "Renseignez les informations du nouveau domaine."}
        isOpen={isOpenFormModal}
        setIsOpen={setIsOpenFormModal}
        form={form}
        fields={domaineFields}
        onSubmit={onSubmit}
        isSubmitLoading={loadingSubmit}
        onClose={() => {
          reset();
          setIsOpenFormModal(false);
          setSelectedDomaine(null);
        }}
      />

      <div className="flex items-center justify-between bg-background mx-5">
        <div>
          <h2 className="text-lg font-semibold">Domaines d'activité</h2>
          <p className="text-xs text-muted-foreground mb-4">
            Gérez les domaines d'activité disponibles.
          </p>
        </div>
        <div className="flex gap-3">
          <div className="relative">
            <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
            <Input
              placeholder="Rechercher un domaine..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-8 w-[250px]"
            />
          </div>
          <Button
            variant="outline"
            size="sm"
            onClick={() => setSortOrder(prev => prev === "asc" ? "desc" : "asc")}
          >
            <ArrowDownUp className="h-4 w-4 mr-1" />
            {sortOrder === "asc" ? "A → Z" : "Z → A"}
          </Button>
          <Button
            variant="outline"
            size="sm"
            onClick={() => {
              setSelectedDomaine(null);
              setIsOpenFormModal(true);
            }}
          >
            Ajouter un domaine
          </Button>
        </div>
      </div>

      <div className="m-5">
        <DynamicTable3
          columns={columns}
          data={filteredDomaines}
          isLoading={isLoading}
          onAdd={() => {
            setSelectedDomaine(null);
            setIsOpenFormModal(true);
          }}
          onEdit={handleEdit}
          onDelete={handleDelete}
          onFilter={(query: string) => setSearchQuery(query)}
          filterPlaceholder="Rechercher un domaine..."
        />
      </div>
    </div>
  );
}