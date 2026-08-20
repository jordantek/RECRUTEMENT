import React, { useEffect } from "react";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Calendar, User2, Contact, Fingerprint, Save, X, Sparkles } from "lucide-react";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Badge } from "@/components/ui/badge";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import { format, parseISO } from "date-fns";

// Schéma de validation Zod
const employeeSchema = z.object({
  nom: z.string().min(1, "Le nom est requis"),
  prenom: z.string().min(1, "Le prénom est requis"),
  titre: z.string().min(1, "Le titre est requis"),
  sexe: z.string().min(1, "Le genre est requis"),
  date_naissance: z.date({
    required_error: "La date de naissance est requise",
    invalid_type_error: "Format de date invalide",
  }),
  lieu_naissance: z.string().min(1, "Le lieu de naissance est requis"),
  nationalite: z.string().min(1, "La nationalité est requise"),
  situationMatrimoniale: z.string().min(1, "La situation matrimoniale est requise"),
  telephone: z.string().min(10, "Numéro invalide"),
  email: z.string().email("Email invalide").optional().or(z.literal("")),
  quartier: z.string().optional(),
  numero_cnss: z.string().optional(),
  matricule: z.string().optional(),
  numero_ifu: z.string().optional(),
  profession: z.string().optional(),
});

type EmployeeFormValues = z.infer<typeof employeeSchema>;

interface EmployeeType {
  id?: number;
  nom?: string;
  prenom?: string;
  titre?: string;
  sexe?: string;
  date_naissance?: string;
  lieu_naissance?: string;
  nationalite?: string;
  situationMatrimoniale?: string;
  telephone?: string;
  email?: string;
  quartier?: string;
  numero_cnss?: string;
  matricule?: string;
  numero_ifu?: string;
  profession?: string;
}

interface EmployeeEditModalProps {
  employee: EmployeeType;
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onSave: (values: Partial<EmployeeType>) => Promise<EmployeeType>;
}

const situationsMatrimoniales = [
  { value: "CELIBATAIRE_SANS_ENFANT", label: "Célibataire sans enfant" },
  { value: "CELIBATAIRE_AVEC_ENFANT", label: "Célibataire avec enfant" },
  { value: "MARIE", label: "Marié(e)" },
  { value: "DIVORCE", label: "Divorcé(e)" },
];

const titres = [
  { value: "MONSIEUR", label: "Monsieur" },
  { value: "MADAME", label: "Madame" },
  { value: "MADEMOISELLE", label: "Mademoiselle" },
];

// Fonction pour obtenir les valeurs par défaut
function getDefaultValues(employee: EmployeeType): EmployeeFormValues {
  return {
    nom: employee.nom || "",
    prenom: employee.prenom || "",
    titre: employee.titre || "MONSIEUR",
    sexe: employee.sexe || "",
    date_naissance: employee.date_naissance 
      ? parseISO(employee.date_naissance) 
      : new Date(),
    lieu_naissance: employee.lieu_naissance || "",
    nationalite: employee.nationalite || "",
    situationMatrimoniale: employee.situationMatrimoniale || "",
    telephone: employee.telephone || "",
    email: employee.email || "",
    quartier: employee.quartier || "",
    numero_cnss: employee.numero_cnss || "",
    matricule: employee.matricule || "",
    numero_ifu: employee.numero_ifu || "",
    profession: employee.profession || "",
  };
}

function toEmployeePayload(values: EmployeeFormValues): Partial<EmployeeType> {
    return {
      ...values,
      date_naissance: values.date_naissance.toISOString().split('T')[0],
    };
  }
  

