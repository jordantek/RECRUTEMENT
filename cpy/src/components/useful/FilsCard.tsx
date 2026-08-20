// FilsCard.tsx
import React, {useState} from 'react';
import {Download} from "lucide-react";

const icons = [
    {
        extension: "pdf",
        iconPath: "/icons/files/pdf.svg"
    },
    {
        extension: "doc",
        iconPath: "/icons/files/word.svg"
    },
    {
        extension: "docx",
        iconPath: "/icons/files/word.svg"
    },
    {
        extension: "rtf",
        iconPath: "/icons/files/word-rtf.svg"
    }

]

interface FilsCardProps {
    name: string;
    type: string; // ex: "PDF", "DOCX", etc.
    size: string | number;
    downloadUrl: string;
}


const FilsCard: React.FC<FilsCardProps> = ({ name, type, size, downloadUrl }) => {
    const extension = type.toLowerCase();
    const matchedIcon = icons.find(icon => icon.extension === extension);
    const iconPath = matchedIcon ? matchedIcon.iconPath : "";

    const [isDownloading, setIsDownloading] = useState(false);

    const handleDownload = () => {
        setIsDownloading(true);

        // Simuler un téléchargement
        setTimeout(() => {
            setIsDownloading(false);
            window.open(downloadUrl, '_blank');
        }, 1000); // Temps d'animation simulé
    };

    return (
        <div className="bg-white p-2 rounded-lg border border-gray-200 flex items-center gap-3 hover:shadow-xl">
            <div className="p-2  rounded">
                <img src={iconPath} alt={`${extension} file`} className="h-8 w-8" />
            </div>

            <div className="flex-1 min-w-0">
                <p className="text-left text-sm font-medium text-gray-800 truncate">{name}</p>
                <div className="flex justify-between text-xs text-gray-500 mt-1">
                    <span>{type.toUpperCase()}</span>
                    <span>{size}</span>
                </div>
            </div>

            <button
                onClick={handleDownload}
                disabled={isDownloading}
                className={`p-2 rounded-full text-primary hover:bg-primary/10 transition-colors ${
                    isDownloading ? 'cursor-not-allowed opacity-70' : ''
                }`}
                aria-label="Télécharger le fichier"
            >
                {isDownloading ? (
                    <svg className="animate-spin h-5 w-5 text-green-500" xmlns="http://www.w3.org/2000/svg" fill="none"
                         viewBox="0 0 24 24">
                        <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor"
                                strokeWidth="4"></circle>
                        <path className="opacity-75" fill="currentColor"
                              d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"></path>
                    </svg>
                ) : (
                    <Download className="h-4 w-4"/>
                )}
            </button>
        </div>
    );
};

export default FilsCard;