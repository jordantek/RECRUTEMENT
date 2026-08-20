"use client";

import { useImageUpload } from "@/hooks/use-image-upload";
import { Button } from "@/components/ui/button";
import { CircleUserRound, X } from "lucide-react";
import { Dispatch, SetStateAction } from 'react'

interface InputFileImagePreviewProps {
  file?: File|null
  setFile:Dispatch<SetStateAction<File|null>>
  defaultImage?:string|null
}
export default function InputFileImagePreview({defaultImage,setFile}:InputFileImagePreviewProps) {
  const {
    previewUrl,
    fileInputRef,
    handleThumbnailClick,
    handleFileChange,
    handleRemove,
   // fileName,
  } = useImageUpload();

  // Gérer le changement de fichier
  const handleFileChange_ = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files?.length) {
      setFile(event.target.files[0]);
    }
  };

  // Supprimer le fichier
  const handleRemove_ = () => {
    setFile(null);
  };

  return (
    <div>
      <div className="relative inline-flex">
        <div
          className="relative flex items-center justify-center size-24 overflow-hidden border rounded bg-muted  p-1"
          onClick={handleThumbnailClick}
          aria-label={previewUrl ? "Change image" : "Upload image"}
        >
          {previewUrl ? (
            <img
              className="h-full w-full object-cover"
              src={previewUrl}
              alt="Preview of uploaded image"
              width={40}
              height={40}
              style={{ objectFit: "cover" }}
            />
          ) : (

            <div aria-hidden="true">
              {
                defaultImage ? (
                  <img
                    className="h-full w-full object-cover"
                    src={defaultImage}
                    alt="Preview of uploaded image"
                    width={40}
                    height={40}
                    style={{ objectFit: "cover" }}
                  />
                ):(<CircleUserRound className="opacity-60" size={16} strokeWidth={2}/>)
              }
            </div>
          )}
        </div>
        {previewUrl && (
          <Button
            onClick={() => {
              handleRemove_()
              handleRemove()
            }}
            size="sm"
            variant="destructive"
            className="absolute -right-2 -top-2 rounded-full size-4 hover:opacity-100 focus:opacity-100 hover:bg-red-600 focus:bg-destructive/80 hover:border-none"
            aria-label="Remove image"
          >
            <X size={10} />
          </Button>
        )}
        <input
          type="file"
          ref={fileInputRef}
          onChange={(event) => {
            handleFileChange_(event)
            handleFileChange(event)
          }}
          className="hidden"
          accept="image/*"
          aria-label="Upload image file"
        />
      </div>
     {/* {fileName && <p className="mt-2 text-xs text-muted-foreground">{fileName}</p>}
      <div className="sr-only" aria-live="polite" role="status">
        {previewUrl ? "Image uploaded and preview available" : "No image uploaded"}
      </div>*/}
    </div>
  );
}
