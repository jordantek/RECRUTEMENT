import { Trash2, Pencil, Download, Send, Eye, SearchIcon, Loader2, RefreshCw, Check, X } from "lucide-react";
import {
    Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from "@/components/ui/table";
import React, { useState } from "react";
import { Input } from "@/components/ui/input";
import { ArrowRightIcon } from "@radix-ui/react-icons";
import { Button } from "@/components/ui/button.tsx";

export interface EditableTableColumn<T> {
    key: keyof T | string;
    label: string;
    editable?: boolean;
    inputType?: 'text' | 'number' | 'date' | 'checkbox';
    render?: (value: any, row: T) => React.ReactNode;
    renderEdit?: (value: any, row: T, onChange: (newValue: any) => void) => React.ReactNode;
}

export interface EditableTableProps<T> {
    columns: EditableTableColumn<T>[];
    data: T[];
    onAdd?: () => void;
    onEdit?: (row: T) => void;
    onDelete?: (row: T) => void;
    onDownload?: (row: T) => void;
    onView?: (row: T) => void;
    onSend?: (row: T) => void;
    onSave?: (row: T) => Promise<void> | void;
    onCancel?: (row: T) => void;
    isLoading?: boolean;
    emptyText?: string;
    onFilter?: (value: string) => void;
    filterPlaceholder?: string;
    onRefresh?: () => void;
}

export const EditableTable = <T extends Record<string, any>>({
                                                                 columns,
                                                                 data = [],
                                                                 onEdit,
                                                                 onDelete,
                                                                 onDownload,
                                                                 onView,
                                                                 onSend,
                                                                 onSave,
                                                                 onCancel,
                                                                 isLoading = false,
                                                                 emptyText = "Aucune donnée disponible.",
                                                                 onRefresh,
                                                                 onFilter,
                                                                 filterPlaceholder = "Rechercher...",
                                                             }: EditableTableProps<T>) => {
    const [search, setSearch] = useState("");
    const [editingRow, setEditingRow] = useState<T | null>(null);
    const [editedRow, setEditedRow] = useState<Partial<T>>({});
    const [isSaving, setIsSaving] = useState(false);

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

    const handleEdit = (row: T) => {
        setEditingRow(row);
        setEditedRow({ ...row });
    };

    const handleSave = async () => {
        if (!editingRow || !onSave) return;

        setIsSaving(true);
        try {
            await onSave({ ...editingRow, ...editedRow });
            setEditingRow(null);
            setEditedRow({});
        } finally {
            setIsSaving(false);
        }
    };

    const handleCancel = () => {
        if (!editingRow) return;

        onCancel?.(editingRow);
        setEditingRow(null);
        setEditedRow({});
    };

    const handleChange = (key: string, value: any) => {
        setEditedRow(prev => ({
            ...prev,
            [key]: value
        }));
    };

    const renderEditableCell = (col: EditableTableColumn<T>, row: T) => {
        const rawValue = getValueFromKey(editingRow === row ? { ...row, ...editedRow } : row, col.key as string);

        if (editingRow === row && col.editable) {
            if (col.renderEdit) {
                return col.renderEdit(rawValue, row, (newValue) => handleChange(col.key as string, newValue));
            }

            switch (col.inputType) {
                case 'number':
                    return (
                        <Input
                            type="number"
                            value={rawValue || ''}
                            onChange={(e) => handleChange(col.key as string, e.target.valueAsNumber)}
                            className="h-8 w-full"
                        />
                    );
                case 'date':
                    return (
                        <Input
                            type="date"
                            value={rawValue instanceof Date ? rawValue.toISOString().split('T')[0] : rawValue || ''}
                            onChange={(e) => handleChange(col.key as string, new Date(e.target.value))}
                            className="h-8 w-full"
                        />
                    );
                case 'checkbox':
                    return (
                        <input
                            type="checkbox"
                            checked={!!rawValue}
                            onChange={(e) => handleChange(col.key as string, e.target.checked)}
                            className="h-4 w-4"
                        />
                    );
                default:
                    return (
                        <Input
                            type="text"
                            value={rawValue || ''}
                            onChange={(e) => handleChange(col.key as string, e.target.value)}
                            className="h-8 w-full"
                        />
                    );
            }
        }

        return col.render ? col.render(rawValue, row) : renderValue(rawValue);
    };

    return (
        <div className="mb-4 space-y-2">
            {onFilter && (
                <div className="*:not-first:mt-2">
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
                        <button
                            className="text-muted-foreground/80 hover:text-foreground focus-visible:border-ring focus-visible:ring-ring/50 absolute inset-y-0 end-0 flex h-full w-9 items-center justify-center rounded-e-md transition-[color,box-shadow] outline-none focus:z-10 focus-visible:ring-[3px] disabled:pointer-events-none disabled:cursor-not-allowed disabled:opacity-50"
                            aria-label="Submit search"
                            type="submit"
                        >
                            <ArrowRightIcon aria-hidden="true" />
                        </button>
                    </div>
                </div>
            )}

            <div className="overflow-x-auto rounded-lg border bg-white">
                <Table className="w-full text-sm font-inter">
                    <TableHeader className="bg-blue-ps-50">
                        <TableRow>
                            {columns.map((col, idx) => (
                                <TableHead key={idx} className="text-left text-xs font-semibold py-2 whitespace-nowrap">
                                    {col.label}
                                </TableHead>
                            ))}
                            {(onEdit || onDelete || onDownload || onView || onSend || onSave) && (
                                <TableHead className="text-center text-xs font-semibold py-2">
                                    Actions
                                </TableHead>
                            )}
                        </TableRow>
                    </TableHeader>

                    <TableBody>
                        {isLoading ? (
                            <TableRow>
                                <TableCell colSpan={columns.length + 1} className="text-center py-6 text-muted-foreground">
                                    Chargement en cours...
                                </TableCell>
                            </TableRow>
                        ) : data.length > 0 ? (
                            data.map((row, rowIndex) => (
                                <TableRow key={rowIndex} className="hover:bg-muted/50">
                                    {columns.map((col, colIndex) => (
                                        <TableCell
                                            key={colIndex}
                                            className="py-2 text-left border-b truncate whitespace-nowrap max-w-[200px]"
                                        >
                                            {renderEditableCell(col, row)}
                                        </TableCell>
                                    ))}
                                    <TableCell className="py-1 px-4 border-b text-center">
                                        <div className="flex space-x-1 items-center justify-center">
                                            {editingRow === row ? (
                                                <>
                                                    {onSave && (
                                                        <Button
                                                            variant="ghost"
                                                            size="sm"
                                                            onClick={handleSave}
                                                            disabled={isSaving}
                                                        >
                                                            {isSaving ? (
                                                                <Loader2 className="h-4 w-4 animate-spin" />
                                                            ) : (
                                                                <Check className="h-4 w-4 text-green-600" />
                                                            )}
                                                        </Button>
                                                    )}
                                                    {onCancel && (
                                                        <Button
                                                            variant="ghost"
                                                            size="sm"
                                                            onClick={handleCancel}
                                                            disabled={isSaving}
                                                        >
                                                            <X className="h-4 w-4 text-red-600" />
                                                        </Button>
                                                    )}
                                                </>
                                            ) : (
                                                <>
                                                    {onView && (
                                                        <Eye
                                                            size={30}
                                                            className="bg-gray-100 p-2 rounded text-gray-600 cursor-pointer border border-transparent hover:bg-gray-200 hover:border hover:border-gray-400"
                                                            onClick={() => onView(row)}
                                                        />
                                                    )}
                                                    {onDownload && (
                                                        <Download
                                                            size={30}
                                                            className="bg-green-100 p-2 rounded text-green-600 cursor-pointer border border-transparent hover:bg-green-200 hover:border hover:border-green-500"
                                                            onClick={() => onDownload(row)}
                                                        />
                                                    )}
                                                    {onSend && (
                                                        <Send
                                                            size={30}
                                                            className="bg-purple-100 p-2 rounded text-purple-600 cursor-pointer border border-transparent hover:bg-purple-200 hover:border hover:border-purple-500"
                                                            onClick={() => onSend(row)}
                                                        />
                                                    )}
                                                    {onEdit && (
                                                        <Pencil
                                                            size={30}
                                                            className="bg-blue-100 p-2 rounded text-blue-500 cursor-pointer border border-transparent hover:bg-blue-200 hover:border hover:border-blue-500"
                                                            onClick={() => handleEdit(row)}
                                                        />
                                                    )}
                                                    {onDelete && (
                                                        <Trash2
                                                            size={30}
                                                            className="bg-red-100 p-2 rounded text-red-500 cursor-pointer border border-transparent hover:bg-red-200 hover:border hover:border-red-500"
                                                            onClick={(e) => {
                                                                e.stopPropagation();
                                                                onDelete(row);
                                                            }}
                                                        />
                                                    )}
                                                </>
                                            )}
                                        </div>
                                    </TableCell>
                                </TableRow>
                            ))
                        ) : (
                            <TableRow>
                                <TableCell
                                    colSpan={columns.length + 1}
                                    className="py-6 text-center text-gray-500 border"
                                >
                                    {emptyText}
                                    {onRefresh && (
                                        <Button
                                            variant="outline"
                                            size="sm"
                                            onClick={() => onRefresh()}
                                            disabled={isLoading}
                                        >
                                            {isLoading ? (
                                                <Loader2 className="h-4 w-4 animate-spin" />
                                            ) : (
                                                <RefreshCw className="h-4 w-4" />
                                            )}
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