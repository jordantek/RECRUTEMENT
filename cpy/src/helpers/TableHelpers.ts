import Papa from 'papaparse';
import { Table } from '@tanstack/react-table';

export class TableHelpers  {
    static exportToCSV <TData>({table, filename} : {table: Table<TData>, filename: string}) {
        const csvData = table.getRowModel().rows.map((row) =>
            row.original
        );
        const csv = Papa.unparse(csvData);
        const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
        const url = URL.createObjectURL(blob);

        const link = document.createElement('a');
        link.setAttribute('href', url);
        link.setAttribute('download', filename+'.csv');
        link.click();
  };
}

