export interface ApiResponseTypes {
    statut: boolean,
    message ?: string | null,	
    error_code?: string | null,
    errors ?: {[tag: string]: string} | [string] | null,
    data ?: {[tag: string]: string} | [string] | null,
    debug ?: null
}
