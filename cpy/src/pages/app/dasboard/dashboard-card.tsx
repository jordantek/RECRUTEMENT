import { Link } from 'react-router-dom';
import { ReactNode } from 'react';
import { cn } from '@/lib/utils';
import { motion } from 'framer-motion';

interface DashboardCardProps {
  title: string;
  description: string;
  icon: ReactNode;
  to?: string;
  className?: string;
}

export function DashboardCard({ title, description, icon, to, className }: DashboardCardProps) {
  const cardVariants = {
    hover: {
      y: -4,
      boxShadow: '0 10px 15px -3px rgba(0, 0, 0, 0.1)',
      transition: { duration: 0.2, ease: 'easeOut' }
    }
  };

  const iconVariants = {
    hover: {
      scale: 1.1,
      rotate: 5,
      transition: { duration: 0.3 }
    }
  };

  const content = (
    <motion.div
      variants={cardVariants}
      whileHover="hover"
      className={cn(
        'group relative flex items-start justify-between rounded-lg border border-gray-300 bg-white p-4 h-28',
        'focus:outline-none focus:ring-2 focus:ring-primary-500 focus:ring-offset-2',
        'cursor-pointer overflow-hidden',
        className
      )}
    >
      {/* Effet de fond au survol */}
      <div className="absolute inset-0 bg-gradient-to-br from-blue-100/20 to-blue-200/20 opacity-0 group-hover:opacity-100 transition-opacity duration-300" />
      
      <div>
        <div className="text-zinc-800 text-lg font-bold group-hover:text-blue-700 transition-colors">
          {title}
        </div>
        <div className="text-zinc-600 text-xs font-normal leading-snug max-w-[220px] mt-1 group-hover:text-zinc-700 transition-colors">
          {description}
        </div>
      </div>

      <motion.div 
        variants={iconVariants}
        className="ml-4 mt-1 text-blue-500 font-light group-hover:text-blue-600 transition-colors"
      >
        {icon}
      </motion.div>

      {/* Indicateur de survol */}
      <div className="absolute bottom-0 left-0 right-0 h-0.5 bg-blue-400 opacity-0 group-hover:opacity-100 transition-opacity duration-300" />
    </motion.div>
  );

  return to ? (
    <Link to={to} className="focus:outline-none">
      {content}
    </Link>
  ) : (
    content
  );
}