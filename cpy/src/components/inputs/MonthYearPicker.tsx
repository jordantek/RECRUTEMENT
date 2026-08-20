"use client"

import { useState, useEffect } from "react"
import DatePicker from "react-datepicker"
import "react-datepicker/dist/react-datepicker.css"
import { format } from "date-fns"
import { fr } from "date-fns/locale"
import { Calendar as CalendarIcon } from "lucide-react"

import { Button } from "@/components/ui/button"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"
import {
    Select,
    SelectTrigger,
    SelectValue,
    SelectContent,
    SelectItem,
} from "@/components/ui/select"
import { cn } from "@/lib/utils"

interface MonthYearPickerProps {
    value?: string | null
    onChange?: (value: string | null) => void
    placeholder?: string
    className?: string
    displayFormat?: string
    valueFormat?: string
    disabled?: boolean
}

export function MonthYearPicker({
                                    value,
                                    onChange,
                                    placeholder = "Sélectionner un mois",
                                    className = "",
                                    displayFormat = "MMMM yyyy",
                                    valueFormat = "yyyy-MM",
                                    disabled = false,
                                }: MonthYearPickerProps) {
    const [date, setDate] = useState<Date | null>(null)
    const [isOpen, setIsOpen] = useState(false)

    useEffect(() => {
        if (value) {
            const parts = value.split("-")
            const parsedDate = new Date(Number(parts[0]), Number(parts[1]) - 1)
            setDate(parsedDate)
        } else {
            setDate(null)
        }
    }, [value])

    const handleChange = (selectedDate: Date | null) => {
        setDate(selectedDate)
        if (selectedDate) {
            const formatted = format(selectedDate, valueFormat, { locale: fr })
            onChange?.(formatted)
        } else {
            onChange?.(null)
        }
        setIsOpen(false)
    }

    const currentYear = new Date().getFullYear()
    const years = Array.from({ length: 20 }, (_, i) => currentYear - 10 + i)
    const months = Array.from({ length: 12 }, (_, i) =>
        format(new Date(0, i), "MMMM", { locale: fr })
    )

    const renderMonthContent = (month: number, shortMonthName: string) => {
        const isCurrent = new Date().getMonth() === month && new Date().getFullYear() === date?.getFullYear()
        const isSelected = date?.getMonth() === month

        return (
            <div className={cn(
                "w-full h-full flex items-center justify-center p-2 rounded-md transition-colors",
                isSelected ? "bg-blue-600 text-white font-medium" :
                    isCurrent ? "bg-blue-100" :
                        "hover:bg-gray-100"
            )}>
        <span className="text-sm">
          {shortMonthName.slice(0, 3)} {/* Format court sur 3 lettres */}
        </span>
            </div>
        )
    }

    return (
        <Popover open={isOpen} onOpenChange={setIsOpen}>
            <PopoverTrigger asChild>
                <Button
                    variant="outline"
                    disabled={disabled}
                    className={cn(
                        "w-[240px] justify-start text-left font-normal",
                        !date && "text-muted-foreground",
                        className
                    )}
                >
                    <CalendarIcon className="mr-2 h-4 w-4" />
                    {date ? (
                        <span className="font-medium">
              {format(date, displayFormat, { locale: fr })}
            </span>
                    ) : (
                        <span>{placeholder}</span>
                    )}
                </Button>
            </PopoverTrigger>

            <PopoverContent className="w-auto border shadow-lg rounded-lg bg-white p-0">
                <DatePicker
                    selected={date}
                    onChange={handleChange}
                    inline
                    showMonthYearPicker
                    dateFormat={displayFormat}
                    locale={fr}
                    preventOpenOnFocus
                    className="border-none bg-white"
                    calendarClassName="border-none bg-white p-3"
                    dayClassName={() => "hidden"}
                    popperClassName="bg-white"
                    wrapperClassName="bg-white"
                    renderMonthContent={renderMonthContent}
                    renderCustomHeader={({ date: currentDate, changeYear, changeMonth }) => (
                        <div className="flex items-center justify-between gap-2 px-3 py-2 bg-white ">
                            <Select
                                value={currentDate.getMonth().toString()}
                                onValueChange={(value) => changeMonth(parseInt(value))}
                            >
                                <SelectTrigger className="w-[140px] h-8 text-sm font-medium bg-white">
                                    <SelectValue placeholder="Mois" />
                                </SelectTrigger>
                                <SelectContent className="max-h-[240px] overflow-y-auto bg-white">
                                    {months.map((month, index) => (
                                        <SelectItem
                                            key={index}
                                            value={index.toString()}
                                            className="capitalize hover:bg-gray-100"
                                        >
                                            {month}
                                        </SelectItem>
                                    ))}
                                </SelectContent>
                            </Select>

                            <Select
                                value={currentDate.getFullYear().toString()}
                                onValueChange={(value) => changeYear(parseInt(value))}
                            >
                                <SelectTrigger className="w-[100px] h-8 text-sm font-medium bg-white">
                                    <SelectValue placeholder="Année" />
                                </SelectTrigger>
                                <SelectContent className="max-h-[240px] overflow-y-auto bg-white">
                                    {years.map((year) => (
                                        <SelectItem
                                            key={year}
                                            value={year.toString()}
                                            className="hover:bg-gray-100"
                                        >
                                            {year}
                                        </SelectItem>
                                    ))}
                                </SelectContent>
                            </Select>
                        </div>
                    )}
                />
            </PopoverContent>
        </Popover>
    )
}