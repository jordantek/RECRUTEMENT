export class UserHelpers {
  static  getInitialUser(name: string): {
    initials: string;
    bgColor: string;
    bgGradient: string;
    textColor: string;
  } {
    if (!name) {
      return {
        initials: "",
        bgColor: "#000000",
        bgGradient: "#222222",
        textColor: "#FFFFFF"
      };
    }

    const words = name.trim().split(/\s+/);
    const initials = words.slice(0, 2).map(word => word[0].toUpperCase()).join("");

    function hashCode(str: string): string {
      let hash = 2166136261;
      for (let i = 0; i < str.length; i++) {
        hash ^= str.charCodeAt(i);
        hash *= 16777619;
      }
      return (hash >>> 0).toString(16).padStart(6, "0").slice(0, 6);
    }

    function hexToRGB(hex: string): number[] {
      return hex.match(/\w\w/g)?.map(x => parseInt(x, 16)) ?? [0, 0, 0];
    }

    function rgbToHex(rgb: number[]): string {
      return "#" + rgb.map(x => Math.max(0, Math.min(255, x)).toString(16).padStart(2, "0")).join("");
    }

    function adjustColor(rgb: number[], percent: number): number[] {
      return rgb.map(c => Math.min(255, Math.floor(c + (255 - c) * percent)));
    }

    function getAverageLuminance(colors: string[]): number {
      const luminances = colors.map(hex => {
        const [r, g, b] = hexToRGB(hex);
        return 0.2126 * r + 0.7152 * g + 0.0722 * b;
      });
      return luminances.reduce((a, b) => a + b, 0) / colors.length;
    }

    const baseHex = hashCode(name);
    const bgColor = `#${baseHex}`;
    const lighterRgb = adjustColor(hexToRGB(baseHex), 0.35); // un vrai dégradé plus visible
    const bgGradient = rgbToHex(lighterRgb);

    const averageLuminance = getAverageLuminance([bgColor, bgGradient]);
    const textColor = averageLuminance > 140 ? "#000000" : "#FFFFFF"; // seuil un peu relevé pour meilleur contraste

    return { initials, bgColor, bgGradient, textColor };
  }
}
