import { useId, useState } from "react"
import { CheckIcon, ChevronDownIcon } from "lucide-react"

import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import {
    Command,
    CommandEmpty,
    CommandGroup,
    CommandItem,
    CommandList,
    CommandSeparator,
} from "@/components/ui/command"
import { Label } from "@/components/ui/label"
import {
    Popover,
    PopoverContent,
    PopoverTrigger,
} from "@/components/ui/popover"

interface Organization {
    value: string;
    label: string;
}

interface OrganizationSelectWithSearchProps {
    organizations: Organization[];
    value?: string;
    onChange: (value: string) => void;
    onAddNewOrganization?: () => void;
    label: string;
}

export default function OrganizationSelectWithSearch({
                                                         organizations,
                                                         value: initialValue = "",
                                                         onChange,
                                                         label,
                                                     }: OrganizationSelectWithSearchProps) {
    const id = useId()
    const [open, setOpen] = useState<boolean>(false)
    const [value, setValue] = useState<string>(initialValue)

    const handleSelect = (selectedValue: string) => {
        const newValue = selectedValue === value ? "" : selectedValue
        setValue(newValue)
        onChange(newValue)
        setOpen(false)
    }

    return (
        <div className="*:not-first:mt-2">
            <Label htmlFor={id}>{label}</Label>
            <Popover open={open} onOpenChange={setOpen}>
                <PopoverTrigger asChild>
                    <Button
                        id={id}
                        variant="outline"
                        role="combobox"
                        aria-expanded={open}
                        className="bg-background hover:bg-background border-input w-full justify-between px-3 font-normal outline-offset-0 outline-none focus-visible:outline-[3px]"
                    >
                        <span className={cn("truncate", !value && "text-muted-foreground")}>
                          {value
                              ? organizations.find((organization) => organization.value === value)?.label
                              : "Selectionner un département "}
                        </span>
                        <ChevronDownIcon
                            size={16}
                            className="text-muted-foreground/80 shrink-0"
                            aria-hidden="true"/>
                    </Button>
                </PopoverTrigger>
                <PopoverContent
                    className="border-input w-full min-w-[var(--radix-popper-anchor-width)] p-0"
                    align="start"
                >
                    <Command>
                        <CommandList>
                            <CommandEmpty>No organization found.</CommandEmpty>
                            <CommandGroup>
                                {organizations.map((organization) => (
                                    <CommandItem
                                        key={organization.value}
                                        value={organization.value}
                                        onSelect={() => handleSelect(organization.value)}
                                    >
                                        {organization.label}
                                        {value === organization.value && (
                                            <CheckIcon size={16} className="ml-auto" />
                                        )}
                                    </CommandItem>
                                ))}
                            </CommandGroup>
                            <CommandSeparator />
                            {/*<CommandGroup>
                                {onAddNewOrganization && (
                                    <Button
                                        variant="ghost"
                                        className="w-full justify-start font-normal"
                                        onClick={onAddNewOrganization}
                                    >
                                        <PlusIcon
                                            size={16}
                                            className="-ms-2 opacity-60"
                                            aria-hidden="true"
                                        />
                                        New organization
                                    </Button>
                                )}
                            </CommandGroup>*/}
                        </CommandList>
                    </Command>
                </PopoverContent>
            </Popover>
        </div>
    )
}
