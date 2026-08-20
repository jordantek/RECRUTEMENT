"use client";

import { ChevronDown, ChevronUp } from "lucide-react";
import { Button, Group, Input, Label, NumberField } from "react-aria-components";
import { cn } from '@/lib/utils.ts';

interface InputNumberWithChevronsProps {
  label?: string;
  defaultValue?: number;
  currency?: string;
  value?: number;
  className?: string;
  values?: never;
}

export default function InputNumberWithChevrons({label,defaultValue,currency,className,...props}: InputNumberWithChevronsProps) {
  return (
    <NumberField
      defaultValue={defaultValue ?? 0}
      minValue={0}
      {...currency && { formatOptions: { style: "currency", currency } }}
      value={props.value}
      className={cn("rounded-md hover:border-blue-600 focus:border-blue-600", className)}
      {...props}
    >
      <div className="space-y-2">
        {label && (
          <Label className="text-sm font-medium text-foreground">
            {label}
          </Label>
        )}

        <Group className="relative inline-flex h-9 w-full items-center overflow-hidden whitespace-nowrap rounded-sm border border-input focus:border-none text-sm shadow-sm shadow-black/5 transition-shadow data-[focus-within]:border-ring data-[disabled]:opacity-50 data-[focus-within]:outline-none data-[focus-within]:ring-[3px] data-[focus-within]:ring-ring/20">
          <Input className={cn(
            "flex-1 bg-background px-3 py-2 tabular-nums text-foreground focus:outline-none rounded-md focus:rounded "
          )}

          />
          <div className="flex h-[calc(100%+2px)] flex-col">
            <Button
              slot="increment"
              className="-me-px h-1/2 flex flex-1 items-center justify-center rounded-r-none rounded-l-none bg-background border border-input text-sm text-muted-foreground/80 transition-shadow hover:bg-accent hover:text-foreground disabled:pointer-events-none disabled:cursor-not-allowed disabled:opacity-50"
            >
              <ChevronUp size={12} strokeWidth={2} aria-hidden="true" />
            </Button>
            <Button
              slot="decrement"
              className="-me-px h-1/2 flex flex-1 items-center justify-center rounded-r-none rounded-l-none bg-background border border-input text-sm text-muted-foreground/80 transition-shadow hover:bg-accent hover:text-foreground disabled:pointer-events-none disabled:cursor-not-allowed disabled:opacity-50"
            >
              <ChevronDown size={12} strokeWidth={2} aria-hidden="true" />
            </Button>
          </div>
        </Group>
      </div>
    </NumberField>
  );
}
