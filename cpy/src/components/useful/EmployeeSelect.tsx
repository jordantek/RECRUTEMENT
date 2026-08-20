import {
    Command,
    CommandEmpty,
    CommandInput,
    CommandItem,
    CommandGroup,
    CommandList,
} from "@/components/ui/command";
import { Popover, PopoverTrigger, PopoverContent } from "@/components/ui/popover";
import { Button } from "@/components/ui/button";
import { Check, ChevronsUpDown, User, RefreshCw, Loader2, Plus } from "lucide-react";
import { useAuth } from "@/lib/auth";
import { useEffect, useState } from "react";
import { cn } from "@/lib/utils";
import useEmployeeStore from "@/contexts/useEmployeeStore.ts";
import useCompanyStore from "@/contexts/CompanyContext.ts";
import {routeHelpers} from "@/helpers/routeHelpers.ts";
import { useNavigate } from "react-router-dom";

export const EmployeeSelect = () => {
    const { user } = useAuth();
    const navigate = useNavigate();
    const { selectedCompany } = useCompanyStore();
    const {
        employees,
        loading,
        selectedEmployee,
        setSelectedEmployee,
        fetchEmployees,
        resetEmployees,
    } = useEmployeeStore();

    const [open, setOpen] = useState(false);  // <-- gérer l'ouverture du popover

    // Charger les employés quand l'entreprise change
    useEffect(() => {
        if (selectedCompany && user) {
            fetchEmployees(selectedCompany, user);
        } else {
            resetEmployees();
        }
    }, [selectedCompany, user]);

    if ( !selectedCompany) return null;

    const onAddNewEmployee = () => {
        navigate(routeHelpers.dashboard.employee.create)
    };

    return (
        <Popover open={open} onOpenChange={setOpen}>
            <PopoverTrigger asChild>
                <Button
                    variant="outline"
                    role="combobox"
                    aria-expanded={open}
                    className="w-[300px] justify-between rounded-md px-4 py-2 text-sm shadow-none border-gray-200 bg-background hover:bg-accent/30 transition"
                    disabled={loading || !selectedCompany}
                >
                    {selectedEmployee ? (
                        <div className="flex flex-col items-start text-left w-full py-0.5">
                            <div className="flex items-center w-full justify-between">
                                <div className="flex items-center gap-2">
                                    <User className="h-4 w-4 text-muted-foreground" />
                                    <span className="font-semibold truncate capitalize">
                                        {`${selectedEmployee.nom} ${selectedEmployee.prenom}`}
                                    </span>
                                </div>
                                <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 text-muted-foreground" />
                            </div>
                        </div>
                    ) : (
                        <span className="text-muted-foreground">Liste des employés</span>
                    )}
                </Button>
            </PopoverTrigger>

            <PopoverContent className="w-[400px] p-0" align="center">
                <Command>
                    <CommandInput
                        placeholder="Rechercher un employé..."
                        className="h-9 border-none"
                    />
                    <CommandEmpty className="py-4 flex flex-col items-center gap-2">
                        <span>Aucun employé trouvé.</span>
                        <Button
                            variant="outline"
                            size="sm"
                            onClick={() => selectedCompany && user && fetchEmployees(selectedCompany, user)}
                            disabled={loading}
                        >
                            {loading ? (
                                <Loader2 className="h-4 w-4 animate-spin" />
                            ) : (
                                <RefreshCw className="h-4 w-4" />
                            )}
                            <span className="ml-2">Actualiser</span>
                        </Button>
                    </CommandEmpty>

                    <CommandList>
                        <CommandGroup>
                            {employees.map((employee) => (
                                <CommandItem
                                    key={employee.employeId}
                                    value={`${employee.nom} ${employee.prenom}`}
                                    onSelect={() => {
                                        setSelectedEmployee(employee);
                                        setOpen(true);  // fermer popover au choix
                                    }}
                                    className="py-2 aria-selected:bg-accent/30"
                                >
                                    <div className="flex w-full flex-col">
                                        <div className="flex items-center justify-between">
                                            <div className="flex items-center gap-2">
                                                <User className="h-4 w-4 text-muted-foreground" />
                                                <span className="font-semibold text-sm truncate capitalize">
                                                    {`${employee.nom} ${employee.prenom}`}
                                                </span>
                                            </div>
                                            <Check
                                                className={cn(
                                                    "h-4 w-4",
                                                    selectedEmployee?.employeId.toString() === employee.employeId.toString()
                                                        ? "text-green-500 opacity-100"
                                                        : "opacity-0"
                                                )}
                                            />
                                        </div>
                                    </div>
                                </CommandItem>
                            ))}
                        </CommandGroup>
                    </CommandList>

                    <div className="border-t px-4 py-3">
                        <Button
                            variant="ghost"
                            size="sm"
                            onClick={onAddNewEmployee}
                            className="w-full justify-center text-primary hover:bg-accent"
                        >
                            <Plus className="h-4 w-4 mr-2" />
                            Ajouter un nouvel employé
                        </Button>
                    </div>
                </Command>
            </PopoverContent>
        </Popover>
    );
};
