import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Search, Landmark, ArrowDownUp } from "lucide-react";
import { useEffect, useState } from "react";
import apiService from "@/api/apiService";
import apiRoutes from "@/api/apiRoutes";
import { useAuth } from "@/lib/auth";
import { BankType } from "@/types/UtilsTypes";
import FormModal from "@/components/useful/form-modal.tsx";
import { Icon } from "@tabler/icons-react";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { DynamicTable3 } from "@/components/tables/dynamic-table-3";
import DeleteModal from "@/components/useful/delete-modal.tsx";

export default function BanqueSettingTab() {
  const { user } = useAuth();
  const [isLoading, setIsLoading] = useState(false);
  const [banques, setBanques] = useState<BankType[]>([]);
  const [searchQuery, setSearchQuery] = useState<string>("");
  const [isOpenFormModal, setIsOpenFormModal] = useState(false);
  const [loadingSubmit, setLoadingSubmit] = useState(false);
  const [sortOrder, setSortOrder] = useState<"asc" | "desc">("asc");
  const [selectedBank, setSelectedBank] = useState<BankType | null>(null);
  const [isOpenDelete, setIsOpenDelete] = useState(false);

  const fetchData = async () => {
    try {
      setIsLoading(true);
      const res = await apiService.get({
        url: apiRoutes.admin.app.contrat.banks.list
      }, {
        userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "",
        hasNoSuccessModal: true
      });
      setBanques(res.data || []);
    } catch (error) {
      if (error instanceof Error) {
        apiService.handleError(error.message, { hasNoFailureModal: true });
      }
    } finally {
      setIsLoading(false);
    }
  };

  const banqueFild = [
    {
      tag: "name",
      label: "Nom de la banque",
      type: "text",
      placeholder: "Entrez le nom de la banque",
      size: "col-span-12",
      required: true,
    },
  ];

  const banqueSchema = z.object({
    name: z.string().min(1, "Le nom de la banque est requis"),
  });

  const form = useForm<z.infer<typeof banqueSchema>>({
    resolver: zodResolver(banqueSchema),
    defaultValues: {
      name: "",
    },
    mode: "onSubmit",
    shouldFocusError: true,
  });

  const { reset } = form;

  const onSubmit = async (data: z.infer<typeof banqueSchema>) => {
    setLoadingSubmit(true);
    try {
      if (selectedBank) {
        // Mise à jour
        await apiService.put({
          url: `${apiRoutes.admin.app.contrat.banks.update}${selectedBank.id}`,
          body: data
        }, {
          userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "",
          hasNoSuccessModal: false
        });
      } else {
        // Création
        await apiService.post({
          url: apiRoutes.admin.app.contrat.banks.create,
          body: data
        }, {
          userToken: user?.type && user?.token ? `${user.type} ${user.token}` : "",
          hasNoSuccessModal: false
        });
      }
      await fetchData();
      setIsOpenFormModal(false);
      reset();
      setSelectedBank(null);
    } catch (error) {
      if (error instanceof Error) {
        apiService.handleError(error.message, { hasNoFailureModal: false });
      }
    } finally {
      setLoadingSubmit(false);
    }
  };

  const handleEdit = (bank: BankType) => {
    setSelectedBank(bank);
    reset({ name: bank.name });
    setIsOpenFormModal(true);
  };

  const handleDelete = (bank: BankType) => {
    setSelectedBank(bank);
    setIsOpenDelete(true);
  };

  const handleConfirmDelete = async () => {
    if (!selectedBank) return;
    
    try {
      setLoadingSubmit(true);
      await apiService.remove({
        url: `${apiRoutes.admin.app.contrat.banks.delete}${selectedBank.id}`
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
      setSelectedBank(null);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const filteredBanks = [...banques]
    .filter(b => b.name.toLowerCase().includes(searchQuery.toLowerCase()))
    .sort((a, b) => {
      return sortOrder === "asc"
        ? a.name.localeCompare(b.name)
        : b.name.localeCompare(a.name);
    });

  const columns = [
    { 
      key: "name", 
      label: "Nom de la banque",
      className: "font-medium"
    },
  ];

  return (
    <div className="flex flex-col gap-4">
      <DeleteModal
        title={"⚠️ Êtes-vous sûr de vouloir supprimer cette banque ?"}
        description={`La banque "${selectedBank?.name}" sera définitivement supprimée. Cette action est irréversible.`}
        isOpen={isOpenDelete}
        isetIsOpen={setIsOpenDelete}
        isDeleteLoading={loadingSubmit}
        onDelete={handleConfirmDelete}
        onCancel={() => {
          setSelectedBank(null);
          setIsOpenDelete(false);
        }}
      />

      <FormModal
        icon={Landmark as Icon}
        title={selectedBank ? "Modifier la banque" : "Ajouter une banque"}
        description={selectedBank 
          ? "Modifiez les informations de la banque" 
          : "Remplissez les informations pour ajouter une nouvelle banque."}
        isOpen={isOpenFormModal}
        setIsOpen={setIsOpenFormModal}
        form={form}
        fields={banqueFild}
        onSubmit={onSubmit}
        isSubmitLoading={loadingSubmit}
        onClose={() => {
          reset();
          setIsOpenFormModal(false);
          setSelectedBank(null);
        }}
      />

      <div className="flex items-center justify-between bg-background mx-5">
        <div>
          <h2 className="text-lg font-semibold">Banques</h2>
          <p className="text-xs text-muted-foreground mb-4">
            Gérez les banques utilisées dans votre système.
          </p>
        </div>
        <div className="flex gap-3">
          <div className="relative">
            <Search className="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
            <Input
              placeholder="Rechercher une banque..."
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
              setSelectedBank(null);
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
          data={filteredBanks}
          isLoading={isLoading}
          onAdd={() => {
            setSelectedBank(null);
            setIsOpenFormModal(true);
          }}
          onEdit={handleEdit}
          onDelete={handleDelete}
          onFilter={(query: string) => setSearchQuery(query)}
          filterPlaceholder="Rechercher une banque..."
        />
      </div>
    </div>
  );
}