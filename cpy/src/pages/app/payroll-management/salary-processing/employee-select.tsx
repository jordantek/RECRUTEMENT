import { useId, useState } from "react"
import { CheckIcon, ChevronDownIcon } from "lucide-react"
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from "@/components/ui/command"
import { Label } from "@/components/ui/label"
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover"
import { Button } from "@/components/ui/button"
import { cn } from "@/lib/utils"

import { EmployeeType } from "@/types/employee/EmployeeType"

const Square = ({
  className,
  children,
}: {
  className?: string
  children: React.ReactNode
}) => (
  <span
    data-square
    className={cn(
      "bg-muted text-muted-foreground flex size-6 items-center justify-center rounded text-xs font-medium",
      className
    )}
    aria-hidden="true"
  >
    {children}
  </span>
)

function getInitials(name: string) {
  const words = name.trim().split(" ")
  if (words.length === 1) return words[0][0]?.toUpperCase()
  return (words[0][0] + words[1][0])?.toUpperCase()
}

type Props = {
  employees: EmployeeType[]
  selected: EmployeeType | null
  onSelect: (employee: EmployeeType) => void
}

export default function EmployeeSelect({ employees, selected, onSelect }: Props) {
  const id = useId()
  const [open, setOpen] = useState(false)
  const [search, setSearch] = useState("")

  const handleSelect = (employee: EmployeeType) => {
    onSelect(employee)
    setOpen(false)
  }

  const filteredEmployees = employees.filter(emp =>
    `${emp.nom} ${emp.prenom}`.toLowerCase().includes(search.toLowerCase())
  )

  return (
    <div className="flex items-center gap-4">
      <Label htmlFor={id} className="font-semibold">
        Employé
      </Label>
      <Popover open={open} onOpenChange={setOpen}>
        <PopoverTrigger asChild>
          <Button
            id={id}
            variant="outline"
            role="combobox"
            aria-expanded={open}
            className="w-72 justify-between ps-3 [&>span]:flex [&>span]:items-center [&>span]:gap-2"
          >
            {selected ? (
              <>
                <Square>{getInitials(`${selected.nom} ${selected.prenom}`)}</Square>
                <span className="truncate">{selected.nom} {selected.prenom}</span>
              </>
            ) : (
              <span className="text-muted-foreground">Sélectionner un employé</span>
            )}
            <ChevronDownIcon
              size={16}
              className="text-muted-foreground/80 ml-auto"
              aria-hidden="true"
            />
          </Button>
        </PopoverTrigger>
        <PopoverContent className="w-[290px] p-0">
          <Command>
            <CommandInput
              placeholder="Rechercher un employé..."
              value={search}
              onValueChange={setSearch}
            />
            <CommandList>
              <CommandEmpty>Aucun employé trouvé.</CommandEmpty>
              <CommandGroup>
                {filteredEmployees.map((emp) => (
                  <CommandItem
                    key={emp.id}
                    value={`${emp.nom} ${emp.prenom}`}
                    onSelect={() => handleSelect(emp)}
                    className="flex items-center gap-2"
                  >
                    <Square className="text-blue-700">{getInitials(`${emp.nom} ${emp.prenom}`)}</Square>
                    <span className="truncate text-gray-500">{emp.nom} {emp.prenom}</span>
                    {selected?.id === emp.id && (
                      <CheckIcon size={16} className="ml-auto text-primary" />
                    )}
                  </CommandItem>
                ))}
              </CommandGroup>
            </CommandList>
          </Command>
        </PopoverContent>
      </Popover>
    </div>
  )
}
