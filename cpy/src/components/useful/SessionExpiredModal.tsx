"use client"

import { useId, useState } from "react"
import { useNavigate } from "react-router-dom"
import { CircleAlertIcon, Lock } from "lucide-react"
import { Button } from "@/components/ui/button"
import {
    AlertDialog,
    AlertDialogContent,
    AlertDialogFooter,
    AlertDialogHeader,
} from "@/components/ui/alert-dialog"
import { Input } from "@/components/ui/input"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { useAuth } from "@/lib/auth.ts"
import { UserHelpers } from "@/helpers/UserHelpers.ts"
import { useForm } from "react-hook-form"
import * as z from "zod"
import { zodResolver } from "@hookform/resolvers/zod"
import apiService from "@/api/apiService.ts"
import apiRoutes from "@/api/apiRoutes.ts"
import { UserAuthData } from "@/types/UserModelTypes.ts"

interface SessionProp {
    isOpen: boolean
    setIsOpen: React.Dispatch<React.SetStateAction<boolean>>
}

export default function SessionExpiredModal({ isOpen, setIsOpen }: SessionProp) {
    const navigate = useNavigate()
    const { oldeUser, login } = useAuth() // Note: j'ai changé oldeUser en oldUser pour cohérence
    const id = useId()
    const [isLoading, setIsLoading] = useState(false)
    const avatarInfo = UserHelpers.getInitialUser(oldeUser?.fullName ?? "")

    const formSchema = z.object({
        password: z.string().min(1, "Mot de passe requis"),
    })

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            password: "",
        },
    })

    // Gère la soumission du formulaire
    const onSubmit = async (values: z.infer<typeof formSchema>) => {
        try {
            setIsLoading(true)
            form.clearErrors()

            if (!oldeUser?.username) {
                throw new Error("Nom d'utilisateur non disponible")
            }

            const postData = {
                username: oldeUser.username,
                password: values.password
            }

            const response = await apiService.post({
                url: apiRoutes.auth.login,
                body: postData
            })

            if (response?.data) {
                await login(response.data as UserAuthData)
                setIsOpen(false)
            } else {
                throw new Error("Réponse invalide du serveur")
            }
        } catch (error) {
            if (error instanceof Error) {
                form.setError("password", {
                    type: "manual",
                    message: "Mot de passe incorrect"
                })
            }
        } finally {
            setIsLoading(false)
        }
    }

    const handleLogout = () => {
        setIsOpen(false)
        navigate('/login')
        // Ajoutez ici toute autre logique de déconnexion si nécessaire
    }

    return (
        <AlertDialog open={isOpen} onOpenChange={setIsOpen}>
            <AlertDialogContent className="sm:max-w-md rounded-lg">
                <div className="flex flex-col items-center gap-4 p-4">
                    <div className="relative">
                        <Avatar className="w-20 h-20">
                            <AvatarImage src={""} />
                            <AvatarFallback
                                className="font-semibold text-2xl"
                                style={{
                                    background: `linear-gradient(135deg, ${avatarInfo.bgColor}, ${avatarInfo.bgGradient})`,
                                    color: "#FFFFFF"
                                }}
                            >
                                {avatarInfo.initials}
                            </AvatarFallback>
                        </Avatar>
                        <div className="absolute -bottom-2 -right-2 bg-yellow-500 rounded-full p-1.5 border-2 border-white">
                            <Lock className="h-4 w-4 text-white" />
                        </div>
                    </div>
                    <AlertDialogHeader className="text-center space-y-1">
                        <h3 className="text-lg font-semibold">{oldeUser?.fullName}</h3>
                        <p className="text-sm text-muted-foreground">{oldeUser?.username}</p>
                    </AlertDialogHeader>
                    <div className="text-center space-y-2">
                        <div className="flex items-center justify-center gap-2 text-yellow-600">
                            <CircleAlertIcon className="h-5 w-5" />
                            <p className="font-medium">Session expirée</p>
                        </div>
                        <p className="text-sm text-muted-foreground">
                            Pour des raisons de sécurité, veuillez saisir à nouveau votre mot de passe
                        </p>
                    </div>
                </div>
                <form onSubmit={form.handleSubmit(onSubmit)} className="px-4 pb-4 space-y-4">
                    <div className="space-y-2">
                        <div className="relative">
                            <Input
                                id={id}
                                type="password"
                                placeholder="Mot de passe"
                                {...form.register("password")}
                                autoFocus
                                className={`pr-10 ${form.formState.errors.password ? "border-red-500" : ""}`}
                            />
                            <Lock className="absolute right-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                        </div>
                        {form.formState.errors.password && (
                            <p className="text-sm text-red-500 flex items-center gap-1.5">
                                <CircleAlertIcon className="h-4 w-4" />
                                {form.formState.errors.password.message}
                            </p>
                        )}
                    </div>

                    <AlertDialogFooter className="gap-2 sm:gap-3">
                        <Button
                            type="button"
                            variant="outline"
                            className="flex-1"
                            onClick={handleLogout}
                        >
                            Déconnexion
                        </Button>
                        <Button
                            type="submit"
                            className="flex-1"
                            disabled={isLoading || !form.formState.isDirty}
                        >
                            {isLoading ? "Connexion..." : "Se reconnecter"}
                        </Button>
                    </AlertDialogFooter>
                </form>
            </AlertDialogContent>
        </AlertDialog>
    )
}