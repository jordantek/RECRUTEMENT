import { Loader2 } from "lucide-react";

const FullScreenLoader: React.FC = () => {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-80">
      <div className="flex items-center justify-center">
        <div className="animate-spin">
          <Loader2 className="w-12 h-12 text-white" />
        </div>
      </div>
    </div>
  );
};

export default FullScreenLoader;
