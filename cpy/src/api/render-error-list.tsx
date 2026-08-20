import  { ReactNode } from "react";

export const renderErrorList = (errors: { field: string; message: string }[]): ReactNode => {
    return (
        <ul className="list-disc pl-4 space-y-1 text-red-500">
            {errors.map((err, idx) => (
                <li key={idx}>{err.message}</li>
            ))}
        </ul>
    );
};