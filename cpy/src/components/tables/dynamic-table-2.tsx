
import { Trash2, Pencil } from "lucide-react";
import {
    Table, TableBody, TableCell, TableHead, TableHeader, TableRow,
} from "@/components/ui/table";

interface DynamicTableColumn<T> {
    key: keyof T;
    label: string;
    render?: (value: any, row: T) => React.ReactNode;
}

interface DynamicTableProps<T> {
    columns: DynamicTableColumn<T>[];
    data: T[];
    onAdd?: () => void;
    onEdit?: (row: T) => void;
    onDelete?: (row: T) => void;
}

export const DynamicTable2 = <T extends Record<string, any>>({
                                                                columns,
                                                                data,
                                                               /* onAdd,*/
                                                                onEdit,
                                                                onDelete,
                                                            }: DynamicTableProps<T>) => {
    return (
        <div className="mb-4">
            <div className="overflow-auto w-full bg-gray-50 p-2 rounded-lg">
                <Table className="min-w-full text-xs font-inter">
                    <TableHeader className=" text-blue-900 font-inter text-xs ">
                        <TableRow>
                            {columns.map((col, idx) => (
                                <TableHead
                                    key={idx}
                                    className="h-8 text-left border-b font-semibold tracking-wide text-xs  py-1"
                                >
                                    {col.label}
                                </TableHead>
                            ))}
                            {(onEdit || onDelete) && (
                                <TableHead className="h-6 border-b font-semibold tracking-wide text-xs  py-1 text-center">
                                    Actions
                                </TableHead>
                            )}
                        </TableRow>
                    </TableHeader>

                    <TableBody>
                        {data.length > 0 ? (
                            data.map((row, rowIndex) => (
                                <TableRow key={rowIndex}>
                                    {columns.map((col, colIndex) => {
                                        const rawValue = row[col.key];

                                        // Formatage dans une variable temporaire
                                        let displayValue: any = rawValue;

                                        if (
                                            displayValue instanceof Date ||
                                            (typeof displayValue === "string" && !isNaN(Date.parse(displayValue)))
                                        ) {
                                            const date = new Date(displayValue);
                                            if (!isNaN(date.getTime())) {
                                                displayValue = date.toLocaleDateString("fr-FR");
                                            }
                                        }

                                        return (
                                            <TableCell key={colIndex} className="py-1 px-4 border-b">
                                                {col.render ? col.render(displayValue, row) : displayValue || "-"}
                                            </TableCell>
                                        );
                                    })}

                                    {(onEdit || onDelete) && (
                                        <TableCell className="py-1 px-4 border-b ">
                                            <div className="flex space-x-1 items-center justify-center ">
                                                {onEdit && (

                                                        <Pencil
                                                            size={30}
                                                            className={"bg-blue-100 p-2 rounded text-blue-500 cursor-pointer border border-transparent hover:bg-blue-200 hover:border hover:border-blue-500"}
                                                            onClick={() => onEdit(row)}/>

                                                )}
                                                {onDelete && (
                                                    <Trash2
                                                        size={30}
                                                        className={"bg-red-100 p-2 rounded text-red-500 cursor-pointer border border-transparent hover:bg-red-200 hover:border hover:border-red-500"}
                                                        onClick={(e) => {
                                                            e.stopPropagation();
                                                            onDelete(row);
                                                        }}/>

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
