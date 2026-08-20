import {z} from "zod";
import {CalendarDate} from "@internationalized/date";

export class ZodHelper {

    static calendarDateSchema = z.object({
        year: z.number().int().min(1900).max(2100),
        month: z.number().int().min(1).max(12),
        day: z.number().int().min(1).max(31),
    }).refine(data => {
        // Validation simple d’une date valide
        const d = new Date(data.year, data.month - 1, data.day);
        return d.getFullYear() === data.year &&
            d.getMonth() === data.month - 1 &&
            d.getDate() === data.day;
    }, { message: "Date invalide" })
        .transform(data => new CalendarDate(data.year, data.month, data.day));
}