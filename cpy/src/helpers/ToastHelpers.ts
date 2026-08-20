type Position = 'top-right' | 'top-left' | 'bottom-right' | 'bottom-left' | 'top-center';

interface ToastStyle {
  backgroundColor: string;
  color: string;
}

interface ToastConfig {
  description: string;
  position: Position;
  style: ToastStyle;
}

interface ToastHelpersType {
  UI: {
    SUCCESS: ToastConfig;
    INFO: ToastConfig;
    ERROR: ToastConfig;
  };
}

const ToastHelpers: ToastHelpersType = {
  UI: {
    SUCCESS: {
      description: 'Action effectué',
      position: 'top-right',  // correct Position value
      style: { backgroundColor: '#ecfdf3', color: '#0f913a' },
    },
    INFO: {
      description: "Nouvelle inscription d'un compte entreprise",
      position: 'top-right',  // correct Position value
      style: { backgroundColor: '#eceffd', color: '#0f2d91' },
    },
    ERROR: {
      position: 'top-right',  // correct Position value
      description: 'Problème survenu',
      style: { backgroundColor: '#f565652F', color: '#FF0000' },
    },
  },
};

export default ToastHelpers;
