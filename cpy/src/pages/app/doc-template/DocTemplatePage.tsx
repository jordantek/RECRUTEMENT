import PageTitle from '@/components/seo/pageTitle';
import { useEffect} from 'react';
import usePageTitleStore from '@/contexts/usePageTitleStore.ts';
import useCompanyStore from "@/contexts/CompanyContext.ts";
import FilsCard from "@/components/useful/FilsCard.tsx";
import {DocTemplate} from "@/data/DocTemplate.ts";
import {Tooltip, TooltipContent, TooltipProvider, TooltipTrigger} from "@/components/ui/tooltip.tsx";
import {Icon} from "@tabler/icons-react";
import {FileText} from "lucide-react";

export function DocTemplatePage() {
    const { setShowCompanySelect } = useCompanyStore();
    useEffect(() => {
        usePageTitleStore.getState().setTitle(
            'Modèles de documents',
            'Bibliothèque de modèles de documents administratifs prêts à l’usage',
            FileText as Icon
        );        setShowCompanySelect(false);
    }, []);

    return (
        <>
            <PageTitle title="Modèles de Documents" />

            <div className="max-w-7xl mx-auto px-6 py-8">
                {Object.entries(DocTemplate).map(([key, category]) => (
                    <section key={key} className="mb-12">
                        <h2 className="text-xl font-bold ">{category.title}</h2>

                        {category.description && (
                            <p className="text-gray-600 text-xs italic">{category.description}</p>
                        )}
                        <hr className="mb-4" />
                        <div className="grid gap-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4">
                            {category.files.map((file) => (
                                <TooltipProvider key={file.path} delayDuration={0} >
                                    <Tooltip>
                                        <TooltipTrigger className={"bg-transparent p-2"}>
                                            <FilsCard
                                                name={file.name}
                                                type={file.extension}
                                                size={`${file.sizeKB} KB`}
                                                downloadUrl={file.path}
                                            />
                                        </TooltipTrigger>
                                        <TooltipContent className="py-3 w-80 bg-white border">
                                            <div className="space-y-1">
                                                <p className="text-[13px] font-medium leading-none text-black">{file.name}</p>
                                                <p className="text-muted-foreground text-xs">
                                                    {file.description}
                                                </p>
                                            </div>
                                        </TooltipContent>
                                    </Tooltip>
                                </TooltipProvider>
                            ))}
                        </div>
                    </section>
                ))}
            </div>
        </>
    );
}