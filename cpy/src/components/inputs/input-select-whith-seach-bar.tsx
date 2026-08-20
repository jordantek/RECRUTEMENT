"use client";

import * as React from "react";
import { Check, ChevronsUpDown } from "lucide-react";
import { cn } from "@/lib/utils";
import { Button } from "@/components/ui/button";
import {
  Command,
  CommandEmpty,
  CommandGroup,
  CommandInput,
  CommandItem,
  CommandList,
} from "@/components/ui/command";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";

interface InputSelectWithSearchBarProps {
  options: { value: string; label: string }[];
  onSelect: (value: string) => void;
  placeholder?: string;
  defaultValue?: string;
}

export function InputSelectWithSearchBar({
                                           options,
                                           onSelect,
                                           placeholder = "Sélectionner...",
                                           defaultValue = ""
                                         }: InputSelectWithSearchBarProps) {
  const [open, setOpen] = React.useState(false);
  const [value, setValue] = React.useState(defaultValue);

  const selectedLabel = options.find((option) => option.value === value)?.label;

  return (
      <Popover open={open} onOpenChange={setOpen}>
        <PopoverTrigger asChild>
          <Button
              variant="outline"
              role="combobox"
              aria-expanded={open}
              className="w-[200px] justify-between select-content-ps"
          >
            {selectedLabel || placeholder}
            <ChevronsUpDown className="ml-2 h-4 w-4 opacity-50" />
          </Button>
        </PopoverTrigger>
        <PopoverContent forceMount className="w-[200px] p-0 select-content-ps">
          <Command>
            <CommandInput placeholder="Rechercher..." className="h-9" />
            <CommandList>
              <CommandEmpty>Aucune correspondance.</CommandEmpty>
              <CommandGroup>
                {options.map((option) => (
                    <CommandItem
                        key={option.value}
                        value={option.value}
                        onSelect={(currentValue) => {
                          setValue(currentValue);
                          onSelect(currentValue);
                          setOpen(false);
                        }}
                    >
                      {option.label}
                      <Check
                          className={cn(
                              "ml-auto h-4 w-4",
                              value === option.value ? "opacity-100" : "opacity-0"
                          )}
                      />
                    </CommandItem>
                ))}
              </CommandGroup>
            </CommandList>
          </Command>
        </PopoverContent>
      </Popover>
  );
}
