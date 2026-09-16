package com.tpc.tpcgestpaie.localapp.repository.numerisation;

import com.tpc.tpcgestpaie.localapp.model.numerisation.DocumentReconstructionProcess ;
import com.tpc.tpcgestpaie.localapp.model.numerisation.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentReconstructionProcessRepository extends JpaRepository<DocumentReconstructionProcess, Long> {
    List<DocumentReconstructionProcess> findByStatus(DocumentReconstructionProcess.ProcessStatus status);

    Optional<DocumentReconstructionProcess> findTopByOriginalDocumentIdOrderByStartDateDesc(Long documentId);

    List<DocumentReconstructionProcess> findByOriginalDocumentEmployeId(Long employeId);
    List<DocumentReconstructionProcess> findByOriginalDocument_Employe_IdOrderByStartDateDesc(Long employeId);

}