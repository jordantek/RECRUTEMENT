import { FormControl, FormField, FormItem, FormMessage } from '@/components/ui/form';
import { Input } from '@/components/ui/input.tsx';
import { FormInputHelper } from '@/helpers/FormInputHelper.ts';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select.tsx';
import { Textarea } from '@/components/ui/textarea.tsx';
import InputNumberWithChevrons from '@/components/inputs/input-number-with-chevrons.tsx'
import InputWithStartAddOn from '@/components/inputs/input-with-start-add-on.tsx'
import { Dispatch, SetStateAction } from 'react'
import InputPassword from '@/components/inputs/input-password.tsx'
import { PhoneInput } from '@/components/inputs/phone-input.tsx'
import { cn } from '@/lib/utils.ts'
import MultipleSelector, { Option as MultiOption } from '@/components/ui/multiselect'
import {RadioGroup, RadioGroupItem} from "@/components/ui/radio-group.tsx";
import {AtSignIcon, CalendarDays} from "lucide-react";
import {
    Button,
    DatePicker,
    Dialog,
    Group,
    Popover,
} from "react-aria-components"

import { Calendar } from "@/components/ui/calendar-rac"
import { DateInput } from "@/components/ui/datefield-rac"
import { CalendarDate, getLocalTimeZone } from '@internationalized/date';
import { format, parse } from 'date-fns';


// ... (autres imports et FieldOption inchangé sauf input_type)

export interface FieldOption {
    label?: string;
    tag: string;
    input_type: 'text' | 'textarea' | 'select' | 'file' | 'number' | 'link' | 'password' | 'date'|'date2'|'phone' | 'multiselect' | 'radio'| 'email';
    placeholder?: string;
    required?: boolean;
    size?: string;
    currency?: string;
    defaultValue?: string;
    options?: { value: string; label: string }[]; // Pour select et multiselect
    files_array?: File[];
    setFiles?: Dispatch<SetStateAction<File[]>>;
    accepts?: string;
}

export const renderDate2 = (field: any, readonly: boolean) => {
    const parseStringToCalendarDate = (dateString: string | undefined): CalendarDate | null => {
        if (!dateString) return null;
        try {
            const parsed = parse(dateString, 'dd/MM/yyyy', new Date());
            if (isNaN(parsed.getTime())) return null;
            return new CalendarDate(parsed.getFullYear(), parsed.getMonth() + 1, parsed.getDate());
        } catch {
            return null;
        }
    };

    const formatCalendarDateToString = (calendarDate: CalendarDate | null): string => {
        if (!calendarDate) return '';
        const jsDate = calendarDate.toDate(getLocalTimeZone());
        return format(jsDate, 'dd/MM/yyyy');
    };

    return (
        <DatePicker
            className="*:not-first:mt-2 "
            value={parseStringToCalendarDate(field.value)}
            onChange={(date) => field.onChange(formatCalendarDateToString(date))}
            isDisabled={readonly}
        >
            <div className="flex w-full relative">
                <Group className="w-full">
                    <DateInput className="pe-12 py-2 px-3 rounded-l-md border border-input bg-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring transition-colors" />
                </Group>
                <Button className="text-black absolute right-0 top-0 h-full w-10 flex items-center justify-center rounded-r-md border border-l-0 border-input bg-muted hover:bg-accent transition-colors">
                    <CalendarDays size={18} className="text-black" />
                </Button>
            </div>

            <Popover
                className="date-picker-ps absolute bg-background text-popover-foreground rounded-lg border shadow-lg "
                offset={4}
            >
                <Dialog className="max-h-[inherit] overflow-auto p-2">
                    <Calendar

                        className={"no-days rdp-head"}
                    />
                </Dialog>
            </Popover>
        </DatePicker>
    );
};

