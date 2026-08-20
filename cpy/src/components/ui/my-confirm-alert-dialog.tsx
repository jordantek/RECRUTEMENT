import { useEffect, useRef } from "react";
import { Form, FormControl, FormField, FormItem, FormMessage } from '@/components/ui/form'
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Button } from '@/components/ui/button';
import ButtonWithLoading from "./button-with-loading";
import { Textarea } from "./textarea";
import { FormInputHelper } from "@/helpers/FormInputHelper";
import { z } from "zod";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";

interface ConfirmDialogProps {
  onConfirm?: (data: {motif: string} | null) => void;
  onDismiss?: () => void;
  onFinally?: () => void;
  withMotif?: boolean;
  confirmBtnText?: string;
  confirmBtnClass?: string|null;
  title: string;
  titleClass ?: string|null;
  message: string;
}

export function ConfirmDialog({
  onConfirm = () => {},
  onDismiss= () => {},
  onFinally= () => {},
  confirmBtnText= "D'accord",
  confirmBtnClass= "",
  withMotif = false,
  title,
  titleClass = "",
  message}: ConfirmDialogProps) {
  const dialogRef = useRef<HTMLDivElement>(null);

  const formSchema = z.object({
      motif: z.string().min(1, 'Motif est requis')
  });

  const form = useForm<z.infer<typeof formSchema>>({
    resolver: zodResolver(formSchema),
    defaultValues: {motif: ""},
  });

  // Gestion du clic à l'extérieur
  const handleClickOutside = (event: MouseEvent) => {
    if (dialogRef.current && !dialogRef.current.contains(event.target as Node)) {
      handleDismiss();
    }
  };

  const handleConfirm = (data: {motif: string} | null = null ) => {
    if(onConfirm) onConfirm(data)
    if(onFinally) onFinally();
  };

  const handleDismiss = () => {
    if(onDismiss) onDismiss();
    if(onFinally) onFinally();
  };

  const onSubmit = async (values: z.infer<typeof formSchema>) => {
    handleConfirm({motif: values.motif});
  };


  useEffect(() => {
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  return (
    <Dialog open={true}>
      <DialogContent ref={dialogRef} className="sm:max-w-[425px] [&>button]:hidden">
        <DialogHeader>
          <DialogTitle className={`${titleClass}`}>{title}</DialogTitle>
        </DialogHeader>
          <DialogDescription className="text-gray-700 font-medium">{message}</DialogDescription>
          <>
          {withMotif ? (
            <Form {...form}>
                <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-3">
                  {/* Champ editable Password */}
                  <FormField control={form.control} name="motif" render={({ field }) => (
                  <FormItem className='col-span-12'>
                      <FormControl>
                      <Textarea 
                          {...field}  
                          className={`w-full p-4 pb-5 rounded-lg h-28 bg-white ${FormInputHelper.getInputBorderClass(form, "motif", field.value)}`}
                          placeholder="Pourquoi ?..."
                      />
                      </FormControl>
                      <FormMessage />
                  </FormItem>
                  )} />
                
                  <ButtonWithLoading
                      type="submit"
                      title={confirmBtnText}
                      classList={`w-full bg-black hover:bg-gray-800 text-white ${confirmBtnClass}`}
                      loading={false}/>
                </form>
            </Form>
          ) : (
            <div>
              <Button
                  variant={"default"}
                  onClick={() => handleConfirm(null)}
                  className={`w-full bg-black hover:bg-gray-800 text-white ${confirmBtnClass}`}>{confirmBtnText}
                </Button>
            </div>
          )}
          </>
      </DialogContent>
    </Dialog>
  );
}
