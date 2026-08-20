import {
    Command,
    CommandEmpty,
    CommandInput,
    CommandItem,
    CommandGroup,
    CommandList,
} from "@/components/ui/command"
import { Popover, PopoverTrigger, PopoverContent } from "@/components/ui/popover"
import { Button } from "@/components/ui/button"
import {Check, ChevronsUpDown, Building2, RefreshCw, Loader2} from "lucide-react"
import { useAuth } from "@/lib/auth"
import { useEffect, useState } from "react"
import { cn } from "@/lib/utils"
import useCompanyStore from "@/contexts/CompanyContext.ts";
import useEmployeeStore from "@/contexts/useEmployeeStore.ts";

export const CompanySelect = () => {
    const { user } = useAuth()
    const {
        companies,
        selectedCompany,
        selectCompany,
        fetchCompanyList,
        loading,
        showCompanySelect,
        //setShowCompanySelect
    } = useCompanyStore()

    const {
        fetchEmployees,
        resetEmployees
    } = useEmployeeStore()

    const [open, setOpen] = useState(false)

    // Charger la liste des entreprises au montage
    useEffect(() => {
        if (user) fetchCompanyList(user)
    }, [user])

    // Charger les employés quand une entreprise est sélectionnée
    useEffect(() => {
        console.log("chargement")
        if (selectedCompany && user) {
            fetchEmployees(selectedCompany, user)
        } else {
            resetEmployees()
        }
    }, [selectedCompany])

    if (!showCompanySelect) return null

    return (
        <Popover open={open} onOpenChange={setOpen}>
            <PopoverTrigger asChild>
                <Button
                    variant="outline"
                    role="combobox"
                    aria-expanded={open}
                    className="w-[300px] justify-between rounded-md px-4 py-2 text-sm shadow-none border-gray-200 bg-background hover:bg-accent/30 transition"
                    disabled={loading}
                >
                    {selectedCompany ? (
                        <div className="flex flex-col items-start text-left w-full py-0.5">
                            <div className="flex items-center w-full justify-between">
                                <div className="flex items-center gap-2">
                                    <Building2 className="h-4 w-4 text-muted-foreground" />
                                    <span className="font-semibold truncate capitalize">
                                        {selectedCompany.name}
                                    </span>
                                </div>
                                <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 text-muted-foreground" />
                            </div>
                        </div>
                    ) : (
                        <span className="text-muted-foreground">Sélectionner une entreprise</span>
                    )}
                </Button>
            </PopoverTrigger>

            <PopoverContent className="w-[500px] p-0" align="start">
                <Command>
                    <CommandInput
                        placeholder="Rechercher une entreprise..."
                        className="h-9 border-none"
                    />
                    <CommandEmpty className="py-4 flex flex-col items-center gap-2">
                        <span>Aucune entreprise trouvée.</span>
                        <Button
                            variant="outline"
                            size="sm"
                            onClick={() => user && fetchCompanyList(user)}
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
                            {companies.map((company) => (
                                <CommandItem
                                    key={company.id}
                                    value={company.name.toLowerCase()}
                                    onSelect={() => {
                                        selectCompany(company)
                                        setOpen(false)
                                    }}
                                    className="py-2 aria-selected:bg-accent/30"
                                >
                                    <div className="flex w-full flex-col">
                                        <div className="flex items-center justify-between">
                                            <div className="flex items-center gap-2">
                                                <Building2 className="h-4 w-4 text-muted-foreground" />
                                                <span className="font-semibold text-sm truncate capitalize">
                                                    {company.name}
                                                </span>
                                            </div>
                                            <Check
                                                className={cn(
                                                    "h-4 w-4",
                                                    selectedCompany?.id === company.id
                                                        ? "text-green-500 opacity-100"
                                                        : "opacity-0"
                                                )}
                                            />
                                        </div>
                                        <p className="text-xs text-muted-foreground pl-6 truncate">
                                            {company.address}
                                        </p>
                                    </div>
                                </CommandItem>
                            ))}
                        </CommandGroup>
                    </CommandList>
                </Command>
            </PopoverContent>
        </Popover>
    )
}