const FormInputHelper = {
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    getInputBorderClass : (form: any, tag: string, value: any) => {
        if (form && form?.formState?.errors[tag]) {
            return 'border-red-500 border bg-red-50'; // Red border for error state
        }
        if (!value) {
            return 'border-gray-200 '; // Gray border if input is empty
        }
        return 'border-blue-400 border bg-gray-50'; // Black border if input has a value
    },
    uiFillInput: () => 'border-black border-2 bg-gray-50',
    uiDestructiveButton: () => 'text-white bg-red-500 hover:text-white border-2 border-red-500 hover:bg-red-700 hover:border-red-500'
}

export {FormInputHelper}
