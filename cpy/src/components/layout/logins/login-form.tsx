import { useState } from "react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Card, CardContent } from "@/components/ui/card";
import { cn } from "@/lib/utils";
import apiService from "@/api/apiService";
import apiRoutes from "@/api/apiRoutes";
import ButtonWithLoading from "@/components/ui/button-with-loading";
import { useAuth } from '@/lib/auth.ts'
import { UserAuthData } from '@/types/UserModelTypes.ts'
import {Building2, Users, Wallet} from "lucide-react";

// Validation Schema avec Zod
const formSchema = z.object({
  email: z.string().min(1, "Email est requis")/*.email("Email invalide")*/,
  password: z.string().min(1, "Mot de passe requis"),
});

export function LoginForm({
                            className,
                            ...props
                          }: React.ComponentProps<"div">) {
  const { login } = useAuth();
  const [loading, setLoading] = useState(false); // Gère l'état de soumission
  const [errorMessage, setErrorMessage] = useState(""); // Pour afficher les erreurs éventuelles

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      email: "",
      password: "",
    },
  });

  // Gère la soumission du formulaire
  const onSubmit = async (values: z.infer<typeof formSchema>) => {
    try {
      setLoading(true);
      setErrorMessage(""); // Réinitialise le message d'erreur

      // Remplace ceci par ton appel API pour l'authentification
      const postData = { username: values.email, password: values.password };
      const response = await apiService.post({ url: apiRoutes.auth.login, body: postData });
      await login(response?.data as UserAuthData);
      // Si la connexion réussie, tu peux rediriger l'utilisateur, etc.
    } catch (error) {
      if (error instanceof Error) {
        setErrorMessage("Erreur de connexion, veuillez réessayer."); // Affiche l'erreur
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={cn("flex flex-col gap-6", className)} {...props}>
      <Card className="overflow-hidden">
        <CardContent className="grid p-0 md:grid-cols-2">
          <form onSubmit={form.handleSubmit(onSubmit)} className="p-6 md:p-8">
            <div className="flex flex-col gap-6">
              <div className="flex flex-col items-center text-center space-y-4 mb-6">
                <h1 className="text-2xl font-bold">Connexion</h1>
                <p className="text-muted-foreground text-sm max-w-sm">
                  Connectez-vous pour accéder à votre espace de travail
                </p>

                {/* Icônes des fonctionnalités */}
                <div className="grid grid-cols-3 gap-6 mt-8 text-muted-foreground">
                  <div className="flex flex-col items-center space-y-1">
                    <Users className="w-6 h-6 text-primary"/>
                    <span className="text-xs font-medium">Gestion RH</span>
                  </div>
                  <div className="flex flex-col items-center space-y-1">
                    <Wallet className="w-6 h-6 text-primary"/>
                    <span className="text-xs font-medium">Paie simplifiée</span>
                  </div>
                  <div className="flex flex-col items-center space-y-1">
                    <Building2 className="w-6 h-6 text-primary"/>
                    <span className="text-xs font-medium">Multi-entreprise</span>
                  </div>
                </div>
              </div>
              <div className="grid gap-2">
                <Label htmlFor="email">Email/Nom d'utilisateur</Label>
                <Input
                    id="email"
                    type="text"
                    placeholder="exemple@domaine.com"
                    {...form.register("email")}
                />
              </div>
              <div className="grid gap-2">
                <div className="flex items-center">
                  <Label htmlFor="password">Mot de passe</Label>

                </div>
                <Input
                    id="password"
                    type="password"
                    {...form.register("password")}
                />
              </div>
              <div className={"flex justify-end"}>
                <a
                    href="#"
                    className="ml-auto text-sm underline-offset-2 hover:underline"
                >
                  Mot de passe oublié ?
                </a>
              </div>
              {/* Afficher un message d'erreur */}
              {errorMessage && (
                  <div className="text-red-500 text-sm text-center mt-2">
                    {errorMessage}
                  </div>
              )}
              <ButtonWithLoading
                  type="submit"
                  classList="w-full"
                  title="Se connecter"
                  loading={loading}
              />

            </div>
          </form>
          <div className="relative hidden bg-muted md:block">
            <img
                src="/images/login_img.webp"
                alt="Image"
                className="absolute inset-0 h-full w-full object-cover dark:brightness-[0.2] dark:grayscale"
            />
            <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-black/40 to-transparent"/>
            <div className="absolute inset-0 flex items-end justify-start p-8 text-white">
              <div>
                <h2 className="text-3xl font-bold">Talents Gest Paie</h2>
                <p className="mt-2 text-lg">
                  Gérez efficacement vos ressources humaines et votre paie.
                </p>
              </div>
            </div>
          </div>
        </CardContent>
      </Card>

      {/* Footer */}
      <div className="text-balance text-center text-xs text-muted-foreground">
        Développé par le cabinet <a href="#">Talents Plus Afrique</a>.
      </div>
    </div>
  );
}
