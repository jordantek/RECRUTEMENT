"use client"

import { useId, useState } from "react"
import { UserRoundPlusIcon } from "lucide-react"
import { Button } from "@/components/ui/button"
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import apiService from "@/api/apiService.ts";
import apiRoutes from "@/api/apiRoutes.ts";
import {useUser} from "@/contexts/UserContext.ts";

type AddStaffProps = {
    isOpen: boolean,
    setIsOpen: (isOpen: boolean) => void
    refresh: () => void
}

type Errors = {
    fullName?: string
    email?: string
    password?: string
    role?: string
}

export default function AddStaffModal({ isOpen, setIsOpen,refresh }: AddStaffProps) {
    const id = useId()
    const user = useUser()
    const [fullName, setFullName] = useState("")
    const [email, setEmail] = useState("")
    const [password, setPassword] = useState("")
    const [role, setRole] = useState("ROLE_HR")
    const [isSubmitting, setIsSubmitting] = useState(false)
    const [errors, setErrors] = useState<Errors>({})

    const validateForm = () => {
        const newErrors: Errors = {}
        if (!fullName.trim()) newErrors.fullName = "Le nom complet est requis."
        if (!email.trim()) newErrors.email = "L'email est requis."
        else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) newErrors.email = "L'email n'est pas valide."
        if (!password.trim()) newErrors.password = "Le mot de passe est requis."
        if (!role) newErrors.role = "Le rôle est requis."
        return newErrors
    }

    const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault()
        const validationErrors = validateForm()
        if (Object.keys(validationErrors).length > 0) {
            setErrors(validationErrors)
            return
        }

        try {
            setIsSubmitting(true)
            setErrors({})

            await apiService.post(
                { url: apiRoutes.admin.app.staff.create, body: { fullName, email, password, role } },
                { userToken: `${user?.type??''} ${user?.token ?? ''}`, hasNoSuccessModal: false }
            )
            refresh()
            setIsOpen(false)

        } catch (error) {
            if (error instanceof Error) {
                apiService.handleError(error.message, { hasNoFailureModal: true })
            }
        } finally {
            setIsSubmitting(false)
        }
    }

    return (
        <Dialog open={isOpen} onOpenChange={setIsOpen} >
            <DialogContent  className="scroll-hidden">
                <DialogHeader>
                    <DialogTitle className="text-left flex items-center gap-2">
                        <UserRoundPlusIcon className="opacity-80 " size={16} />
                        Ajouter un staff
                    </DialogTitle>
                    <DialogDescription className="text-left">
                        Renseignez les informations du nouveau staff
                    </DialogDescription>
                </DialogHeader>

                <form onSubmit={handleSubmit} className="space-y-1">
                    {/* Nom complet */}
                    <div className="space-y-1">
                        <Label htmlFor="fullname">Nom complet</Label>
                        <Input
                            id="fullname"
                            type="text"
                            placeholder="Jean Dupont"
                            value={fullName}
                            onChange={(e) => setFullName(e.target.value)}
                        />
                        {errors.fullName && (
                            <p className="text-sm text-red-500">{errors.fullName}</p>
                        )}
                    </div>

                    {/* Role */}
                    <div className="space-y-1">
                        <Label htmlFor={id}>Rôle</Label>
                        <Select defaultValue={role} onValueChange={(value) => setRole(value)}>
                            <SelectTrigger id={id}>
                                <SelectValue placeholder="Choisissez un rôle" />
                            </SelectTrigger>
                            <SelectContent className={"select-content-ps"}>
                                <SelectItem value="ROLE_HR">Assistant RH</SelectItem>
                                <SelectItem value="ROLE_MANAGER">Manager</SelectItem>
                                <SelectItem value="ROLE_EMPLOYEE">Employé</SelectItem>
                            </SelectContent>
                        </Select>
                        {errors.role && (
                            <p className="text-sm text-red-500">{errors.role}</p>
                        )}
                    </div>

                    {/* Email */}
                    <div className="space-y-1">
                        <Label htmlFor="email">Email</Label>
                        <Input
                            id="email"
                            type="email"
                            placeholder="exemple@entreprise.com"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                        />
                        {errors.email && (
                            <p className="text-sm text-red-500">{errors.email}</p>
                        )}
                    </div>

                    {/* Mot de passe */}
                    <div className="space-y-1">
                        <Label htmlFor="password">Mot de passe</Label>
                        <Input
                            id="password"
                            type="password"
                            placeholder="********"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                        />
                        {errors.password && (
                            <p className="text-sm text-red-500">{errors.password}</p>
                        )}
                    </div>

                    {/* Footer Buttons */}
                    <DialogFooter className="flex justify-end gap-2 pt-4">
                        <Button type="button" variant="outline" onClick={() => setIsOpen(false)}>
                            Annuler
                        </Button>
                        <Button type="submit" disabled={isSubmitting}>
                            {isSubmitting ? "Enregistrement..." : "Enregistrer"}
                        </Button>
                    </DialogFooter>
                </form>
            </DialogContent>
        </Dialog>
    )
}
