import { ContratEmploye } from "@/types/ContratType.ts";
import {
    BadgeCheck, BadgeDollarSign,
     Banknote,
    BriefcaseBusiness, Building2,
    Calendar, CalendarDays, CircleOff, Clock3, CreditCard, FileText,
    Flag,
    Landmark,
    Layers3,
    MapPin, PauseCircle, Repeat, Repeat2, ShieldCheck, Timer,
    User,
    VenetianMask
} from "lucide-react";

interface PropsDetailContrat {
    contrat: ContratEmploye | null;
}

export default function DetailContrat({ contrat }: PropsDetailContrat) {
    if (!contrat) {
        return <p className="text-gray-500">Aucun contrat sélectionné.</p>;
    }

    return (
        <div className={"space-y-6"}>
            {/* Employé */}
            <section>
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 text-sm">

                    {/* Nom complet */}
                    <div className="flex items-start gap-2">
                        <User className="w-6 h-6 text-blue-500 p-1 bg-gray-100 rounded"/>
                        <div className="flex flex-col">
                            <strong className="text-black">Nom</strong>
                            <span className="font-semibold text-xs text-gray-600">
                                {contrat.employe.nom} {contrat.employe.prenom}
                            </span>
                        </div>
                    </div>

                    {/* Matricule */}
                    <div className="flex items-start gap-2">
                        <BadgeCheck className="w-6 h-6 text-indigo-500 p-1 bg-gray-100 rounded"/>
                        <div className="flex flex-col">
                            <strong className="text-black">Matricule</strong>
                            <span className="font-semibold text-xs text-gray-600">
                                {contrat.employe.matricule}
                              </span>
                        </div>
                    </div>

                    {/* Date de naissance */}
                    <div className="flex items-start gap-2">
                        <Calendar className="w-6 h-6 text-pink-500 p-1 bg-gray-100 rounded"/>
                        <div className="flex flex-col">
                            <strong className="text-black">Date de naissance</strong>
                            <span className="font-semibold text-xs text-gray-600">
                                {contrat.employe.date_naissance}
                              </span>
                        </div>
                    </div>

                    {/* Lieu de naissance */}
                    <div className="flex items-start gap-2">
                        <MapPin className="w-6 h-6 text-emerald-500 p-1 bg-gray-100 rounded"/>
                        <div className="flex flex-col">
                            <strong className="text-black">Lieu de naissance</strong>
                            <span className="font-semibold text-xs text-gray-600">
                            {contrat.employe.lieu_naissance}
                          </span>
                        </div>
                    </div>

                    {/* Sexe */}
                    <div className="flex items-start gap-2">
                        <VenetianMask className="w-6 h-6 text-orange-500 p-1 bg-gray-100 rounded"/>
                        <div className="flex flex-col">
                            <strong className="text-black">Sexe</strong>
                            <span className="font-semibold text-xs text-gray-600">
                            {contrat.employe.sexe}
                          </span>
                        </div>
                    </div>

                    {/* Nationalité */}
                    <div className="flex items-start gap-2">
                        <Flag className="w-6 h-6 text-red-500 p-1 bg-gray-100 rounded"/>
                        <div className="flex flex-col">
                            <strong className="text-black">Nationalité</strong>
                            <span className="font-semibold text-xs text-gray-600">
                            {contrat.employe.nationalite}
                          </span>
                        </div>
                    </div>

                </div>

            </section>

            {/* Poste et département */}
            <section>
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 text-sm">

                    {/* Entreprise */}
                    <div className="flex items-start gap-2">
                        <Building2 className="w-6 h-6 text-blue-600 p-1 bg-gray-100 rounded"/>
                        <div className="flex flex-col">
                            <strong className="text-black">Entreprise</strong>
                            <span className="font-semibold text-xs text-gray-600">{contrat.company.name}</span>
                        </div>
                    </div>

                    {/* Poste */}
                    <div className="flex items-start gap-2">
                        <BriefcaseBusiness className="w-6 h-6 text-emerald-600 p-1 bg-gray-100 rounded"/>
                        <div className="flex flex-col">
                            <strong className="text-black">Poste</strong>
                            <span className="font-semibold text-xs text-gray-600">{contrat.poste.libelle}</span>
                        </div>
                    </div>

                    {/* Département */}
                    <div className="flex items-start gap-2">
                        <Layers3 className="w-6 h-6 text-purple-600 p-1 bg-gray-100 rounded"/>
                        <div className="flex flex-col">
                            <strong className="text-black">Département</strong>
                            <span className="font-semibold text-xs text-gray-600">{contrat.departement.libelle}</span>
                        </div>
                    </div>

                </div>
            </section>


            {/* Détails du contrat */}
            <section>
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 text-sm">

                    {/* Type de contrat */}
                    <div className="flex items-start gap-2">
                        <FileText className="w-6 h-6 text-blue-600 p-1 bg-gray-100 rounded"/>
                        <div className="flex flex-col">
                            <strong className="text-black">Type</strong>
                            <span className="font-semibold text-xs text-gray-600">{contrat.typeContrat}</span>
                        </div>
                    </div>



                    {/* Date de début */}
                    <div className="flex items-start gap-2">
                        <CalendarDays className="w-6 h-6 text-green-600 p-1 bg-gray-100 rounded"/>
                        <div className="flex flex-col">
                            <strong className="text-black">Date de début</strong>
                            <span className="font-semibold text-xs text-gray-600">{contrat.dateDebut}</span>
                        </div>
                    </div>

                    {/* Date de fin */}
                    <div className="flex items-start gap-2">
                        <CalendarDays className="w-6 h-6 text-rose-600 p-1 bg-gray-100 rounded"/>
                        <div className="flex flex-col">
                            <strong className="text-black">Date de fin</strong>
                            <span className="font-semibold text-xs text-gray-600">{contrat.dateFin}</span>
                        </div>
                    </div>
                    {/* Mouvement */}
                    <div className="flex items-start gap-2">
                        <Repeat2 className="w-6 h-6 text-cyan-600 p-1 bg-gray-100 rounded"/>
                        <div className="flex flex-col">
                            <strong className="text-black">Mouvement</strong>
                            <span className="font-semibold text-xs text-gray-600">{contrat.mouvementContrat}</span>
                        </div>
                    </div>
                    {/* Période d'essai */}
                    <div className="flex items-start gap-2">
                        <Timer className="w-6 h-6 text-yellow-600 p-1 bg-gray-100 rounded"/>
                        <div className="flex flex-col">
                            <strong className="text-black">Période d'essai</strong>
                            <span className="font-semibold text-xs text-gray-600">
                              {contrat.debutEssai} au {contrat.finEssai}
                            </span>
                        </div>
                    </div>

                    {/* Durée */}
                    <div className="flex items-start gap-2">
                        <Clock3 className="w-6 h-6 text-indigo-600 p-1 bg-gray-100 rounded"/>
                        <div className="flex flex-col">
                            <strong className="text-black">Durée</strong>
                            <span className="font-semibold text-xs text-gray-600">{contrat.dureeContrat || "N/A"}</span>
                        </div>
                    </div>

                    {/* Date d'arrêt (optionnelle) */}
                    {contrat.dateArretContrat && (
                        <div className="flex items-start gap-2">
                            <PauseCircle className="w-6 h-6 text-red-600 p-1 bg-gray-100 rounded"/>
                            <div className="flex flex-col">
                                <strong className="text-black">Date d'arrêt</strong>
                                <span className="font-semibold text-xs text-gray-600">{contrat.dateArretContrat}</span>
                            </div>
                        </div>
                    )}

                    {/* Motif d'arrêt (optionnel) */}
                    {contrat.motifArretContrat && (
                        <div className="flex items-start gap-2">
                            <CircleOff className="w-6 h-6 text-gray-600 p-1 bg-gray-100 rounded"/>
                            <div className="flex flex-col">
                                <strong className="text-black">Motif d'arrêt</strong>
                                <span className="font-semibold text-xs text-gray-600">{contrat.motifArretContrat}</span>
                            </div>
                        </div>
                    )}

                </div>
            </section>

            <section>
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 text-sm">

                    {/* Mode de paiement */}
                    <div className="flex items-start gap-2">
                        <CreditCard className="w-6 h-6 text-purple-600 p-1 bg-gray-100 rounded"/>
                        <div className="flex flex-col">
                            <strong className="text-black">Mode de paiement</strong>
                            <span className="font-semibold text-xs text-gray-600">{contrat.modeDePaiement.libelle}</span>
                        </div>
                    </div>

                    {/* Banque */}
                    <div className="flex items-start gap-2">
                        <Landmark className="w-6 h-6 text-blue-600 p-1 bg-gray-100 rounded"/>
                        <div className="flex flex-col">
                            <strong className="text-black">Banque</strong>
                            <span className="font-semibold text-xs text-gray-600">{contrat.banque.name}</span>
                        </div>
                    </div>

                    {/* Numéro de compte */}
                    <div className="flex items-start gap-2">
                        <Banknote className="w-6 h-6 text-green-600 p-1 bg-gray-100 rounded"/>
                        <div className="flex flex-col">
                            <strong className="text-black">Compte N°</strong>
                            <span className="font-semibold text-xs text-gray-600">{contrat.numeroCompte}</span>
                        </div>
                    </div>

                </div>
            </section>

            {/* Infos supplémentaires */}
            <section>
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4 text-sm">

                    {/* AIB */}
                    <div className="flex items-start gap-2">
                        <BadgeDollarSign className="w-6 h-6 text-yellow-600 p-1 bg-gray-100 rounded"/>
                        <div className="flex flex-col">
                            <strong className="text-black">AIB</strong>
                            <span className="font-semibold text-xs text-gray-600">{contrat.aibContratEmploye}</span>
                        </div>
                    </div>

                    {/* Caution */}
                    <div className="flex items-start gap-2">
                        <ShieldCheck className="w-6 h-6 text-indigo-600 p-1 bg-gray-100 rounded"/>
                        <div className="flex flex-col">
                            <strong className="text-black">Caution</strong>
                            <span className="font-semibold text-xs text-gray-600">{contrat.cautionContratEmploye}</span>
                        </div>
                    </div>

                    {/* Transfert */}
                    <div className="flex items-start gap-2">
                        <Repeat className="w-6 h-6 text-emerald-600 p-1 bg-gray-100 rounded"/>
                        <div className="flex flex-col">
                            <strong className="text-black">Transfert</strong>
                            <span className="font-semibold text-xs text-gray-600">{contrat.transfertContratEmploye}</span>
                        </div>
                    </div>

                </div>
            </section>
        </div>
    );
}
