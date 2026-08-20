import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Search, GraduationCap, ArrowDownUp } from "lucide-react";
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

type DiplomeType = {
  id: number;
  name: string;
  description: string;
};

export default function DiplomeManagerSettingTab() {
  const { user, logout } = useAuth();
  const [isLoading, setIsLoading] = useState(false);
  const [diplomes, setDiplomes] = useState<DiplomeType[]>([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [isOpenFormModal, setIsOpenFormModal] = useState(false);
  const [loadingSubmit, setLoadingSubmit] = useState(false);
  const [sortOrder, setSortOrder] = useState<"asc" | "desc">("asc");
  const [selectedDiplome, setSelectedDiplome] = useState<DiplomeType | null>(null);
  const [isOpenDelete, setIsOpenDelete] = useState(false);

  const fetchData = async () => {
    try {
      setIsLoading(true);
      const res = await apiService.get(
        {
          url: apiRoutes.admin.app.employee.diplomas.list,
        },
        {
          userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "",
          hasNoSuccessModal: true,
        }
      );
      setDiplomes(res.data || []);
    } catch (error) {
      if (error instanceof Error) {
        apiService.handleError(error.message, { hasNoFailureModal: true });
      }
    } finally {
      setIsLoading(false);
    }
  };

  const diplomeFields = [
    {
      tag: "name",
      label: "Nom du diplôme",
      type: "text",
      placeholder: "Entrez le nom du diplôme",
      size: "col-span-12",
      required: true,
    },
    {
      tag: "description",
      label: "Description du diplôme",
      type: "textarea",
      placeholder: "Entrez la description du diplôme",
      size: "col-span-12 h-100",
      required: false,
    },
  ];

  const diplomeSchema = z.object({
    name: z.string().min(1, "Le nom du diplôme est requis"),
    description: z.string().optional(),
  });

  const form = useForm<z.infer<typeof diplomeSchema>>({
    resolver: zodResolver(diplomeSchema),
    defaultValues: {
      name: "",
      description: "",
    },
  });

  const { reset } = form;

  const onSubmit = async (data: z.infer<typeof diplomeSchema>) => {
    setLoadingSubmit(true);
    try {
      if (selectedDiplome) {
        // Mise à jour
        await apiService.put(
          {
            url: `${apiRoutes.admin.app.employee.diplomas.update}${selectedDiplome.id}`,
            body: data,
          },
          {
            userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
            hasNoSuccessModal: false,
            onTokenExpired: logout
          }
        );
      } else {
        // Création
        await apiService.post(
          {
            url: apiRoutes.admin.app.employee.diplomas.create,
            body: data,
          },
          {
            userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
            hasNoSuccessModal: false,
            onTokenExpired: logout
          }
        );
      }
      await fetchData();
      setIsOpenFormModal(false);
      reset();
      setSelectedDiplome(null);
    } catch (error) {
      if (error instanceof Error) {
        apiService.handleError(error.message, { hasNoFailureModal: false });
      }
    } finally {
      setLoadingSubmit(false);
    }
  };

  const handleEdit = (diplome: DiplomeType) => {
    setSelectedDiplome(diplome);
    reset({
      name: diplome.name,
      description: diplome.description || ""
    });
    setIsOpenFormModal(true);
  };

  const handleDelete = (diplome: DiplomeType) => {
    setSelectedDiplome(diplome);
    setIsOpenDelete(true);
  };

  const handleConfirmDelete = async () => {
    if (!selectedDiplome) return;
    
    try {
      setLoadingSubmit(true);
      await apiService.remove(
        {
          url: `${apiRoutes.admin.app.employee.diplomas.delete}${selectedDiplome.id}`,
        },
        {
          userToken: `${user?.type ?? ""} ${user?.token ?? ""}`,
          hasNoSuccessModal: false,
          onTokenExpired: logout
        }
      );
      await fetchData();
    } catch (error) {
      if (error instanceof Error) {
        apiService.handleError(error.message);
      }
    } finally {
      setLoadingSubmit(false);
      setIsOpenDelete(false);
      setSelectedDiplome(null);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const filteredDiplomes = [...diplomes]
    .filter((d) => d.name.toLowerCase().includes(searchQuery.toLowerCase()))
    .sort((a, b) =>
      sortOrder === "asc"
        ? a.name.localeCompare(b.name)
        : b.name.localeCompare(a.name)
    );

  const columns = [
    { 
      key: "name", 
      label: "Nom du diplôme",
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
        title="⚠️ Supprimer ce diplôme ?"
        description={`Le diplôme "${selectedDiplome?.name}" sera définitivement supprimé.`}
        isOpen={isOpenDelete}
        isetIsOpen={setIsOpenDelete}
        isDeleteLoading={loadingSubmit}
        onDelete={handleConfirmDelete}
        onCancel={() => {
          setSelectedDiplome(null);
          setIsOpenDelete(false);
        }}
      />

      <FormModal
        icon={GraduationCap as Icon}
        title={selectedDiplome ? "Modifier le diplôme" : "Ajouter un diplôme"}
        description={selectedDiplome 
          ? "Modifiez les informations du diplôme" 
          : "Entrez les informations du nouveau diplôme."}
        isOpen={isOpenFormModal}
        setIsOpen={setIsOpenFormModal}
        form={form}
        fields={diplomeFields}
        onSubmit={onSubmit}
        isSubmitLoading={loadingSubmit}
        onClose={() => {
          reset();
          setIsOpenFormModal(false);
          setSelectedDiplome(null);
        }}
      />

      <div className="flex items-center justify-between bg-background mx-5">
        <div>
          <h2 className="text-lg font-semibold">Diplômes</h2>
          <p className="text-xs text-muted-foreground mb-4">
            Consultez, ajoutez ou supprimez les diplômes enregistrés.
          </p>
        </div>
        <div className="flex gap-3">
          <div className="relative">
            <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
            <Input
              placeholder="Rechercher un diplôme..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="pl-8 w-[250px]"
            />
          </div>
          <Button
            variant="outline"
            size="sm"
            onClick={() => setSortOrder((prev) => (prev === "asc" ? "desc" : "asc"))}
          >
            <ArrowDownUp className="h-4 w-4 mr-1" />
            {sortOrder === "asc" ? "A → Z" : "Z → A"}
          </Button>
          <Button
            variant="outline"
            size="sm"
            onClick={() => {
              setSelectedDiplome(null);
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
          data={filteredDiplomes}
          isLoading={isLoading}
          onAdd={() => {
            setSelectedDiplome(null);
            setIsOpenFormModal(true);
          }}
          onEdit={handleEdit}
          onDelete={handleDelete}
          onFilter={(query: string) => setSearchQuery(query)}
          filterPlaceholder="Rechercher un diplôme..."
        />
      </div>
    </div>
  );
}