const BindFormItem = ({
                          index, option, form, tag, readonly = false
                      }: { index: number, readonly: boolean, option: FieldOption; form: any; tag: string }) => {
    return (
        <FormField
            key={index}
            control={form.control}
            name={tag}
            render={({ field }) => (
                <FormItem className={option?.size ?? 'col-span-12'}>
                    <p className={`text-sm font-medium ${option?.required ? "after:content-['*'] after:ml-1 after:text-red-500" : ''}`}>
                        {option.label}
                    </p>
                    <FormControl>
                        {option.input_type === 'textarea' ? (
                            <Textarea
                                className={` ${FormInputHelper.getInputBorderClass(form, option.tag, field.value)} resize-none w-full min-h-[60px]`}
                                placeholder={option.placeholder}
                                {...field}
                                readOnly={readonly}
                                value={(field.value as string) || ''}
                            />
                        ) : option.input_type === 'select' ? (
                            <Select
                                value={field.value ?? ''}
                                onValueChange={field.onChange}
                                disabled={readonly}
                                defaultValue={option.defaultValue}
                            >
                                <SelectTrigger className={cn(` ${FormInputHelper.getInputBorderClass(form, option.tag, field.value)}`, "text-black")}>
                                    <SelectValue placeholder={option.placeholder || ''} />
                                </SelectTrigger>
                                <SelectContent className={"select-content-ps"}>
                                    {option.options?.map((opt) => (
                                        <SelectItem key={opt.value} value={opt.value}>
                                            {opt.label}
                                        </SelectItem>
                                    ))}
                                </SelectContent>
                            </Select>
                        ) : option.input_type === 'multiselect' ? (
                            <MultipleSelector
                                defaultOptions={option.options ?? []}
                                placeholder={option.placeholder}
                                value={
                                    (field.value as string[] | undefined)?.map(val =>
                                        option.options?.find(opt => opt.value === val) || { value: val, label: val }
                                    ) ?? []
                                }
                                onChange={(selected: MultiOption[]) => {
                                    field.onChange(selected.map(opt => opt.value));
                                }}
                                disabled={readonly}
                                emptyIndicator={<p className="text-center text-sm bg-transparent focus-visible: ">Aucun résultat</p>}
                            />
                        ) : option.input_type === 'file' ? (
                            <Input
                                type="file"
                                accept={option.accepts ?? "image/jpeg,image/png,application/pdf"}
                                className={` ${FormInputHelper.getInputBorderClass(form, option.tag, field.value)}`}
                                onChange={(e) => {
                                    const file = e.target.files?.[0] || null;
                                    field.onChange(file);
                                }}
                                readOnly={readonly}
                            />
                        ) :  option.input_type === 'date' ? (
                            <DatePicker
                                className="*:not-first:mt-2 select-content-ps"
                                value={field.value}
                                onChange={field.onChange}
                                isDisabled={readonly}
                                hideTimeZone={true}
                            >
                                <div className="flex w-full relative select-content-ps">
                                    <Group className="w-full">
                                        <DateInput
                                            className="pe-12 py-2 px-3 rounded-l-md border border-input bg-background focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring transition-colors"
                                        />
                                    </Group>
                                    <Button
                                        className=" absolute right-0 top-0 h-full w-10 flex items-center justify-center rounded-l-none  border-l-0 border-input  bg-transparent"

                                    >
                                      <span>
                                           <CalendarDays size={16} className="text-gray-400  " />
                                      </span>
                                    </Button>

                                </div>
                                <Popover
                                    className="date-picker-ps bg-background text-popover-foreground data-entering:animate-in data-exiting:animate-out data-[entering]:fade-in-0 data-[exiting]:fade-out-0 data-[entering]:zoom-in-95 data-[exiting]:zoom-out-95 data-[placement=bottom]:slide-in-from-top-2 data-[placement=left]:slide-in-from-right-2 data-[placement=right]:slide-in-from-left-2 data-[placement=top]:slide-in-from-bottom-2 z-50 rounded-lg border shadow-lg outline-hidden"
                                    offset={4}
                                >
                                    <Dialog className="max-h-[inherit] overflow-auto p-2">
                                        <Calendar/>
                                    </Dialog>
                                </Popover>
                            </DatePicker>
                        ):option.input_type ==='date2'?(
                            renderDate2(field, readonly)
                        ): option.input_type === 'number' ? (
                            <InputNumberWithChevrons
                                {...option && option.currency ? {currency: option.currency} : {}}
                                className={` ${FormInputHelper.getInputBorderClass(form, option.tag, field.value)}`}
                                {...field}
                                value={(field.value as number) || 0}
                                aria-label={option.label || option.placeholder || option.tag}
                                {...readonly ? { readOnly: false } : {}}
                            />
                        ) : option.input_type === "phone" ? (
                            <PhoneInput
                                className={cn(` ${FormInputHelper.getInputBorderClass(form, option.tag, field.value)}`)} {...field}
                                value={(field.value as string) || ""}
                                onChange={(value) => {
                                    field.onChange(value)
                                }}
                                placeholder={option.placeholder}
                                defaultCountry='BJ'
                            />
                        ) : option.input_type === 'link' ? (
                            <InputWithStartAddOn
                                className={`${FormInputHelper.getInputBorderClass(form, option?.tag, field.value)}`}
                                {...field}
                                value={(field.value as string) || ""}
                                placeholder={option.placeholder}
                                {...readonly ? { readOnly: true } : {}}
                            />
                        ) :option.input_type === "radio"?(
                            <RadioGroup
                                value={field.value ?? ""}
                                onValueChange={val => field.onChange(val)}
                                disabled={readonly}
                            >
                                {option.options?.map(opt => (
                                    <div key={opt.value} className="flex items-center space-x-2">
                                        <RadioGroupItem value={opt.value} id={`${option.tag}-${opt.value}`} />
                                        <label htmlFor={`${option.tag}-${opt.value}`} className="text-sm">{opt.label}</label>
                                    </div>
                                ))}
                            </RadioGroup>
                        ):  option.input_type === 'email' ? (
                            <div className="relative">
                                <Input
                                    id={option.tag}
                                    type="email"
                                    className={`peer ps-9 ${FormInputHelper.getInputBorderClass(form, option?.tag, field.value)}`}
                                    placeholder={option.placeholder || 'Email'}
                                    {...field}
                                    value={(field.value as string) || ''}
                                    readOnly={readonly}
                                />
                                <div className="text-muted-foreground/80 pointer-events-none absolute inset-y-0 start-0 flex items-center justify-center ps-3 peer-disabled:opacity-50">
                                    <AtSignIcon size={16} aria-hidden="true" />
                                </div>
                            </div>):
                            option.input_type === 'password' ? (
                            <InputPassword
                                className={` ${FormInputHelper.getInputBorderClass(form, option?.tag, field.value)}`}
                                {...field}
                                value={(field.value as string) || ""}
                                placeholder={option.placeholder}
                            />
                        ) : (
                            <Input
                                type="text"
                                className={` ${FormInputHelper.getInputBorderClass(form, option?.tag, field.value)}`}
                                placeholder={option.placeholder}
                                {...field}
                                value={(field.value as string) || ''}
                                readOnly={readonly}
                            />
                        )}
                    </FormControl>
                    <FormMessage />
                </FormItem>
            )}
        />
    );
};

export { BindFormItem };