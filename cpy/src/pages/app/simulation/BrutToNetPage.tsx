import { useEffect, useState } from "react";
import usePageTitleStore from "@/contexts/usePageTitleStore";
import PageTitle from "@/components/seo/pageTitle";
import { UserCog, Plus, Trash2, FileDown, MoreHorizontal } from "lucide-react";
import { Icon } from "@tabler/icons-react";
import * as XLSX from "xlsx";
import { saveAs } from "file-saver";

import {
  Table,
  TableHeader,
  TableRow,
  TableHead,
  TableBody,
  TableCell,
} from "@/components/ui/table";
import { ScrollArea, ScrollBar } from "@/components/ui/scroll-area";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";

type Result = {
  brut: number;
  brut_imp: number;
  arrondi: number;
  cnss: number;
  its: number;
  total_retenue: number;
  net: number;
};

export function BrutToNetPage() {
  const [salaireBruts, setSalaireBruts] = useState<number[]>([0]);
  const [results, setResults] = useState<Result[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    usePageTitleStore
      .getState()
      .setTitle("Salaire brut au net", "Calcul du salaire brut au salaire net", UserCog as Icon);
  }, []);

  const handleInputChange = (index: number, value: string) => {
    const updated = [...salaireBruts];
    updated[index] = parseFloat(value) || 0;
    setSalaireBruts(updated);
  };

  const handleAddField = () => {
    setSalaireBruts([...salaireBruts, 0]);
  };

  const handleRemoveField = (index: number) => {
    const updated = salaireBruts.filter((_, i) => i !== index);
    setSalaireBruts(updated);
  };

  const calculateNetFromBrut = (brut: number): Result => {
    let arrondi: number;

    if (brut < 1000) {
      arrondi = 0;
    } else {
      const calcdec = brut / 1000;
      const enti = Math.floor(calcdec);
      const decimal = calcdec - enti;
      arrondi = brut - 1000 * decimal;
    }

    const brut_imp = brut;
    const cnss = Math.round(brut_imp * 0.036);

    let its = 0;

    if (arrondi <= 60000) {
      its = 0;
    } else if (arrondi > 60000 && arrondi <= 150000) {
      its = (arrondi - 60000) * 0.1;
    } else if (arrondi > 150000 && arrondi <= 250000) {
      its = (150000 - 60000) * 0.1 + (arrondi - 150000) * 0.15;
    } else if (arrondi > 250000 && arrondi <= 500000) {
      its = (150000 - 60000) * 0.1 + (250000 - 150000) * 0.15 + (arrondi - 250000) * 0.19;
    } else if (arrondi > 500000) {
      its =
        (150000 - 60000) * 0.1 +
        (250000 - 150000) * 0.15 +
        (500000 - 250000) * 0.19 +
        (arrondi - 500000) * 0.3;
    }

    its = Math.max(its, 0);
    const total_retenue = cnss + its;
    const net = (brut - total_retenue);

    return {
      brut,
      brut_imp,
      arrondi,
      cnss,
      its,
      total_retenue,
      net,
    };
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setTimeout(() => {
      const calculated = salaireBruts.map(calculateNetFromBrut);
      setResults(calculated);
      setIsLoading(false);
    }, 1500);
  };

  const exportToExcel = () => {
    const worksheet = XLSX.utils.json_to_sheet(results);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, "BrutToNet");
    const excelBuffer = XLSX.write(workbook, { bookType: "xlsx", type: "array" });
    const blob = new Blob([excelBuffer], { type: "application/octet-stream" });
    saveAs(blob, "brut-au-net.xlsx");
  };

  return (
    <>
      <PageTitle title="Salaire brut au net" />
      <div className="p-6 max-w-4xl mx-auto space-y-6">
        <form onSubmit={handleSubmit} className="space-y-4">
          <h2 className="text-2xl font-semibold">Saisissez le salaire brut</h2>
          {salaireBruts.map((val, index) => (
            <div key={index} className="scale-[0.8] flex gap-2 items-center -ml-20">
              <input
                type="number"
                className="form-input px-4 py-2 border rounded w-full"
                placeholder="Montant brut"
                value={val || ""}
                onChange={(e) => handleInputChange(index, e.target.value)}
                disabled={isLoading}
              />
              {salaireBruts.length > 1 && (
                <button
                  type="button"
                  className="text-red-600 hover:text-red-800"
                  onClick={() => handleRemoveField(index)}
                  disabled={isLoading}
                >
                  <Trash2 size={20} />
                </button>
              )}
            </div>
          ))}
          <div className="flex gap-4 mt-2">
            <button
              type="button"
              onClick={handleAddField}
              className="scale-[0.8] btn btn-outline-primary flex items-center gap-1 text-blue-900 border-blue-900 hover:bg-blue-100"
              disabled={isLoading}
            >
              <Plus size={16} /> Ajouter
            </button>
            <button
              type="submit"
              className="scale-[0.8] btn bg-blue-900 text-white"
              disabled={isLoading}
            >
              {isLoading ? "Calcul en cours..." : "Valider"}
            </button>
          </div>
        </form>

        {isLoading && (
          <div className="flex justify-center mt-10">
            <div className="animate-spin rounded-full h-10 w-10 border-t-4 border-blue-900 border-solid"></div>
          </div>
        )}

        {!isLoading && results.length > 0 && (
          <div className="mt-8">
            <div className="flex justify-between items-center mb-2">
              <h3 className="text-xl font-bold">Résultats</h3>
              <button
                onClick={exportToExcel}
                className="scale-[0.8] flex items-center gap-1 bg-green-600 hover:bg-green-700 text-white font-medium py-2 px-4 rounded"
              >
                <FileDown size={16} /> Exporter Excel
              </button>
            </div>

            <ScrollArea className="bg-background rounded-md border m-1 max-h-[500px]">
              <div className="min-w-max">
                <Table>
                  <TableHeader className="bg-muted/40 sticky top-0 z-10">
                    <TableRow>
                      <TableHead className="pl-6">Brut</TableHead>
                      <TableHead>Brut imposable</TableHead>
                      <TableHead>Arrondi</TableHead>
                      <TableHead>CNSS</TableHead>
                      <TableHead>ITS</TableHead>
                      <TableHead>Total retenue</TableHead>
                      <TableHead>Net</TableHead>
                      <TableHead className="text-right pr-6">Actions</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {results.map((res, idx) => (
                      <TableRow key={idx}>
                        <TableCell className="pl-6">{res.brut.toFixed(2)}</TableCell>
                        <TableCell>{res.brut_imp.toFixed(2)}</TableCell>
                        <TableCell>{res.arrondi}</TableCell>
                        <TableCell>{res.cnss.toFixed(2)}</TableCell>
                        <TableCell>{res.its.toFixed(2)}</TableCell>
                        <TableCell>{res.total_retenue.toFixed(2)}</TableCell>
                        <TableCell>{res.net.toFixed(2)}</TableCell>
                        <TableCell className="text-right pr-6">
                          <DropdownMenu>
                            <DropdownMenuTrigger asChild>
                              <Button variant="ghost" className="h-8 w-8 p-0">
                                <MoreHorizontal className="h-4 w-4 text-muted-foreground" />
                              </Button>
                            </DropdownMenuTrigger>
                            <DropdownMenuContent align="end" className="w-40">
                              <DropdownMenuLabel>Actions</DropdownMenuLabel>
                              <DropdownMenuItem className="flex items-center gap-2 text-destructive">
                                <Trash2 className="h-4 w-4" /> Supprimer
                              </DropdownMenuItem>
                            </DropdownMenuContent>
                          </DropdownMenu>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </div>
              <ScrollBar orientation="horizontal" />
            </ScrollArea>
          </div>
        )}
      </div>
    </>
  );
}
