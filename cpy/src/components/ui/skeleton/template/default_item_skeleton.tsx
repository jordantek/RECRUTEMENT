import { Skeleton } from "@/components/ui/skeleton"

const DefaultItemSkeleton = () => {
  return (
    <div className="flex items-center space-x-4 mb-4">
      <Skeleton className="h-14 w-14 " />
      <div className="space-y-2 w-full">
        <Skeleton className="h-4 w-1/4" />
        <Skeleton className="h-4 w-1/3" />
      </div>
    </div>
  );
};

export default DefaultItemSkeleton;
