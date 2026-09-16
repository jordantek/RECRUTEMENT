package com.tpc.tpcgestpaie.localapp.service.numerisation.analysis;

import com.tpc.tpcgestpaie.localapp.model.numerisation.parcours.EmployeeEvent;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SmartDateExtractorService {

    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void enrichEvent(EmployeeEvent event, String text) {

        text = normalize(text);

        detectContractType(event, text);

        extractPeriod(event, text);

        extractSingleDate(event, text);

        // fallback
        if (event.getEventDate() == null)
            event.setEventDate(event.getStartDate());
    }

    private void detectContractType(EmployeeEvent event, String text) {

        if (text.contains("durée indéterminée")
                || text.contains("contrat à durée indéterminée")
                || text.contains("cdi")) {

            event.setEndDate(null);
        }
    }

    private void extractPeriod(EmployeeEvent event, String text) {

        Pattern pattern = Pattern.compile(
                "(\\d{2}/\\d{2}/\\d{4}).{0,40}(au|à).{0,40}(\\d{2}/\\d{2}/\\d{4})"
        );

        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {

            LocalDate start =
                    LocalDate.parse(matcher.group(1), FORMAT);

            LocalDate end =
                    LocalDate.parse(matcher.group(3), FORMAT);

            event.setStartDate(start);

            if (event.getEndDate() == null) {
                // CDI → ignore end date
            }
            else {
                event.setEndDate(end);
            }

            event.setEventDate(start);
        }
    }

    private void extractSingleDate(EmployeeEvent event, String text) {

        Pattern pattern = Pattern.compile(
                "(effet|compter|date|notification).{0,40}(\\d{2}/\\d{2}/\\d{4})"
        );

        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {

            LocalDate date =
                    LocalDate.parse(matcher.group(2), FORMAT);

            event.setStartDate(date);

            event.setEventDate(date);
        }
    }

    private String normalize(String text) {

        return text
                .toLowerCase()
                .replace("\n", " ")
                .replaceAll("\\s+", " ");
    }
}
