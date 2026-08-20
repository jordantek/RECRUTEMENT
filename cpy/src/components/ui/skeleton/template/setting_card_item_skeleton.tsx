import { Skeleton } from "@/components/ui/skeleton"

const SettingCardSkeleton =  () => {
  return (
    <div className="flex flex-col space-y-3">
       <div className="space-y-2">
        <Skeleton className="h-8 w-[250px]" />
      </div>
      <Skeleton className="h-[200px] w-full rounded-xl" />
    </div>
  );
};

export default SettingCardSkeleton;
