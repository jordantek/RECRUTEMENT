import React from "react";
import {
  Pagination,
  PaginationContent,
  PaginationEllipsis,
  PaginationItem,
  PaginationLink,
  PaginationNext,
  PaginationPrevious,
} from "@/components/ui/pagination";
import { useNavigate, useLocation } from "react-router-dom";

interface PaginationProps {
  currentPage: number;
  totalItemCount: number;
  perPage: number;
  handleSelect?: (page: number) => void;
}

const MyPagination: React.FC<PaginationProps> = ({
  currentPage,
  totalItemCount,
  perPage,
  handleSelect = () => {},
}) => {
  const navigate = useNavigate();
  const location = useLocation();
  const totalPages = Math.ceil(totalItemCount / perPage);

  const getPageNumbers = () => {
    const pages = [];
    if (totalPages <= 5) {
      for (let i = 1; i <= totalPages; i++) {
        pages.push(i);
      }
    } else {
      if (currentPage <= 3) {
        pages.push(1, 2, 3, 4, "...", totalPages);
      } else if (currentPage >= totalPages - 2) {
        pages.push(1, "...", totalPages - 3, totalPages - 2, totalPages - 1, totalPages);
      } else {
        pages.push(1, "...", currentPage - 1, currentPage, currentPage + 1, "...", totalPages);
      }
    }
    return pages;
  };

  const handleNavigate = (page: number) => {
    const params = new URLSearchParams(location.search);
    params.set("page", page.toString());
    navigate(`${location.pathname}?${params.toString()}`);
    if(handleSelect) {
      handleSelect(page)
    }
  };

  return (
    <Pagination>
      <PaginationContent>
        {/* Bouton Précédent */}
        <PaginationItem>
          <PaginationPrevious style={{color:"black"}} role="button" color="black" onClick={currentPage === 1 ? undefined : () => handleNavigate(currentPage - 1)} />
        </PaginationItem>

        {/* Boutons de page */}
        {getPageNumbers().map((page, index) =>
          page === "..." ? (
            <PaginationItem key={index}>
              <PaginationEllipsis />
            </PaginationItem>
          ) : (
            <PaginationItem key={index}>
              <PaginationLink style={{color:"black"}} role="button"
                onClick={() => handleNavigate(Number(page))}
                isActive={page === currentPage}
              >
                {page}
              </PaginationLink>
            </PaginationItem>
          )
        )}

        {/* Bouton Suivant */}
          <PaginationItem >
            <PaginationNext style={{color:"black"}} role="button" onClick={ currentPage === totalPages ? undefined : () => handleNavigate(currentPage + 1)} />
          </PaginationItem>
        </PaginationContent>
    </Pagination>
  );
};

export default MyPagination;
