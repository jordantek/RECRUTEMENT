import { Trash2, Pencil, Download, Send, Eye, SearchIcon, Loader2, RefreshCw } from "lucide-react";
import {
    Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from "@/components/ui/table";
import React, { useState } from "react";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button.tsx";
// ...imports inchangés

export interface DynamicTableColumn<T> {
    key: keyof T | string;
    label: string;
    editable?: boolean;
    inputType?: 'text' | 'number' | 'date' | 'checkbox' | 'select';
    render?: (value: any, row: T) => React.ReactNode;
    className?: string;
}

export interface DynamicTableProps<T> {
    columns: DynamicTableColumn<T>[];
    data: T[];
    onAdd?: () => void;
    onEdit?: (row: T) => void;
    onDelete?: (row: T) => void;
    onDownload?: (row: T) => void;
    onView?: (row: T) => void;
    onSend?: (row: T) => void;
    onAction?: (row: T) => {
        icon: React.ReactNode;
        onClick: () => void;
    } | null;
    isLoading?: boolean;
    emptyText?: string;
    onFilter?: (value: string) => void;
    filterPlaceholder?: string;
    onRefresh?: () => void;
    onChangeInput?: (row: T, key: keyof T | string, value: any) => void;
}

export const DynamicTable3 = <T extends Record<string, any>>({
                                                                 columns,
                                                                 data = [],
                                                                 onEdit,
                                                                 onDelete,
                                                                 onDownload,
                                                                 onView,
                                                                 onSend,
                                                                 onAction,
                                                                 isLoading = false,
                                                                 emptyText = "Aucune donnée disponible.",
                                                                 onRefresh,
                                                                 onFilter,
                                                                 filterPlaceholder = "Rechercher...",
                                                                 onChangeInput,
                                                             }: DynamicTableProps<T>) => {
    const [search, setSearch] = useState("");

    const getValueFromKey = (row: any, key: string): any => {
        return key.split('.').reduce((acc, part) => acc?.[part], row);
    };

    const renderValue = (rawValue: any): React.ReactNode => {
        if (typeof rawValue === "boolean") return rawValue ? "Oui" : "Non";
        if (rawValue instanceof Date || (typeof rawValue === "string" && !isNaN(Date.parse(rawValue)))) {
            const date = new Date(rawValue);
            return !isNaN(date.getTime()) ? date.toLocaleDateString("fr-FR") : "-";
        }
        if (Array.isArray(rawValue)) return rawValue.length ? rawValue.join(", ") : "-";
        if (typeof rawValue === "object" && rawValue !== null) return JSON.stringify(rawValue);
        if (rawValue !== null && rawValue !== undefined && rawValue !== "") {
            const str = String(rawValue);
            return str.length > 50 ? str.slice(0, 50) + "..." : str;
        }
        return "-";
    };

    const handleSearch = (e: React.ChangeEvent<HTMLInputElement>) => {
        const val = e.target.value;
        setSearch(val);
        onFilter?.(val);
    };

    return (
        <div className="mb-4 space-y-2">
            {onFilter && (
                <div className="*:not-first:mt-2 w-fit">
                    <div className="relative">
                        <Input
                            type="text"
                            placeholder={filterPlaceholder}
                            value={search}
                            onChange={handleSearch}
                            className="w-full max-w-xs text-sm peer ps-9 pe-9"
                        />
                        <div className="text-muted-foreground/80 pointer-events-none absolute inset-y-0 start-0 flex items-center justify-center ps-3 peer-disabled:opacity-50">
                            <SearchIcon size={16} />
                        </div>
                    </div>
                </div>
            )}

            <div className="overflow-x-auto rounded-lg border bg-white">
                <Table className="w-full text-sm font-inter">
                    <TableHeader className="bg-blue-ps-50">
                        <TableRow>
                            {columns.map((col, idx) => (
                                <TableHead
                                    key={idx}
                                    className={`text-left text-xs font-semibold py-2 ${col.className?.includes('whitespace-normal') ? 'whitespace-normal' : 'whitespace-nowrap'}`}
                                >
                                    {col.label}
                                </TableHead>
                            ))}
                            {(onEdit || onDelete || onDownload || onView || onSend) && (
                                <TableHead className="text-center text-xs font-semibold py-2">Actions</TableHead>
                            )}
                        </TableRow>
                    </TableHeader>

                    <TableBody>
                        {isLoading ? (
                            <TableRow>
                                <TableCell colSpan={columns.length + 1} className="py-6 text-center text-blue-600">
                                    <div className="flex flex-col items-center justify-center space-x-2">
                                        <Loader2 className="w-5 h-5 animate-spin text-blue-600" />
                                        <span>Chargement en cours...</span>
                                    </div>
                                </TableCell>
                            </TableRow>
                        ) : data.length > 0 ? (
                            data.map((row, rowIndex) => (
                                <TableRow key={rowIndex} className="hover:bg-muted/50 text-sm">
                                    {columns.map((col, colIndex) => {
                                        const rawValue = getValueFromKey(row, col.key as string);
                                        if (col.editable) {
                                            return (
                                                <TableCell key={colIndex} className={`py-2 text-left border-b text-sm ${col.className || 'truncate whitespace-nowrap max-w-[200px]'}`}>
                                                    <Input
                                                        type={col.inputType ?? "number"}
                                                        id={col.key as string}
                                                        value={col.render ? String(col.render(rawValue, row)) : String(rawValue ?? "")}
                                                        onChange={(e) => {
                                                            const newValue = e.target.value;
                                                            onChangeInput?.(row, col.key, col.inputType === "number" ? Number(newValue) : newValue);
                                                        }}
                                                        className="h-8 w-full"
                                                    />
                                                </TableCell>
                                            );
                                        }
                                        return (
                                            <TableCell key={colIndex} className={`py-1 text-left border-b text-xs ${col.className || 'truncate whitespace-nowrap max-w-[200px]'}`}>
                                                {col.render ? col.render(rawValue, row) : renderValue(rawValue)}
                                            </TableCell>
                                        );
                                    })}

                                    {(onEdit || onDelete || onDownload || onView || onSend || onAction) && (
                                        <TableCell className="py-1 px-4 border-b text-center">
                                            <div className="flex space-x-1 items-center justify-center">
                                                {onView && (<Eye size={30} className="bg-gray-100 p-2 rounded text-gray-600 cursor-pointer border hover:bg-gray-200" onClick={() => onView(row)} />)}
                                                {onDownload && (<Download size={30} className="bg-green-100 p-2 rounded text-green-600 cursor-pointer border hover:bg-green-200" onClick={() => onDownload(row)} />)}
                                                {onSend && (<Send size={30} className="bg-purple-100 p-2 rounded text-purple-600 cursor-pointer border hover:bg-purple-200" onClick={() => onSend(row)} />)}
                                                {onEdit && (<Pencil size={30} className="bg-blue-100 p-2 rounded text-blue-500 cursor-pointer border hover:bg-blue-200" onClick={() => onEdit(row)} />)}
                                                {onDelete && (<Trash2 size={30} className="bg-red-100 p-2 rounded text-red-500 cursor-pointer border hover:bg-red-200" onClick={(e) => { e.stopPropagation(); onDelete(row); }}  />)}
                                                {onAction && (() => {
                                                    const action = onAction(row);
                                                    if (!action || !action.icon || !action.onClick) return null;
                                                    return (
                                                        <div className="bg-gray-100 p-2 rounded cursor-pointer border hover:bg-gray-200" onClick={(e) => { e.stopPropagation(); action.onClick(); }}>
                                                            {action.icon}
                                                        </div>
                                                    );
                                                })()}
                                            </div>
                                        </TableCell>
                                    )}
                                </TableRow>
                            ))
                        ) : (
                            <TableRow>
                                <TableCell colSpan={columns.length + 1} className="py-6 text-center text-gray-500 border">
                                    {emptyText}<br/>
                                    {onRefresh && (
                                        <Button variant="outline" size="sm" onClick={onRefresh} disabled={isLoading} className="mt-2">
                                            {isLoading ? (<Loader2 className="h-4 w-4 animate-spin" />) : (<RefreshCw className="h-4 w-4" />)}
                                            <span className="ml-2">Actualiser</span>
                                        </Button>
                                    )}
                                </TableCell>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>
            </div>
        </div>
    );
};
