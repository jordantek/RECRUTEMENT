import { useState } from "react";
import { cn } from "@/lib/utils";
import { User, Check, ChevronsUpDown, X } from "lucide-react";
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
import { Badge } from "@/components/ui/badge";
import useEmployeeStore from "@/contexts/useEmployeeStore";

interface MultiEmployeeSelectProps {
  selectedEmployeeIds: number[];
  onSelectionChange: (ids: number[]) => void;
}

export const MultiEmployeeSelect = ({
  selectedEmployeeIds,
  onSelectionChange,
}: MultiEmployeeSelectProps) => {
  const [open, setOpen] = useState(false);
  const { employees } = useEmployeeStore();

  const handleSelect = (employeeId: number) => {
    const newSelected = selectedEmployeeIds.includes(employeeId)
      ? selectedEmployeeIds.filter(id => id !== employeeId)
      : [...selectedEmployeeIds, employeeId];
    onSelectionChange(newSelected);
  };

  return (
    <div className="space-y-2">
      <Popover open={open} onOpenChange={setOpen}>
        <PopoverTrigger asChild>
          <Button
            variant="outline"
            role="combobox"
            aria-expanded={open}
            className="w-full justify-between"
          >
            <div className="flex items-center gap-2">
              <User className="h-4 w-4" />
              <span>
                {selectedEmployeeIds.length > 0
                  ? `${selectedEmployeeIds.length} employé(s) sélectionné(s)`
                  : "Sélectionner des employés"}
              </span>
            </div>
            <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
          </Button>
        </PopoverTrigger>
        <PopoverContent className="w-[400px] p-0">
          <Command>
            <CommandInput placeholder="Rechercher un employé..." />
            <CommandEmpty>Aucun employé trouvé</CommandEmpty>
            <CommandList>
              <CommandGroup>
                {employees.map((employee) => (
                  <CommandItem
                    key={employee.employeId}
                    onSelect={() => handleSelect(employee.employeId)}
                  >
                    <Check
                      className={cn(
                        "mr-2 h-4 w-4",
                        selectedEmployeeIds.includes(employee.employeId)
                          ? "opacity-100"
                          : "opacity-0"
                      )}
                    />
                    <div className="flex items-center gap-2">
                      <span className="capitalize">{employee.prenom} {employee.nom}</span>
                    </div>
                  </CommandItem>
                ))}
              </CommandGroup>
            </CommandList>
          </Command>
        </PopoverContent>
      </Popover>
    </div>
  );
};