import { Input } from "@/components/ui/input.tsx";
import { Label } from "@/components/ui/label.tsx";
import { cn } from '@/lib/utils.ts'
import { useId } from 'react'
interface InputWithStartAddOnProps {
  label?: string
  placeholder?: string
  startAddOn?: string
  className?: string
  value?: string
}

export default function InputWithStartAddOn({label,className,...props}: InputWithStartAddOnProps) {
  const id = useId();
  return (
    <div className="space-y-2">
      {label && (
        <Label className="text-sm font-medium text-foreground">
          {label}
        </Label>
      )}
   {/*   <Label htmlFor={id}>Input with start add-on</Label>*/}
      <div className="flex rounded-lg shadow-sm shadow-black/5">
        <span className=" inline-flex items-center rounded-s-lg border border-input bg-background px-3 text-sm text-black">
          https://
        </span>
        <Input
         id={id}
          type="text"
          placeholder={props && props.placeholder ? props.placeholder : "google.com"}
          value={props.value}
          className={cn("-ms-px rounded-s-none shadow-none !rounded-r-md hover:border-blue-600 focus:border-blue-600", className)}
          {...props}
        />
      </div>
    </div>
  );
}
