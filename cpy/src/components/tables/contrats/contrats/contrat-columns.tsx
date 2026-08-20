import { ColumnDef } from '@tanstack/react-table'

import { EllipsisIcon, Eye } from 'lucide-react'
import { Link } from 'react-router-dom'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu.tsx'

import {ContratEmploye} from "@/types/ContratType.ts";
import {DataTableColumnHeader} from "@/components/tables/contrats/data-table-column-header.tsx";

export const ContratColumns: ColumnDef<ContratEmploye>[] = [
  {
    accessorKey: 'nom',
    accessorFn: row => `${row.employe?.nom || ''} ${row.employe?.prenom || ''}`.trim() || null,
    header: ({ column }) => <DataTableColumnHeader column={column} title='Employé' />,
    cell: ({ row }) => <span>{row.getValue('nom') || '-'}</span>,
  },

  {
    accessorKey: 'libelle',
    accessorFn: row => row.departement?.libelle,
    header: ({ column }) => <DataTableColumnHeader column={column} title='Département' />,
    cell: ({ row }) => <span>{row.getValue('libelle') || '-'}</span>,
  },
  {
    accessorKey: 'libelle',
    accessorFn: row => row.poste?.libelle,
    header: ({ column }) => <DataTableColumnHeader column={column} title='Poste' />,
    cell: ({ row }) => <span>{row.getValue('libelle') || '-'}</span>,
  },
  {
    accessorKey: 'mouvementContrat',
    accessorFn: row => row.mouvementContrat,
    header: ({ column }) => <DataTableColumnHeader column={column} title='Mouvement' />,
    cell: ({ row }) => <span>{row.getValue('mouvementContrat') || '-'}</span>,
  },

  {
    accessorKey: 'dateDebut',
    header: ({ column }) => <DataTableColumnHeader column={column} title='Début' />,
    cell: ({ row }) => <span>{row.getValue('dateDebut') || '-'}</span>,

  },
  {
    accessorKey: 'dateFin',
    header: ({ column }) => <DataTableColumnHeader column={column} title='Fin' />,
    cell: ({ row }) => <span>{row.getValue('dateFin') || '-'}</span>,

  },

  {
    accessorKey: 'id',
    header: ({ column }) => <DataTableColumnHeader column={column} title='Action' className={'flex justify-center'} />,
    cell: ({ row }) => <div className={'flex justify-center'}>
      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <EllipsisIcon size={16} aria-hidden="true" aria-label="Open edit menu" className={"cursor-pointer"} />
        </DropdownMenuTrigger>
        <DropdownMenuContent>
          <DropdownMenuItem className={"cursor-pointer"}>  <Link key={row.getValue('order_num')} to={`/dashboard/orders/details/${row.getValue('id')}`}><span className={" flex items-center font-light text-black hover:text-blue-400"}><Eye size={16} className={"mr-2"}/>  Détails</span></Link></DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>
    </div>,
  },
]
