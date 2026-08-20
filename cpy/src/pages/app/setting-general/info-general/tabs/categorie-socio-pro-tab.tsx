import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Search, Users, ArrowDownUp } from "lucide-react";
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

type CategorieType = {
  id: string;
  name: string;
  description: string;
};

export default function CategorieSocioProTab() {
  const { user } = useAuth();
  const [categories, setCategories] = useState<CategorieType[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [isOpenFormModal, setIsOpenFormModal] = useState(false);
  const [loadingSubmit, setLoadingSubmit] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [sortOrder, setSortOrder] = useState<"asc" | "desc">("asc");
  const [selectedCategorie, setSelectedCategorie] = useState<CategorieType | null>(null);
  const [isOpenDelete, setIsOpenDelete] = useState(false);

  const fetchData = async () => {
    setIsLoading(true);
    try {
      const res = await apiService.get({
        url: apiRoutes.admin.app.employee.categories.list,
      }, {
        userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "",
        hasNoSuccessModal: true,
      });
      setCategories(res.data || []);
    } catch (error) {
      if (error instanceof Error) {
        apiService.handleError(error.message, { hasNoFailureModal: true });
      }
    } finally {
      setIsLoading(false);
    }
  };

  const categorieFields = [
    {
      tag: "name",
      label: "Nom de la catégorie",
      type: "text",
      placeholder: "Ex: Cadres",
      size: "col-span-12",
      required: true,
    },
    {
      tag: "description",
      label: "Description",
      type: "text",
      placeholder: "Ex: Fonctions de direction",
      size: "col-span-12",
      required: true,
    },
  ];

  const categorieSchema = z.object({
    name: z.string().min(1, "Le nom est requis"),
    description: z.string().min(1, "La description est requise"),
  });

  const form = useForm<z.infer<typeof categorieSchema>>({
    resolver: zodResolver(categorieSchema),
    defaultValues: {
      name: "",
      description: "",
    },
  });

  const { reset } = form;

  const onSubmit = async (data: z.infer<typeof categorieSchema>) => {
    setLoadingSubmit(true);
    try {
      if (selectedCategorie) {
        // Mise à jour
        await apiService.put({
          url: `${apiRoutes.admin.app.employee.categories.update}${selectedCategorie.id}`,
          body: data,
        }, {
          userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "",
          hasNoSuccessModal: false,
        });
      } else {
        // Création
        await apiService.post({
          url: apiRoutes.admin.app.employee.categories.create,
          body: data,
        }, {
          userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "",
          hasNoSuccessModal: false,
        });
      }
      await fetchData();
      reset();
      setIsOpenFormModal(false);
      setSelectedCategorie(null);
    } catch (error) {
      if (error instanceof Error) {
        apiService.handleError(error.message, { hasNoFailureModal: false });
      }
    } finally {
      setLoadingSubmit(false);
    }
  };

  const handleEdit = (categorie: CategorieType) => {
    setSelectedCategorie(categorie);
    reset({
      name: categorie.name,
      description: categorie.description
    });
    setIsOpenFormModal(true);
  };

  const handleDelete = (categorie: CategorieType) => {
    setSelectedCategorie(categorie);
    setIsOpenDelete(true);
  };

  const handleConfirmDelete = async () => {
    if (!selectedCategorie) return;
    
    try {
      setLoadingSubmit(true);
      await apiService.remove({
        url: `${apiRoutes.admin.app.employee.categories.delete}${selectedCategorie.id}`,
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
      setSelectedCategorie(null);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const filteredCategories = [...categories]
    .filter(c => 
      c.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
      c.description.toLowerCase().includes(searchQuery.toLowerCase())
    )
    .sort((a, b) => 
      sortOrder === "asc" 
        ? a.name.localeCompare(b.name) 
        : b.name.localeCompare(a.name)
    );

  const columns = [
    { 
      key: "name", 
      label: "Catégorie",
      className: "font-medium"
    },
    { 
      key: "description", 
      label: "Description",
      className: "text-muted-foreground whitespace-normal"
    },
  ];

  return (
    <div className="flex flex-col gap-4">
      <DeleteModal
        title="⚠️ Supprimer cette catégorie ?"
        description={`La catégorie "${selectedCategorie?.name}" sera définitivement supprimée.`}
        isOpen={isOpenDelete}
        isetIsOpen={setIsOpenDelete}
        isDeleteLoading={loadingSubmit}
        onDelete={handleConfirmDelete}
        onCancel={() => {
          setSelectedCategorie(null);
          setIsOpenDelete(false);
        }}
      />

      <FormModal
        icon={Users as Icon}
        title={selectedCategorie ? "Modifier la catégorie" : "Ajouter une catégorie"}
        description={selectedCategorie 
          ? "Modifiez les informations de la catégorie socio-professionnelle" 
          : "Entrez les informations pour une nouvelle catégorie socio-professionnelle."}
        isOpen={isOpenFormModal}
        setIsOpen={setIsOpenFormModal}
        form={form}
        fields={categorieFields}
        onSubmit={onSubmit}
        isSubmitLoading={loadingSubmit}
        onClose={() => {
          reset();
          setIsOpenFormModal(false);
          setSelectedCategorie(null);
        }}
      />

      <div className="flex items-center justify-between bg-background mx-5">
        <div>
          <h2 className="text-lg font-semibold">Catégories socio-professionnelles</h2>
          <p className="text-xs text-muted-foreground mb-4">
            Gérez les catégories socio-professionnelles disponibles.
          </p>
        </div>
        <div className="flex gap-3">
          <div className="relative">
            <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
            <Input
              placeholder="Rechercher une catégorie..."
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
              setSelectedCategorie(null);
              setIsOpenFormModal(true);
            }}
          >
            Ajouter une catégorie
          </Button>
        </div>
      </div>

      <div className="m-5">
        <DynamicTable3
          columns={columns}
          data={filteredCategories}
          isLoading={isLoading}
          onAdd={() => {
            setSelectedCategorie(null);
            setIsOpenFormModal(true);
          }}
          onEdit={handleEdit}
          onDelete={handleDelete}
          onFilter={(query: string) => setSearchQuery(query)}
          filterPlaceholder="Rechercher une catégorie..."
        />
      </div>
    </div>
  );
}