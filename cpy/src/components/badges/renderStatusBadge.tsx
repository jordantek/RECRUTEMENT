type StatusType = 'delivery' | 'payment';

interface StatusStyle {
  label: string;
  classes: string;
}

const statusStyles: Record<StatusType, Record<number, StatusStyle>> = {
  delivery: {
    [-1]: { label: 'En attente', classes: 'bg-yellow-100 text-yellow-800' },
    0: { label: 'En cours', classes: 'bg-blue-100 text-blue-800' },
    1: { label: 'Livré', classes: 'bg-green-100 text-green-800' },
  },
  payment: {
    0: { label: 'Non payé', classes: 'bg-red-100 text-red-800' },
    1: { label: 'Payé', classes: 'bg-green-100 text-green-800' },
    2: { label: 'En validation', classes: 'bg-yellow-100 text-yellow-800' },
    3: { label: 'Échec paiement', classes: 'bg-[#2c0F14] text-[#E37083]' },
  },
};

const fallback: StatusStyle = {
  label: '---',
  classes: 'bg-gray-100 text-gray-500',
};

export function renderStatusBadge(type: StatusType, value: number): JSX.Element {
  const status = statusStyles[type]?.[value] || fallback;
  return (
    <span className={`text-xs px-3 py-1 rounded-md font-medium ${status.classes}`}>
      {status.label}
    </span>
  );
}