export function EmployeeEditModal({ 
  employee, 
  open, 
  onOpenChange,
  onSave 
}: EmployeeEditModalProps) {
  const form = useForm<EmployeeFormValues>({
    resolver: zodResolver(employeeSchema),
    defaultValues: getDefaultValues(employee)
  });

  useEffect(() => {
    if (open) {
      form.reset(getDefaultValues(employee));
    }
  }, [employee, open, form]);

  const handleSubmit = async (values: EmployeeFormValues) => {
    try {
        await onSave(toEmployeePayload(values));
      onOpenChange(false);
    } catch (error) {
      console.error('Erreur lors de la sauvegarde:', error);
    }
  };

  const handleOpenChangeWrapper = (open: boolean) => {
    if (!open) {
      form.reset();
    }
    onOpenChange(open);
  };

  return (
    <Dialog open={open} onOpenChange={handleOpenChangeWrapper}>
      <DialogContent className="sm:max-w-[800px] max-h-[95vh] overflow-hidden bg-gradient-to-br from-white via-slate-50 to-blue-50/30 border-0 shadow-2xl rounded-xl">
        {/* Header moderne */}
        <DialogHeader className="relative pb-6 border-b border-gradient-to-r from-transparent via-slate-200/50 to-transparent">
          <div className="absolute inset-0 bg-gradient-to-r from-blue-500/5 via-purple-500/5 to-blue-500/5 rounded-t-xl"></div>
          <DialogTitle className="relative flex items-center gap-4 text-xl font-bold bg-gradient-to-r from-slate-800 to-slate-600 bg-clip-text text-transparent">
            <div className="p-3 bg-gradient-to-r from-blue-500 to-purple-600 rounded-xl shadow-lg">
              <User2 className="h-6 w-6 text-white" />
            </div>
            <div>
              <div className="text-xl">Modifier les informations</div>
              <div className="text-sm font-medium text-slate-500 mt-1">
                {employee.prenom} {employee.nom}
              </div>
            </div>
          </DialogTitle>
        </DialogHeader>

        <Form {...form}>
          <form onSubmit={form.handleSubmit(handleSubmit)} className="overflow-y-auto max-h-[calc(95vh-180px)] px-1">
            <div className="space-y-8 py-4">
              
              {/* Section 1: Informations personnelles */}
              <div className="space-y-6 bg-white/80 backdrop-blur-sm rounded-xl p-6 border border-slate-200/50 shadow-sm">
                <div className="flex items-center gap-4">
                  <div className="bg-gradient-to-r from-emerald-500 to-teal-600 p-2 rounded-lg shadow-sm">
                    <Badge className="h-6 w-6 p-0 flex items-center justify-center bg-white text-emerald-600 font-bold">1</Badge>
                  </div>
                  <h3 className="font-semibold text-slate-800 text-lg">Informations personnelles</h3>
                  <div className="ml-auto">
                    <Badge variant="secondary" className="bg-emerald-100 text-emerald-700 border-emerald-200">
                      Étape 1/3
                    </Badge>
                  </div>
                </div>
                
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6 pl-12">
                  <FormField
                    control={form.control}
                    name="titre"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel className="text-slate-700 font-medium text-sm">Titre</FormLabel>
                        <Select onValueChange={field.onChange} value={field.value}>
                          <FormControl>
                            <SelectTrigger className="bg-white/90 border-slate-300 focus:border-blue-500 focus:ring-blue-500/20 transition-all duration-200">
                              <SelectValue placeholder="Sélectionner un titre" />
                            </SelectTrigger>
                          </FormControl>
                          <SelectContent>
                            {titres.map((titre) => (
                              <SelectItem key={titre.value} value={titre.value}>
                                {titre.label}
                              </SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                        <FormMessage className="text-red-500 text-xs" />
                      </FormItem>
                    )}
                  />

                  <FormField
                    control={form.control}
                    name="nom"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel className="text-slate-700 font-medium text-sm">Nom de famille</FormLabel>
                        <FormControl>
                          <Input 
                            {...field}
                            className="bg-white/90 border-slate-300 focus:border-blue-500 focus:ring-blue-500/20 transition-all duration-200"
                          />
                        </FormControl>
                        <FormMessage className="text-red-500 text-xs" />
                      </FormItem>
                    )}
                  />

                  <FormField
                    control={form.control}
                    name="prenom"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel className="text-slate-700 font-medium text-sm">Prénom</FormLabel>
                        <FormControl>
                          <Input 
                            {...field}
                            className="bg-white/90 border-slate-300 focus:border-blue-500 focus:ring-blue-500/20 transition-all duration-200"
                          />
                        </FormControl>
                        <FormMessage className="text-red-500 text-xs" />
                      </FormItem>
                    )}
                  />

                  <FormField
                    control={form.control}
                    name="sexe"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel className="text-slate-700 font-medium text-sm">Genre</FormLabel>
                        <Select onValueChange={field.onChange} value={field.value}>
                          <FormControl>
                            <SelectTrigger className="bg-white/90 border-slate-300 focus:border-blue-500 focus:ring-blue-500/20 transition-all duration-200">
                              <SelectValue placeholder="Sélectionner un genre" />
                            </SelectTrigger>
                          </FormControl>
                          <SelectContent>
                            <SelectItem value="MASCULIN">Masculin</SelectItem>
                            <SelectItem value="FEMININ">Féminin</SelectItem>
                          </SelectContent>
                        </Select>
                        <FormMessage className="text-red-500 text-xs" />
                      </FormItem>
                    )}
                  />

                  <FormField
                    control={form.control}
                    name="date_naissance"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel className="text-slate-700 font-medium text-sm">Date de naissance</FormLabel>
                        <Popover>
                          <PopoverTrigger asChild>
                            <FormControl>
                              <Button
                                variant="outline"
                                className="w-full pl-3 text-left font-normal bg-white/90 border-slate-300 hover:bg-white focus:border-blue-500 focus:ring-blue-500/20 transition-all duration-200"
                              >
                                {field.value ? format(field.value, "dd/MM/yyyy") : "Choisir une date"}
                                <Calendar className="ml-auto h-4 w-4 opacity-50" />
                              </Button>
                            </FormControl>
                          </PopoverTrigger>
                          <PopoverContent className="w-auto p-4 bg-white border-slate-200 shadow-xl rounded-lg" align="start">
                            <input
                              type="date"
                              value={field.value?.toISOString().split('T')[0] || ""}
                              onChange={(e) => field.onChange(new Date(e.target.value))}
                              className="w-full p-3 border border-slate-300 rounded-lg focus:border-blue-500 focus:ring-blue-500/20 transition-all duration-200"
                              max={new Date().toISOString().split('T')[0]}
                              min="1900-01-01"
                            />
                            <Button 
                              className="w-full mt-3 bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-700 hover:to-purple-700" 
                              onClick={() => {}}
                              size="sm"
                              type="button"
                            >
                              Confirmer
                            </Button>
                          </PopoverContent>
                        </Popover>
                        <FormMessage className="text-red-500 text-xs" />
                      </FormItem>
                    )}
                  />

                  <FormField
                    control={form.control}
                    name="lieu_naissance"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel className="text-slate-700 font-medium text-sm">Lieu de naissance</FormLabel>
                        <FormControl>
                          <Input 
                            {...field}
                            className="bg-white/90 border-slate-300 focus:border-blue-500 focus:ring-blue-500/20 transition-all duration-200"
                          />
                        </FormControl>
                        <FormMessage className="text-red-500 text-xs" />
                      </FormItem>
                    )}
                  />

                  <FormField
                    control={form.control}
                    name="nationalite"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel className="text-slate-700 font-medium text-sm">Nationalité</FormLabel>
                        <FormControl>
                          <Input 
                            {...field}
                            className="bg-white/90 border-slate-300 focus:border-blue-500 focus:ring-blue-500/20 transition-all duration-200"
                          />
                        </FormControl>
                        <FormMessage className="text-red-500 text-xs" />
                      </FormItem>
                    )}
                  />

                  <FormField
                    control={form.control}
                    name="situationMatrimoniale"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel className="text-slate-700 font-medium text-sm">Situation matrimoniale</FormLabel>
                        <Select onValueChange={field.onChange} value={field.value}>
                          <FormControl>
                            <SelectTrigger className="bg-white/90 border-slate-300 focus:border-blue-500 focus:ring-blue-500/20 transition-all duration-200">
                              <SelectValue placeholder="Sélectionner une situation" />
                            </SelectTrigger>
                          </FormControl>
                          <SelectContent>
                            {situationsMatrimoniales.map((situation) => (
                              <SelectItem key={situation.value} value={situation.value}>
                                {situation.label}
                              </SelectItem>
                            ))}
                          </SelectContent>
                        </Select>
                        <FormMessage className="text-red-500 text-xs" />
                      </FormItem>
                    )}
                  />
                </div>
              </div>

              {/* Section 2: Identifiants administratifs */}
              <div className="space-y-6 bg-white/80 backdrop-blur-sm rounded-xl p-6 border border-slate-200/50 shadow-sm">
                <div className="flex items-center gap-4">
                  <div className="bg-gradient-to-r from-amber-500 to-orange-600 p-2 rounded-lg shadow-sm">
                    <Badge className="h-6 w-6 p-0 flex items-center justify-center bg-white text-amber-600 font-bold">2</Badge>
                  </div>
                  <h3 className="font-semibold text-slate-800 text-lg flex items-center gap-2">
                    <Fingerprint className="h-5 w-5" />
                    Identifiants administratifs
                  </h3>
                  <div className="ml-auto">
                    <Badge variant="secondary" className="bg-amber-100 text-amber-700 border-amber-200">
                      Étape 2/3
                    </Badge>
                  </div>
                </div>
                
                <div className="bg-amber-50/80 border border-amber-200/50 rounded-lg p-4 mb-4 ml-12">
                  <div className="flex items-center gap-2 text-amber-700">
                    <Sparkles className="h-4 w-4" />
                    <span className="text-sm font-medium">Informations système</span>
                  </div>
                  <p className="text-sm text-amber-600 mt-1">
                    Les identifiants CNSS, Matricule et IFU sont gérés automatiquement.
                  </p>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-6 pl-12">
                  <FormField
                    control={form.control}
                    name="numero_cnss"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel className="text-slate-700 font-medium text-sm">Numéro CNSS</FormLabel>
                        <FormControl>
                          <Input 
                            {...field}
                            readOnly 
                            className="bg-slate-100/80 border-slate-300 cursor-not-allowed text-slate-600"
                          />
                        </FormControl>
                        <FormMessage className="text-red-500 text-xs" />
                      </FormItem>
                    )}
                  />

                  <FormField
                    control={form.control}
                    name="matricule"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel className="text-slate-700 font-medium text-sm">Matricule</FormLabel>
                        <FormControl>
                          <Input 
                            {...field}
                            readOnly 
                            className="bg-slate-100/80 border-slate-300 cursor-not-allowed text-slate-600"
                          />
                        </FormControl>
                        <FormMessage className="text-red-500 text-xs" />
                      </FormItem>
                    )}
                  />

                  <FormField
                    control={form.control}
                    name="numero_ifu"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel className="text-slate-700 font-medium text-sm">Numéro IFU</FormLabel>
                        <FormControl>
                          <Input 
                            {...field}
                            readOnly 
                            className="bg-slate-100/80 border-slate-300 cursor-not-allowed text-slate-600"
                          />
                        </FormControl>
                        <FormMessage className="text-red-500 text-xs" />
                      </FormItem>
                    )}
                  />

                  <FormField
                    control={form.control}
                    name="profession"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel className="text-slate-700 font-medium text-sm">Profession</FormLabel>
                        <FormControl>
                          <Input 
                            {...field}
                            className="bg-white/90 border-slate-300 focus:border-blue-500 focus:ring-blue-500/20 transition-all duration-200"
                            placeholder="Fonction, métier..."
                          />
                        </FormControl>
                        <FormMessage className="text-red-500 text-xs" />
                      </FormItem>
                    )}
                  />
                </div>
              </div>

              {/* Section 3: Coordonnées */}
              <div className="space-y-6 bg-white/80 backdrop-blur-sm rounded-xl p-6 border border-slate-200/50 shadow-sm">
                <div className="flex items-center gap-4">
                  <div className="bg-gradient-to-r from-violet-500 to-purple-600 p-2 rounded-lg shadow-sm">
                    <Badge className="h-6 w-6 p-0 flex items-center justify-center bg-white text-violet-600 font-bold">3</Badge>
                  </div>
                  <h3 className="font-semibold text-slate-800 text-lg flex items-center gap-2">
                    <Contact className="h-5 w-5" />
                    Coordonnées
                  </h3>
                  <div className="ml-auto">
                    <Badge variant="secondary" className="bg-violet-100 text-violet-700 border-violet-200">
                      Étape 3/3
                    </Badge>
                  </div>
                </div>
                
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6 pl-12">
                  <FormField
                    control={form.control}
                    name="telephone"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel className="text-slate-700 font-medium text-sm">Numéro de téléphone</FormLabel>
                        <FormControl>
                          <Input 
                            {...field}
                            className="bg-white/90 border-slate-300 focus:border-blue-500 focus:ring-blue-500/20 transition-all duration-200"
                            placeholder="+229 XX XX XX XX"
                          />
                        </FormControl>
                        <FormMessage className="text-red-500 text-xs" />
                      </FormItem>
                    )}
                  />

                  <FormField
                    control={form.control}
                    name="email"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel className="text-slate-700 font-medium text-sm">Adresse email</FormLabel>
                        <FormControl>
                          <Input 
                            {...field}
                            type="email"
                            className="bg-white/90 border-slate-300 focus:border-blue-500 focus:ring-blue-500/20 transition-all duration-200"
                            placeholder="exemple@email.com"
                          />
                        </FormControl>
                        <FormMessage className="text-red-500 text-xs" />
                      </FormItem>
                    )}
                  />

                  <div className="md:col-span-2">
                    <FormField
                      control={form.control}
                      name="quartier"
                      render={({ field }) => (
                        <FormItem>
                          <FormLabel className="text-slate-700 font-medium text-sm">Adresse complète</FormLabel>
                          <FormControl>
                            <Input 
                              {...field}
                              className="bg-white/90 border-slate-300 focus:border-blue-500 focus:ring-blue-500/20 transition-all duration-200"
                              placeholder="Quartier, rue, numéro..."
                            />
                          </FormControl>
                          <FormMessage className="text-red-500 text-xs" />
                        </FormItem>
                      )}
                    />
                  </div>
                </div>
              </div>

              {/* Boutons d'action */}
              <div className="flex justify-end gap-4 pt-6 bg-white/60 backdrop-blur-sm rounded-lg p-4 border border-slate-200/50">
                <Button 
                  type="button" 
                  variant="outline" 
                  onClick={() => handleOpenChangeWrapper(false)}
                  className="px-6 bg-white hover:bg-slate-50 text-slate-600 border border-slate-300 shadow-sm transition-all duration-200 hover:shadow-md"
                  disabled={form.formState.isSubmitting}
                >
                  <X className="h-4 w-4 mr-2" />
                  Annuler
                </Button>
                <Button 
                  type="submit"
                  disabled={form.formState.isSubmitting}
                  className="px-6 bg-gradient-to-r from-blue-600 to-purple-600 hover:from-blue-700 hover:to-purple-700 text-white shadow-lg hover:shadow-xl transition-all duration-200 transform hover:scale-105 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                  <Save className="h-4 w-4 mr-2" />
                  {form.formState.isSubmitting ? 'Enregistrement...' : 'Enregistrer les modifications'}
                </Button>
              </div>
            </div>
          </form>
        </Form>
      </DialogContent>
    </Dialog>
  );
}