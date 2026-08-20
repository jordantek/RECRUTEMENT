import React from "react";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { CircleCheckBig } from "lucide-react";

interface MyIconModalProps {
  icon ?: 'success' | 'error' | 'warning' | 'info';
  title ?: string;
  titleClass ?: string;
  message: string;
  messageClass ?: string;
  buttonText ?: string;
  buttonClass ?: string;
  onDismiss: () => void;
}

const MyIconModal: React.FC<MyIconModalProps> = ({
  icon = "success",
  title = "Action réussie !",
  titleClass = "",
  message = "La modification a été réalisée avec succès et a bien été prise en compte",
  messageClass = "",
  buttonText = "D'accord",
  buttonClass = "bg-blue-500 hover:bg-blue-600 text-white",
  onDismiss = () => {},
}) => {
  return (
    <Dialog open>
      <DialogContent className="[&>button]:hidden">
        <DialogHeader>
          <div className="flex items-center justify-center mb-4">
            {icon === "success" && <CircleCheckBig size={140} className="text-blue-500" />}
          </div>
          <DialogTitle className={`text-lg font-semibold text-center ${titleClass}`}>
            {title}
          </DialogTitle>
        </DialogHeader>
        <div className={`text-center text-gray-800 text-bold ${messageClass}`}>
          {message}
        </div>
        <DialogFooter>
          <Button onClick={onDismiss} size={"lg"} className={`w-full ${buttonClass}`}>
            {buttonText}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};

export default MyIconModal;
