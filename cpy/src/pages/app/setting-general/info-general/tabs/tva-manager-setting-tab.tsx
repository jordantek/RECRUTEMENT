import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Search, Percent, ArrowDownUp } from "lucide-react";
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

type TVAType = {
  id: string;
  rate: number;
  description: string;
};

export default function TvaSettingTab() {
  const { user } = useAuth();
  const [tvas, setTvas] = useState<TVAType[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [isOpenFormModal, setIsOpenFormModal] = useState(false);
  const [loadingSubmit, setLoadingSubmit] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [sortOrder, setSortOrder] = useState<"asc" | "desc">("asc");
  const [selectedTva, setSelectedTva] = useState<TVAType | null>(null);
  const [isOpenDelete, setIsOpenDelete] = useState(false);

  const fetchData = async () => {
    setIsLoading(true);
    try {
      const res = await apiService.get({
        url: apiRoutes.admin.app.contrat.tva.list,
      }, {
        userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "",
        hasNoSuccessModal: true,
      });
      setTvas(res.data || []);
    } catch (error) {
      if (error instanceof Error) {
        apiService.handleError(error.message, { hasNoFailureModal: true });
      }
    } finally {
      setIsLoading(false);
    }
  };

  const tvaFields = [
    {
      tag: "rate",
      label: "Taux de TVA (%)",
      type: "number",
      placeholder: "Ex: 20",
      size: "col-span-12",
      required: true,
    },
    {
      tag: "description",
      label: "Description",
      type: "text",
      placeholder: "Ex: TVA standard",
      size: "col-span-12",
      required: false,
    }
  ];

  const tvaSchema = z.object({
    rate: z.coerce.number().min(0.1, "Le taux doit être supérieur à 0").max(100, "Le taux ne peut dépasser 100%"),
    description: z.string().optional(),
  });

  const form = useForm<z.infer<typeof tvaSchema>>({
    resolver: zodResolver(tvaSchema),
    defaultValues: {
      rate: 0,
      description: "",
    },
  });

  const { reset } = form;

  const onSubmit = async (data: z.infer<typeof tvaSchema>) => {
    setLoadingSubmit(true);
    try {
      if (selectedTva) {
        // Mise à jour
        await apiService.put({
          url: `${apiRoutes.admin.app.contrat.tva.create}${selectedTva.id}`,
          body: data,
        }, {
          userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "",
          hasNoSuccessModal: false,
        });
      } else {
        // Création
        await apiService.post({
          url: apiRoutes.admin.app.contrat.tva.create,
          body: data,
        }, {
          userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "",
          hasNoSuccessModal: false,
        });
      }
      await fetchData();
      reset();
      setIsOpenFormModal(false);
      setSelectedTva(null);
    } catch (error) {
      if (error instanceof Error) {
        apiService.handleError(error.message, { hasNoFailureModal: false });
      }
    } finally {
      setLoadingSubmit(false);
    }
  };

  const handleEdit = (tva: TVAType) => {
    setSelectedTva(tva);
    reset({
      rate: tva.rate,
      description: tva.description || ""
    });
    setIsOpenFormModal(true);
  };

  const handleDelete = (tva: TVAType) => {
    setSelectedTva(tva);
    setIsOpenDelete(true);
  };

  const handleConfirmDelete = async () => {
    if (!selectedTva) return;
    
    try {
      setLoadingSubmit(true);
      // await apiService.delete({
      //   url: `${apiRoutes.admin.app.contrat.tva.delete}${selectedTva.id}`,
      // }, {
      //   userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "",
      //   hasNoSuccessModal: false,
      // });
      await fetchData();
    } catch (error) {
      if (error instanceof Error) {
        apiService.handleError(error.message);
      }
    } finally {
      setLoadingSubmit(false);
      setIsOpenDelete(false);
      setSelectedTva(null);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const filteredTvas = [...tvas]
    .filter(tva => 
      tva.description.toLowerCase().includes(searchQuery.toLowerCase()) ||
      tva.rate.toString().includes(searchQuery)
    )
    .sort((a, b) => 
      sortOrder === "asc" ? a.rate - b.rate : b.rate - a.rate
    );

  const columns = [
    { 
      key: "rate", 
      label: "Taux (%)",
      render: (rate: number) => `${rate}%`,
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
        title="⚠️ Supprimer ce taux de TVA ?"
        description={`Le taux de ${selectedTva?.rate}% sera définitivement supprimé.`}
        isOpen={isOpenDelete}
        isetIsOpen={setIsOpenDelete}
        isDeleteLoading={loadingSubmit}
        onDelete={handleConfirmDelete}
        onCancel={() => {
          setSelectedTva(null);
          setIsOpenDelete(false);
        }}
      />

      <FormModal
        icon={Percent as Icon}
        title={selectedTva ? "Modifier la TVA" : "Ajouter une TVA"}
        description={selectedTva 
          ? "Modifiez les informations du taux de TVA" 
          : "Renseignez les informations du nouveau taux de TVA."}
        isOpen={isOpenFormModal}
        setIsOpen={setIsOpenFormModal}
        form={form}
        fields={tvaFields}
        onSubmit={onSubmit}
        isSubmitLoading={loadingSubmit}
        onClose={() => {
          reset();
          setIsOpenFormModal(false);
          setSelectedTva(null);
        }}
      />

      <div className="flex items-center justify-between bg-background mx-5">
        <div>
          <h2 className="text-lg font-semibold">TVA</h2>
          <p className="text-xs text-muted-foreground mb-4">
            Gérez les taux de TVA.
          </p>
        </div>
        <div className="flex gap-3">
          <div className="relative">
            <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
            <Input
              placeholder="Rechercher..."
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
            {sortOrder === "asc" ? "Croissant" : "Décroissant"}
          </Button>
          <Button
            variant="outline"
            size="sm"
            onClick={() => {
              setSelectedTva(null);
              setIsOpenFormModal(true);
            }}
          >
            Ajouter une TVA
          </Button>
        </div>
      </div>

      <div className="m-5">
        <DynamicTable3
          columns={columns}
          data={filteredTvas}
          isLoading={isLoading}
          onAdd={() => {
            setSelectedTva(null);
            setIsOpenFormModal(true);
          }}
          onEdit={handleEdit}
          onDelete={handleDelete}
          onFilter={(query: string) => setSearchQuery(query)}
          filterPlaceholder="Rechercher une TVA..."
        />
      </div>
    </div>
  );
}