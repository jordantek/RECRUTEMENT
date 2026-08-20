import {
    AlertDialog,
    AlertDialogContent,
    AlertDialogDescription,
    AlertDialogFooter,
    AlertDialogHeader,
    AlertDialogTitle,
  } from "@/components/ui/alert-dialog";
  import { Button } from "@/components/ui/button";
  import { Form } from "@/components/ui/form.tsx";
  import { BindFormItem } from "@/components/forms/bind-form-item.tsx";
  import ButtonWithLoading from "@/components/ui/button-with-loading.tsx";
  import { Skeleton } from "@/components/ui/skeleton.tsx";
  import { Icon } from "@tabler/icons-react";
  import { cn } from "@/lib/utils.ts";
  import { Label } from "@/components/ui/label"; // ajout nécessaire
  
interface FormModalProps {
    icon?: Icon;
    title?: string;
    description?: string;
    isOpen: boolean;
    setIsOpen: (isOpen: boolean) => void;
    form: any;
    fields: any[];
    className?: string;
    size?: "sm" | "md" | "lg" | "xl"|"xxl"|"xxxl";
    loading?: boolean;
    isSubmitLoading?: boolean;
    onSubmit: (data: any) => void;
    onClose?: () => void;
    onSubmitSuccess?: () => void;

    customRenders?: Record<string, () => React.ReactNode>; // ✅ nouvelle prop
  }

const sizeClasses = {
  sm: "max-w-sm",
  md: "max-w-md",
  lg: "max-w-2xl",
  xl: "max-w-4xl",
  xxl: "max-w-5xl",
  xxxl: "max-w-7xl",
};

  export default function FormModal({
    icon: Icon,
    title,
    description,
    isOpen,
    setIsOpen,
    fields,
    form,
    onSubmit,
    isSubmitLoading,
    onClose,
    className,
    size = "md",
    loading = false,
    onSubmitSuccess,
    customRenders = {}, // ✅ valeur par défaut
  }: FormModalProps) {
    return (
      <AlertDialog open={isOpen} onOpenChange={setIsOpen}>
        <AlertDialogContent
          className={cn(
              sizeClasses[size],
              "flex flex-col p-0 sm:max-h-[min(640px,80vh)]",
            className
          )}
        >
          <AlertDialogHeader className="sticky top-0 z-10 bg-background px-6 pt-6 pb-2 rounded-2xl">
            <div>
              <AlertDialogTitle className={"flex items-center gap-2 "}>
                {Icon && <Icon className="h-5 w-5" />}
                {title ?? ""}
              </AlertDialogTitle>
              {description && (
                <AlertDialogDescription className={"text-xs italic"}>
                  {description}
                </AlertDialogDescription>
              )}
            </div>
          </AlertDialogHeader>
  
          <div className="flex-1 overflow-y-auto px-6 py-2 ">
            {loading ? (
              <div className="grid grid-cols-12 gap-2 p-2">
                {[...Array(6)].map((_, i) => (
                  <Skeleton key={i} className="col-span-6 h-10" />
                ))}
              </div>
            ) : (
              <Form {...form}>
                <form
                  onSubmit={form.handleSubmit(async () => {
                    try {
                      onSubmit(form.getValues());
                      onSubmitSuccess?.();
                     // setIsOpen(false);
                    } catch (error) {
                      console.error(error);
                    }
                  })}
                  className="space-y-4"
                >
                  <div className="grid grid-cols-12 gap-4">
                    {fields.map((option, index) => {
                      const isCustom = option.input_type === "custom";
                      const CustomRender = customRenders?.[option.tag];
  
                      return (
                        <div key={index} className={option.size}>
                          {isCustom && CustomRender ? (
                            <>
                              <Label className="block text-sm font-medium mb-1">
                                {option.label}
                                {option.required && <span className="text-red-500 ml-1">*</span>}
                              </Label>
                              {CustomRender()}
                            </>
                          ) : (
                            <BindFormItem
                              index={index}
                              readonly={option.readonly??false}
                              option={option}
                              form={form}
                              tag={option.tag}
                            />
                          )}
                        </div>
                      );
                    })}
                  </div>
  
                  <AlertDialogFooter className="sticky bottom-0 z-10 bg-background px-6 pb-0 pt-4">
                    <Button
                      type="button"
                      variant={"outline"}
                      onClick={() => {
                        setIsOpen(false);
                        onClose && onClose();
                      }}
                      className="w-fit flex items-center gap-2 "
                      disabled={loading}
                    >
                      Annuler
                    </Button>
                    <ButtonWithLoading
                      type="submit"
                      classList="w-fit flex items-center gap-2"
                      title="Enregistrer"
                      loading={isSubmitLoading ?? false}
                    />
                  </AlertDialogFooter>
                </form>
              </Form>
            )}
          </div>
        </AlertDialogContent>
      </AlertDialog>
    );
  }