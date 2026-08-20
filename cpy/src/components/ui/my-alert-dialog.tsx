import { useState, useEffect, useRef } from "react";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";

interface AutoShowDialogProps {
  onSuccess?: () => void;
  onCancel?: () => void;
  onDismiss?: () => void;
  onFinally?: () => void;
  successBtnText?: string|null;
  cancelBtnText?: string|null;
  showDismissButton?: boolean;
  title: string;
  message: string;
  successBtnClass?: string|null;
  titleClass ?: string|null;
}

export function AutoShowDialog({
  onSuccess = () => {},
  onCancel= () => {},
  onDismiss= () => {},
  onFinally= () => {},
  successBtnText= "D'accord",
  successBtnClass= "",
  cancelBtnText = "",
  title,
  titleClass = "",
  message,}: AutoShowDialogProps) {
  const [isOpen, setIsOpen] = useState(true);
  const dialogRef = useRef<HTMLDivElement>(null);

  // Gestion du clic à l'extérieur
  const handleClickOutside = (event: MouseEvent) => {
    if (dialogRef.current && !dialogRef.current.contains(event.target as Node)) {
      handleDismiss();
    }
  };

  const handleSuccess = () => {
    if (onSuccess) onSuccess()
       if(onFinally) onFinally();
  };

  const handleCancel = () => {
    if (onCancel) onCancel()
       if(onFinally) onFinally();
  };

  const handleDismiss = () => {
    if (onDismiss) onDismiss();
    if(onFinally) onFinally();
  };


  // Open the modal automatically when the component mounts
  useEffect(() => {
    setIsOpen(true);
  }, []);

   useEffect(() => {
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  return (
    <Dialog  open={isOpen} onOpenChange={setIsOpen}>
      <DialogContent ref={dialogRef} className="sm:max-w-[425px] [&>button]:hidden">
        <DialogHeader>
          <DialogTitle className={`${titleClass}`}>{title}</DialogTitle>
          <DialogDescription>{message}</DialogDescription>
        </DialogHeader>
        <DialogFooter>
          {cancelBtnText && (
            <Button variant="outline" onClick={handleCancel}>
                {cancelBtnText}
            </Button>
          )}
          {successBtnText && (
            <Button type="submit" className={`${successBtnClass}`} onClick={handleSuccess}>
              {successBtnText}
            </Button>
          )}
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
