"use client"

import { useId, useState } from "react"
import { CheckIcon, ChevronDownIcon } from "lucide-react"

import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
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

import { CompanyType } from "@/types/company/CompanyType"

// ✅ Affichage d’un carré avec les initiales
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

// ✅ Fonction pour extraire les initiales
function getInitials(name: string) {
  const words = name.trim().split(" ")
  if (words.length === 1) return words[0][0]?.toUpperCase()
  return (words[0][0] + words[1][0])?.toUpperCase()
}

type Props = {
  companies: CompanyType[]
  selected: CompanyType | null
  onSelect: (company: CompanyType) => void
}

export default function CompanySelect({ companies, selected, onSelect }: Props) {
  const id = useId()
  const [open, setOpen] = useState(false)
  const [search, setSearch] = useState("")

  const handleSelect = (company: CompanyType) => {
    onSelect(company)
    setOpen(false)
  }

  const filteredCompanies = companies.filter(company =>
    company.name.toLowerCase().includes(search.toLowerCase())
  )

  return (
    <div className="flex items-center gap-4">
      <Label htmlFor={id} className="font-semibold">
        Entreprise
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
                <Square>{getInitials(selected.name)}</Square>
                <span className="truncate">{selected.name}</span>
              </>
            ) : (
              <span className="text-muted-foreground">Sélectionner une entreprise</span>
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
              placeholder="Rechercher une entreprise..."
              value={search}
              onValueChange={setSearch}
              className="focus:outline-none focus:ring-0 focus:border-transparent"
            />
            <CommandList>
              <CommandEmpty>Aucune entreprise trouvée.</CommandEmpty>
              <CommandGroup>
                {filteredCompanies.map((company) => (
                  <CommandItem
                    key={company.id}
                    value={company.name}
                    onSelect={() => handleSelect(company)}
                    className="flex items-center gap-2"
                  >
                    <Square className="text-blue-700">{getInitials(company.name)}</Square>
                    <span className="truncate text-gray-500">{company.name}</span>
                    {selected?.id === company.id && (
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
