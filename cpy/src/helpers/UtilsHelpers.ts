export class UtilsHelpers {
  static formatDateToSQL = (date: Date) => {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    const hours = String(date.getHours()).padStart(2, "0");
    const minutes = String(date.getMinutes()).padStart(2, "0");
    const seconds = String(date.getSeconds()).padStart(2, "0");
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
  };

  static formatDateToSQL_2(dateString: string): string {
    const cleanDateString = dateString.replace(/\[.*\]/, "");
    const date = new Date(cleanDateString);
    return date.getFullYear() +
        "-" + String(date.getMonth() + 1).padStart(2, "0") +
        "-" + String(date.getDate()).padStart(2, "0") +
        " " + String(date.getHours()).padStart(2, "0") +
        ":" + String(date.getMinutes()).padStart(2, "0") +
        ":" + String(date.getSeconds()).padStart(2, "0");
  }

  static formatMontantWithSeparator(
      value: number | string | null | undefined,
      options?: {
        currency?: string;          // ex: "F CFA", "€", "DH"
        showCurrency?: boolean;     // afficher ou non la monnaie
        minFractionDigits?: number;
        maxFractionDigits?: number;
      }
  ): string {
    if (value === null || value === undefined || value === "") return "-";

    const montant = typeof value === "string" ? parseFloat(value) : value;
    if (isNaN(montant)) return "-";

    const {
      currency,
      showCurrency = false,
      minFractionDigits = 0,
      maxFractionDigits = 2,
    } = options || {};

    const formatted = montant.toLocaleString("fr-FR", {
      minimumFractionDigits: minFractionDigits,
      maximumFractionDigits: maxFractionDigits,
    });

    return showCurrency && currency ? `${formatted} ${currency}` : formatted;
  }
}
