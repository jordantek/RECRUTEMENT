import React from 'react';

const HomePage = () => {
  return (
    <div className="bg-gray-50 text-gray-900 font-['Inter']">
      {/* HEADER */}
      <header className="sticky top-0 bg-white shadow z-50">
        <div className="container flex flex-col sm:flex-row justify-between items-center mx-auto py-4 px-8">
          <div className="flex items-center">
            <a href="/">
              <img src="/assets/Recrut Plus Logo.png" alt="Logo de Lander" className="h-12 w-auto mr-3" />
            </a>
          </div>
          <nav className="flex mt-4 sm:mt-0 space-x-6 text-base font-medium">
            <a href="#accueil" className="text-black hover:text-blue-700">Accueil</a>
            <a href="#fonctionnement" className="text-[#a6a6a6] hover:text-blue-700">Fonctionnement</a>
            <a href="#offres" className="text-[#a6a6a6] hover:text-blue-700">Offres</a>
            <a href="#avantages" className="text-[#a6a6a6] hover:text-blue-700">Avantages</a>
            <a href="#chiffres" className="text-[#a6a6a6] hover:text-blue-700">Nos chiffres</a>
          </nav>
          <div className="hidden md:block mt-4 space-x-6 sm:mt-0">
            <a href="/inscription" className="text-[#a6a6a6] hover:text-blue-700">Connexion</a>
            <a href="/inscription" className="py-2 px-6 bg-[#00329b] text-white rounded-lg text-base hover:bg-blue-800">Inscription</a>
          </div>
        </div>
      </header>

      {/* SECTION ACCUEIL */}
      <section id="accueil" className="pt-20 md:pt-40 bg-white">
        <div className="container mx-auto px-8 lg:flex items-center">
          <div className="text-center lg:text-left lg:w-1/2">
            <h1 className="text-4xl lg:text-5xl xl:text-6xl font-bold leading-none text-[#00329b]">
              Recrutez les meilleurs profils, plus vite.
            </h1>
            <p className="text-xl lg:text-2xl mt-6 font-light text-gray-700">
              Notre solution intelligente vous propose des CVs adaptés à vos besoins, selon votre abonnement.
            </p>
            <div className="mt-8 md:mt-12 flex flex-col sm:flex-row sm:justify-center lg:justify-start gap-4">
              <a href="#contact" className="py-4 px-8 bg-[#00329b] hover:bg-blue-800 text-white rounded-lg text-lg">
                Contactez-nous
              </a>
              <a href="/inscription" className="py-4 px-8 border border-[#00329b] hover:bg-[#00329b] hover:text-white text-[#00329b] rounded-lg text-lg">
                Inscription
              </a>
            </div>
            <p className="mt-4 text-gray-500 text-sm">
              Un service conçu pour gagner du temps et recruter efficacement.
            </p>
          </div>
          <div className="mt-10 lg:mt-0 lg:ml-12 lg:w-1/2">
            <img src="/assets/Image1.png" alt="Illustration Accueil" className="w-full h-auto max-w-md mx-auto lg:max-w-full" />
          </div>
        </div>
      </section>

      {/* SECTION PROFILS */}
      <section className="bg-[#f9f9f9] py-12">
        <div className="container mx-auto px-8 text-center">
          <h2 className="text-3xl md:text-4xl font-bold text-[#191a15] mb-8">
            Plus de 25 000 profils disponibles
          </h2>
          <div className="flex flex-wrap justify-center gap-14 gap-y-4 text-xl text-[#a6a6a6] font-semibold">
            <span>Informatique</span>
            <span>Gestion</span>
            <span>Comptabilité</span>
            <span>Santé</span>
            <span>Data</span>
            <span>Transport</span>
          </div>
        </div>
      </section>

      {/* SECTION FONCTIONNEMENT */}
      <section id="fonctionnement" className="bg-[#f9f8fe] py-20 px-6">
        <div className="container mx-auto flex flex-col lg:flex-row items-start lg:items-center justify-between gap-12">
          <div className="lg:w-1/2 space-y-6">
            <h2 className="text-4xl font-semibold text-[#191a15]">Comment nous fonctionnons ?</h2>
            <p className="text-[#a6a6a6] leading-7">
              Commencez par sélectionner l'abonnement qui correspond à vos besoins de recrutement...
            </p>
            <div className="flex space-x-12 pt-4">
              <div>
                <p className="text-lg text-[#191a15] font-bold">4.9 <span className="font-normal">/</span> <span className="font-medium">5 notes</span></p>
                <p className="text-[#a6a6a6] font-bold">Rapidité</p>
              </div>
              <div>
                <p className="text-lg text-[#191a15] font-bold">4.8 <span className="font-normal">/</span> <span className="font-medium">5 notes</span></p>
                <p className="text-[#a6a6a6] font-bold">Efficacité</p>
              </div>
            </div>
          </div>

          {/* Étapes */}
          <div className="lg:w-1/2 space-y-10">
            {[
              {
                title: 'Décrivez le profil que vous recherchez',
                text: 'Remplissez un formulaire simple : poste, compétences, expérience, localisation, etc.',
              },
              {
                title: 'Recevez des CVs correspondants',
                text: 'Notre système sélectionne et vous envoie des profils parfaitement adaptés.',
              },
              {
                title: 'Contactez les candidats',
                text: 'Consultez les profils, échangez directement et recrutez plus rapidement.',
              },
            ].map((step, idx) => (
              <div key={idx} className="flex items-start space-x-4">
                <div className="w-14 h-14 flex items-center justify-center bg-white shadow-md rounded">
                  <div
                    className={`${
                      idx === 0 ? 'w-6 h-6 border-2 border-[#00027d] rounded' :
                      idx === 1 ? 'w-6 h-6 border-2 border-[#00027d] rounded-full' :
                      'w-5 h-5 bg-gradient-to-br from-[#00027d] to-[#00027d] rounded-full'
                    }`}
                  />
                </div>
                <div>
                  <h3 className="text-xl font-bold text-[#191a15]">{step.title}</h3>
                  <p className="text-[#a6a6a6] text-base leading-6">{step.text}</p>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ... Tu peux continuer avec la section OFFRES, AVANTAGES, etc. ... */}

    </div>
  );
};

export default HomePage;
