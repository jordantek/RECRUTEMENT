import { Label } from '@/components/ui/label.tsx'
import { Input } from '@/components/ui/input.tsx'
import { Button } from '@/components/ui/button.tsx'
import { Dispatch, SetStateAction } from 'react'
import { X, XIcon } from 'lucide-react'
import { Badge } from '@/components/ui/badge.tsx'

interface InputFileProps {
  label?: string
  files?: File[]
  setFiles:Dispatch<SetStateAction<File[]>>
}

  export default function InputMultipleFile({ label, files, setFiles }: InputFileProps) {
  const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const selectedFiles = Array.from(event.target.files || [])
    setFiles((prevFiles) => [...prevFiles, ...selectedFiles])
  }

  const handleFileRemove = (index: number) => {
    setFiles((prevFiles) => prevFiles.filter((_, i) => i !== index))
  }

  return (
    <div className={"w-full"}>
      <div className="space-y-4">
        <Label htmlFor="file" className="block text-sm font-medium">
          {label}
        </Label>
        <Input
          id="file"
          type="file"
          multiple
          accept="image/*"
          onChange={handleFileChange}
          key={ Date.now()}
          className="border-dashed border-2 border-gray-200 rounded-lg w-full h-[50px] flex items-center justify-center transition-colors dark:border-gray-700 hover:border-gray-400"
        />
        <div className="grid grid-cols-10 gap-4 mt-4 ">
          {files?.map((file, index) => (
            <div  key={index} className="grid col-span-2 gap-4">
              <div
                className="relative border rounded-md overflow-hidden p-2 bg-gray-50 mx-auto col-span-2 w-[170px] h-[170px] group hidden md:block"
              >
                <img
                  src={URL.createObjectURL(file)}
                  alt={file.name}
                  className="object-cover rounded-md w-full h-full transition duration-300 ease-in-out group-hover:blur-md"
                />
                <Button
                  className="absolute inset-0 flex items-center justify-center opacity-0 group-hover:opacity-35 transition-opacity duration-300 h-full"
                  onClick={() => handleFileRemove(index)}
                >
                  <XIcon className="w-6 h-6 text-white justify-end" />
                </Button>
              </div>

              <Badge className="md:hidden w-full col-span-5" key={`image_${index}`}>
                {`image_${index + 1}`}
                <Button
                  className="-my-px -me-1.5 -ms-px inline-flex size-5 shrink-0 outline-none shadow-none items-center justify-center rounded-xl p-0 opacity-60 transition-opacity hover:opacity-100    hover:bg-transparent hover:border-0 hover:shadow-none focus:opacity-0 focus:bg-transparent focus:border-0 focus:shadow-none "
                  onClick={() => handleFileRemove(index)}
                >
                  <X size={12} strokeWidth={2} aria-hidden="true" />
                </Button>
              </Badge>
            </div>

          ))}
        </div>
      </div>
    </div>
  )
}
