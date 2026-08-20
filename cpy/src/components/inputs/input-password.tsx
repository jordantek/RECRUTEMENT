"use client";

import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Eye, EyeOff } from "lucide-react";
import { useId, useState } from "react";
import { cn } from '@/lib/utils.ts'

export default function InputPassword( {...props}) {
  const id = useId();
  const [isVisible, setIsVisible] = useState<boolean>(false);

  const toggleVisibility = () => setIsVisible((prevState) => !prevState);

  return (
    <div className="space-y-2">
      <Label htmlFor={id}>{props.label}</Label>
      <div className="relative">
        <Input
          id={id}
          className= {cn("pe-9", props.className)}
          placeholder="Password"
          type={isVisible ? "text" : "password"}
          {...props}
        />
        <button
          className="absolute inset-y-0 end-0 flex h-full  items-center bg-transparent border-none after:content-[''] justify-center rounded-e-lg text-muted-foreground/80 outline-offset-2 transition-colors hover:text-foreground focus:z-10 focus-visible:outline focus-visible:outline-2 focus-visible:outline-ring/70 disabled:pointer-events-none disabled:cursor-not-allowed disabled:opacity-50"
          type="button"
          onClick={toggleVisibility}
          aria-label={isVisible ? "Hide password" : "Show password"}
          aria-pressed={isVisible}
          aria-controls="password"
        >
          {isVisible ? (
            <EyeOff size={16} strokeWidth={2} aria-hidden="true" />
          ) : (
            <Eye size={16} strokeWidth={2} aria-hidden="true" />
          )}
        </button>
      </div>
    </div>
  );
}
