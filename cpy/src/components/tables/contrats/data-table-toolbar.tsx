import { Cross2Icon } from '@radix-ui/react-icons'
import { Table } from '@tanstack/react-table'
import { Button } from '@/components/ui/button.tsx'
import { Input } from '@/components/ui/input.tsx'
import { DataTableFacetedFilter } from './data-table-faceted-filter.tsx'
//import { DataTableViewOptions } from './data-table-view-options'
interface DataTableToolbarProps<TData> {
  table: Table<TData>
}

export function DataTableToolbar<TData>({
  table,
}: DataTableToolbarProps<TData>) {
  const isFiltered = table.getState().columnFilters.length > 0

  return (
    <div className='flex items-center justify-between'>
      <div className='flex flex-1 flex-col-reverse items-start gap-y-2 sm:flex-row sm:items-center sm:space-x-2'>
        <Input
          placeholder='Rechercher par le nom...'
          value={
            (table.getColumn('order_num')?.getFilterValue() as string) ?? ''
          }
          onChange={(event) => {
              console.log(table.getColumn('order_num'))
              table.getColumn('order_num')?.setFilterValue(event.target.value)
            }
          }
          className='h-8 w-[160px] lg:w-[280px]'
        />
        <div className=' gap-x-2 hidden'>
          {table.getColumn('payment') && (
            <DataTableFacetedFilter
              column={table.getColumn('ban_statut_text')}
              title='Payements'
              options={[
                { label: 'Payé', value: 'payé'},
                { label: 'Non payé', value: 'non payé' },
              ]}
            />
          )}
          {table.getColumn('delivery') && (
            <DataTableFacetedFilter
              column={table.getColumn('ban_statut_text')}
              title='Livraisons'
              options={[
                { label: 'En attente', value: 'En attente'},
                { label: 'En cours', value: 'En cours' },
                { label: 'Livré', value: 'Livré' },
              ]}
            />
          )}
        </div>
        {isFiltered && (
          <Button
            variant='ghost'
            onClick={() => table.resetColumnFilters()}
            className='h-8 px-2 lg:px-3'
          >
            Reset
            <Cross2Icon className='ml-2 h-4 w-4' />
          </Button>
        )}
      </div>
      {/*<DataTableViewOptions table={table } />*/}
    </div>
  )
}
