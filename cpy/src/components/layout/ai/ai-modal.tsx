import {
    AlertDialog,AlertDialogCancel,
    AlertDialogContent,
    AlertDialogDescription, AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogTrigger
} from "@/components/ui/alert-dialog.tsx";
import {Button} from "@/components/ui/button.tsx";
import {AlertDialogTitle} from "@radix-ui/react-alert-dialog";
import { PaperclipIcon, SendHorizonalIcon} from "lucide-react";
import {ImageIcon} from "@radix-ui/react-icons";
import {useAuth} from "@/lib/auth.ts";
import {AuroraText} from "@/components/magicui/aurora-text.tsx";

export default function AiModal() {
    const {user} = useAuth();
    return (
        <AlertDialog >
            <AlertDialogTrigger asChild>
                <Button
                    size="icon"
                    variant="ghost"
                    className="relative shadow-none p-1 rounded-full border-none hover:border-none"
                    aria-label="Open notifications menu"
                >
                    IA
                </Button>
            </AlertDialogTrigger>
            <AlertDialogContent>
                    <AlertDialogHeader>

                        <AlertDialogTitle className={"flex items-center justify-center gap-2 font-bold"}>Bonjour  <AuroraText>{user?.user.fullName}</AuroraText></AlertDialogTitle>
                        <AlertDialogDescription>
                            <h4 className={"text-center font-bold mb-3"}>Comment je peux vous aider aujourd'hui  ?</h4>
                            <div className="flex flex-col gap-4">
                                {/* Zone de texte utilisateur */}
                                <textarea
                                    placeholder="Ecrivez votre message ici..."
                                    className="w-full resize-none rounded-xl border border-gray-300 dark:border-zinc-700 bg-white dark:bg-zinc-800 px-4 py-3 text-sm text-gray-900 dark:text-white placeholder-gray-400 dark:placeholder-zinc-500 focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
                                    rows={4}
                                />

                                {/* Barre d'actions (icônes) */}
                                <div className="flex items-center justify-between">
                                    <div className="flex gap-2 text-gray-500 dark:text-zinc-400">
                                        <button className="hover:text-primary transition">
                                            <PaperclipIcon className="size-5"/>
                                        </button>
                                        <button className="hover:text-primary transition">
                                            <ImageIcon className="size-5"/>
                                        </button>
                                    </div>

                                    {/* Bouton d'envoi */}
                                    <button
                                        type="button"
                                        className="flex items-center justify-center rounded-full bg-primary p-2 text-white hover:bg-primary/90 transition"
                                    >
                                        <SendHorizonalIcon className="size-5"/>
                                    </button>
                                </div>
                            </div>
                        </AlertDialogDescription>
                    </AlertDialogHeader>
                <AlertDialogFooter>
                    <AlertDialogCancel>Fermer</AlertDialogCancel>
                </AlertDialogFooter>
            </AlertDialogContent>
        </AlertDialog>
    )
}
