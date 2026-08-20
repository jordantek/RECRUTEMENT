import { DropdownMenuTrigger } from '@radix-ui/react-dropdown-menu'
import { MixerHorizontalIcon } from '@radix-ui/react-icons'
import { Table } from '@tanstack/react-table'
import { Button } from '@/components/ui/button.tsx'
import {
  DropdownMenu,
  DropdownMenuCheckboxItem,
  DropdownMenuContent,
  DropdownMenuLabel,
  DropdownMenuSeparator,
} from '@/components/ui/dropdown-menu.tsx'
import { Download } from 'lucide-react'
import { TableHelpers } from '@/helpers/TableHelpers.ts'
import jsPDF from 'jspdf';


interface DataTableViewOptionsProps<TData> {
  table: Table<TData>
}

export function DataTableViewOptions<TData>({
  table,
}: DataTableViewOptionsProps<TData>) {
  const generatePDF = () => {
    const doc = new jsPDF({ orientation: 'landscape' }); // Changer l'orientation en paysage

    doc.text("Liste des Commandes", 20, 10);

    doc.save("commandes.pdf");
  };

  return (
    <>
      <DropdownMenu modal={false}>
        <DropdownMenuTrigger asChild>
          <Button
            variant='outline'
            size='sm'
            className='ml-auto hidden h-8 lg:flex'>
            <MixerHorizontalIcon className='mr-2 h-4 w-4' />
            Colonnes
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent align='end' className='w-[150px]'>
          <DropdownMenuLabel>Toggle columns</DropdownMenuLabel>
          <DropdownMenuSeparator />
          {table
            .getAllColumns()
            .filter(
              (column) =>
                typeof column.accessorFn !== 'undefined' && column.getCanHide()
            )
            .map((column) => {
              return (
                <DropdownMenuCheckboxItem
                  key={column.id}
                  className='capitalize'
                  checked={column.getIsVisible()}
                  onCheckedChange={(value) => column.toggleVisibility(!!value)}
                >
                  {column.id == 'ban_statut_text' ? 'Status' : column.id == "account_type_text" ? "Type" : column.id}
                </DropdownMenuCheckboxItem>
              )
            })}
        </DropdownMenuContent>
      </DropdownMenu>
      <Button onClick={() => {generatePDF()}} size={'sm'} className=' space-x-1 bg-red-500 mx-2  hover:bg-red-600'>
        <Download size={18} className="mr-1" />
        <span>PDF</span>
      </Button>
      <Button onClick={() => {TableHelpers.exportToCSV({table, filename: "accounts"})}} size={'sm'} className='space-x-1 bg-gray-800 hover:bg-gray-900'>
        <Download size={18} className="mr-1" />
        <span>CSV</span>
      </Button>
    </>
  )
}
