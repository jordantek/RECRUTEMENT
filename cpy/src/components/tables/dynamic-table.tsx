import { Button } from "@/components/ui/button";
import { Plus, Trash2, Pencil } from "lucide-react";
import {
    Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from "@/components/ui/table";

interface DynamicTableProps {
    columns: { key: string; label: string }[];
    data: Record<string, any>[];
    onAdd: () => void;
    onEdit?: (row: Record<string, any>) => void;
    onDelete?: (row: Record<string, any>) => void;
}

export const DynamicTable = ({
                                 columns,
                                 data,
                                 onAdd,
                                 onEdit,
                                 onDelete
                             }: DynamicTableProps) => {
    return (
        <div className="mb-4">
            <div className="flex justify-end">
                <Button
                    variant="ghost"
                    onClick={(e) => {
                        e.stopPropagation();
                        onAdd();
                    }}
                    className="mb-2 inline-flex items-center justify-center w-10 h-10 p-0 rounded-full hover:bg-gray-200 pointer-events-auto"
                    tabIndex={-1}
                    type="button"
                >
                    <Plus size={16} />
                </Button>
            </div>
            <div className="overflow-auto w-full">
                <Table className="min-w-full rounded-lg bg-white dark:bg-zinc-800 border border-gray-200 dark:border-zinc-700">
                    <TableHeader className="bg-gray-100 dark:bg-zinc-700 sticky top-0">
                        <TableRow>
                            {columns.map((col, idx) => (
                                <TableHead key={idx} className="h-8 text-left border-b">
                                    {col.label}
                                </TableHead>
                            ))}
                            {(onEdit || onDelete) && (
                                <TableHead className="h-8 text-left border-b">Actions</TableHead>
                            )}
                        </TableRow>
                    </TableHeader>
                    <TableBody>
                        {data.length > 0 ? (
                            data.map((row, rowIndex) => (
                                <TableRow key={rowIndex}>
                                    {columns.map((col, colIndex) => {
                                        let value = row[col.key];

                                        // Si c'est un objet Date, on le formate
                                        if (value instanceof Date) {
                                            value = value.toLocaleDateString("fr-FR");
                                        }

                                        // Si c'est une string mais qu'on détecte une date ISO
                                        if (typeof value === "string" && !isNaN(Date.parse(value))) {
                                            const parsed = new Date(value);
                                            if (!isNaN(parsed.getTime())) {
                                                value = parsed.toLocaleDateString("fr-FR");
                                            }
                                        }

                                        return (
                                            <TableCell key={colIndex} className="py-2 px-4 border-b">
                                                {value || "-"}
                                            </TableCell>
                                        );
                                    })}

                                    {(onEdit || onDelete) && (
                                        <TableCell className="py-2 px-4 border-b">
                                            <div className="flex space-x-2">
                                                {onEdit && (
                                                    <Button
                                                        size="icon"
                                                        variant="ghost"
                                                        onClick={() => onEdit(row)}
                                                    >
                                                        <Pencil size={14} />
                                                    </Button>
                                                )}
                                                {onDelete && (
                                                    <Button
                                                        size="icon"
                                                        variant="ghost"
                                                        onClick={(e) => {
                                                            e.stopPropagation();
                                                            onDelete(row);
                                                        }}
                                                    >
                                                        <Trash2 size={14} />
                                                    </Button>
                                                )}
                                            </div>
                                        </TableCell>
                                    )}
                                </TableRow>
                            ))
                        ) : (
                            <TableRow>
                                <TableCell
                                    colSpan={columns.length + (onEdit || onDelete ? 1 : 0)}
                                    className="text-center py-4 text-gray-500"
                                >
                                    Aucune donnée disponible.
                                </TableCell>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>
            </div>
        </div>
    );
};
