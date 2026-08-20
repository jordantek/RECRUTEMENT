import { useEffect, useState } from "react";
import usePageTitleStore from "@/contexts/usePageTitleStore";
import PageTitle from "@/components/seo/pageTitle";
import { UserCog, Plus, Trash2, FileDown } from "lucide-react";
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
import { ScrollArea } from "@/components/ui/scroll-area";
import { Button } from "@/components/ui/button";

type Result = {
  brut: number;
  brut_imp: number;
  arrondi: number;
  cnss: number;
  its: number;
  total_retenue: number;
  net: number;
};

export function NetToBrutPage() {
  const [salaireNets, setSalaireNets] = useState<number[]>([0]);
  const [results, setResults] = useState<Result[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    usePageTitleStore
      .getState()
      .setTitle(
        "Salaire net au brut",
        "Calcul du salaire net au salaire brut",
        UserCog as Icon
      );
  }, []);

  const handleInputChange = (index: number, value: string) => {
    const updated = [...salaireNets];
    updated[index] = parseFloat(value) || 0;
    setSalaireNets(updated);
  };

  const handleAddField = () => {
    setSalaireNets([...salaireNets, 0]);
  };

  const handleRemoveField = (index: number) => {
    const updated = salaireNets.filter((_, i) => i !== index);
    setSalaireNets(updated);
  };
  const calculateBrutFromNet = (netConnu: number): Result => {
    let minBrut = netConnu;
    let maxBrut = netConnu * 2;
    let bestResult: Result | null = null;
  
    const tolerance = 1; // accepter un écart de ±1
  
    while (minBrut <= maxBrut) {
      const brut = Math.floor((minBrut + maxBrut) / 2);
  
      // --- Arrondi ---
      let arrondi: number;
      if (brut < 1000) {
        arrondi = 0;
      } else {
        const calcdec = brut / 1000;
        const enti = Math.floor(calcdec);
        const dec = calcdec - enti;
        arrondi = brut - 1000 * dec;
      }
  
      const brutImp = brut;
      const cnss = Math.round(brutImp * 0.036);
  
      let its = 0;
      if (arrondi <= 60000) {
        its = 0;
      } else if (arrondi > 60000 && arrondi <= 250000) {
        its = (arrondi - 60000) * 0.1;
      } else if (arrondi > 250000 && arrondi <= 500000) {
        its = (150000 - 60000) * 0.1 + (250000 - 150000) * 0.15 + (arrondi - 250000) * 0.19;
      } else if (arrondi > 500000) {
        its = (150000 - 60000) * 0.1 + (250000 - 150000) * 0.15 + (500000 - 250000) * 0.19 + (arrondi - 500000) * 0.3;
      }
  
      const totalRetenue = cnss + its;
      const monnet = brut - totalRetenue;
  
      const diff = monnet - netConnu;
  
      if (Math.abs(diff) <= tolerance) {
        bestResult = {
          brut,
          brut_imp: brutImp,
          arrondi,
          cnss,
          its,
          total_retenue: totalRetenue,
          net: monnet,
        };
        break; // trouvé
      }
  
      if (monnet < netConnu) {
        minBrut = brut + 1;
      } else {
        maxBrut = brut - 1;
      }
    }
  
    if (!bestResult) {
      // fallback si pas trouvé (très improbable)
      return {
        brut: 0,
        brut_imp: 0,
        arrondi: 0,
        cnss: 0,
        its: 0,
        total_retenue: 0,
        net: 0,
      };
    }
  
    return bestResult;
  };
  

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);

    setTimeout(() => {
      const calculated = salaireNets.map(calculateBrutFromNet);
      setResults(calculated);
      setIsLoading(false);
    }, 500); // délai court pour UI fluide
  };

  const exportToExcel = () => {
    const worksheet = XLSX.utils.json_to_sheet(results);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, "NetAuBrut");
    const excelBuffer = XLSX.write(workbook, {
      bookType: "xlsx",
      type: "array",
    });
    const blob = new Blob([excelBuffer], { type: "application/octet-stream" });
    saveAs(blob, "net-au-brut.xlsx");
  };

  return (
    <>
      <PageTitle title="Salaire net au brut" />
      <div className="p-6 max-w-4xl mx-auto space-y-6">
        <form onSubmit={handleSubmit} className="space-y-4">
          <h2 className="text-2xl font-semibold">
            Saisissez le salaire net
          </h2>
          {salaireNets.map((val, index) => (
            <div
              key={index}
              className="flex gap-2 items-center scale-[0.8] -ml-20"
            >
              <input
                type="number"
                className="form-input px-4 py-2 border rounded w-full"
                placeholder="Montant net"
                value={val || ""}
                onChange={(e) => handleInputChange(index, e.target.value)}
                disabled={isLoading}
              />
              {salaireNets.length > 1 && (
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
              <Button
                onClick={exportToExcel}
                className="scale-[0.8] bg-green-600 hover:bg-green-700 text-white font-medium py-2 px-4 rounded flex items-center gap-1"
              >
                <FileDown size={16} /> Exporter Excel
              </Button>
            </div>

            <ScrollArea className="bg-background rounded-md border m-1">
              <div className="min-w-max">
                <Table>
                  <TableHeader className="bg-muted/40 sticky top-0 z-10">
                    <TableRow>
                      <TableHead className="pl-6 w-[14%]">Salaire Brut</TableHead>
                      <TableHead className="w-[14%]">Brut Imposable</TableHead>
                      <TableHead className="w-[14%]">Arrondi</TableHead>
                      <TableHead className="w-[14%]">CNSS</TableHead>
                      <TableHead className="w-[14%]">ITS</TableHead>
                      <TableHead className="w-[14%]">Total Retenue</TableHead>
                      <TableHead className="w-[16%]">Salaire Net</TableHead>
                    </TableRow>
                  </TableHeader>

                  <TableBody>
                    {results.map((res, idx) => (
                      <TableRow key={idx} className="text-center border-t">
                        <TableCell className="pl-6">{res.brut.toFixed(2)}</TableCell>
                        <TableCell>{res.brut_imp.toFixed(2)}</TableCell>
                        <TableCell>{res.arrondi}</TableCell>
                        <TableCell>{res.cnss.toFixed(2)}</TableCell>
                        <TableCell>{res.its.toFixed(2)}</TableCell>
                        <TableCell>{res.total_retenue.toFixed(2)}</TableCell>
                        <TableCell>{res.net.toFixed(2)}</TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </div>
            </ScrollArea>
          </div>
        )}
      </div>
    </>
  );
}
