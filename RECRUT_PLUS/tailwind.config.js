/** @type {import('tailwindcss').Config} */
module.exports = {
    content: [
      "./*.html",         // Toutes les pages HTML à la racine
      "./assets/js/*.js"  // Si tu veux aussi parser du JS
    ],
    theme: {
      extend: {},
    },
    plugins: [],
  }