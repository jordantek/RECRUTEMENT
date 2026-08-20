
import PageTitle from '@/components/seo/pageTitle';
import { LoginForm } from '@/components/layout/logins/login-form.tsx'

export function LoginPage() {
  return (
    <>
      <PageTitle title="Connexion" />
      <div className="flex min-h-svh flex-col items-center justify-center p-6 md:p-10">
        <div className="w-full">
          <LoginForm />
        </div>
      </div>
    </>
  );
}