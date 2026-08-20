import { Skeleton } from "@/components/ui/skeleton"

const ValidationCardSkeleton =  ({skeletonItemClass = ''} : {skeletonItemClass?: string | null | undefined} ) => {
  return (
    <div className="w-full space-y-3">
      <Skeleton className={`h-[270px]  w-full rounded-xl ${skeletonItemClass}`} />
    </div>
  );
};

export default ValidationCardSkeleton;
