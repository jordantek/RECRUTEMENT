import { Label } from "@/components/ui/label"
import MultipleSelector, { Option } from "@/components/ui/multiselect"

interface MultiSelectWithSearchProps {
    label?: string
    options: Option[]
    placeholder?: string
    value?: Option[]
    onChange?: (selected: Option[]) => void
    emptyIndicator?: React.ReactNode
}

export default function MultiSelectWithSearch({
                                                  label,
                                                  options,
                                                  placeholder = "Sélectionner...",
                                                  value,
                                                  onChange,
                                                  emptyIndicator,
                                              }: MultiSelectWithSearchProps) {
    return (
        <div className="*:not-first:mt-2">
            {label && <Label>{label}</Label>}
            <MultipleSelector
                commandProps={{ label: placeholder }}
                defaultOptions={options}
                placeholder={placeholder}
                value={value}
                onChange={onChange}
                emptyIndicator={emptyIndicator ?? <p className="text-center text-sm">Aucun résultat</p>}
            />
        </div>
    )
}