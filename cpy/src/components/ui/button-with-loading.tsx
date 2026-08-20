import { Button } from '@/components/ui/button';

const ButtonWithLoading = ({ loading, title, type, classList }: { loading: boolean, title : string, classList : string, type:   "submit" | "button" | "reset" | undefined }) => {
  return (
    <Button type={type} className={classList}  disabled={loading}>
      {loading ? (
        <div className="flex justify-center items-center space-x-2">
          <div className="animate-spin rounded-full h-6 w-6 border-4 border-t-4 border-t-blue-500"></div>
        </div>
      ) : (
        <span>{title}</span>
      )}
    </Button>
  );
};

export default ButtonWithLoading;