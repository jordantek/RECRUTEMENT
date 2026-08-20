import {format, isValid} from "date-fns";

export class DateHelpers {
  static formatDate (dateString: string, newFormat: string = "dd MMM. yyyy HH:mm")  {
    const date = new Date(dateString);
    return format(date, newFormat);
  }

  /**
   * Convertit divers types de dates en format ISO (YYYY-MM-DD)
   * @param date - Peut être : Date JS, objet CalendarDate, ou string
   * @returns Date au format ISO ou null si invalide
   */
  static convertDateToISO(date: Date | { year: number; month: number; day: number } | string | null): string | null {
    if (!date) return null;

    try {
      // Cas 1: Objet CalendarDate (comme {year, month, day})
      if (typeof date === 'object' && 'year' in date && 'month' in date && 'day' in date) {
        const jsDate = new Date(date.year, date.month - 1, date.day);
        return isValid(jsDate) ? format(jsDate, 'yyyy-MM-dd') : null;
      }

      // Cas 2: Date JavaScript standard
      if (date instanceof Date) {
        return isValid(date) ? format(date, 'yyyy-MM-dd') : null;
      }

      // Cas 3: String de date
      const parsedDate = new Date(date);
      return isValid(parsedDate) ? format(parsedDate, 'yyyy-MM-dd') : null;

    } catch (error) {
      console.error('Erreur de conversion:', error);
      return null;
    }
  }
  /**
   * Convertit divers formats de date en ISO
   * @param date - Date JS, objet {year, month, day} ou string
   */
  static universalConvertToISO(date: Date | {year: number, month: number, day: number} | string | null): string | null {
    if (!date) return null;

    try {
      let dateObj: Date;

      if (date instanceof Date) {
        dateObj = date;
      } else if (typeof date === 'object') {
        dateObj = new Date(date.year, date.month - 1, date.day);
      } else {
        dateObj = new Date(date);
      }

      return isValid(dateObj) ? format(dateObj, 'yyyy-MM-dd') : null;
    } catch (error) {
      console.error('Conversion error:', error);
      return null;
    }
  }

   static formatMoisAnnee = (dateStr: string) => {
    // dateStr = "JJ/MM/AAAA"
    const parts = dateStr.split("/");
    if (parts.length !== 3) return ""; // sécurité
    // @ts-ignore
     const [jour,mois, annee] = parts;
    return `${annee}-${mois.padStart(2, '0')}`; // "AAAA-MM"
  }

  static formatMonth = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('fr-FR', {
      month: 'long',
      year: 'numeric'
    }).replace(/^./, (c) => c.toUpperCase());
  };

  /**
   * Formatte une chaîne "YYYY-MM" ou "YYYY-MM-DD" en "MMM yyyy" (ex: "jan 2025")
   * @param dateStr Chaîne représentant le mois (ex: "2025-01" ou "2025-01-01")
   * @param localeCode Code de locale (ex: "fr", "en") — par défaut "fr"
   */
  static formatMonthYearShort(dateStr: string, localeCode: string = "fr"): string {
    try {
      const cleanedDateStr = dateStr.length === 7 ? `${dateStr}-01` : dateStr;
      const date = new Date(cleanedDateStr);

      if (!isValid(date)) return dateStr;

      return date.toLocaleDateString(localeCode, {
        month: "short",
        year: "numeric"
      });
    } catch (e) {
      console.error("formatMonthYearShort error:", e);
      return dateStr;
    }
  }

}