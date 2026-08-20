import { genralConfig } from '../../../config';
import { useEffect } from 'react';
import { useLocation } from 'react-router-dom';

const PageTitle = ({ title }: { title: string }) => {
  const location = useLocation();

  useEffect(() => {
    document.title = `${genralConfig.appName} Admin - ${title}`;
  }, [location, title]);

  return null;
};

export default PageTitle;