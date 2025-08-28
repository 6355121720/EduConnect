package com.educonnect.event.controller;

import com.educonnect.event.model.Ticket;
import com.educonnect.event.repo.TickerRepo;
import com.educonnect.event.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/pdf")
public class PdfController {
    private final PdfService pdfService;

    @Autowired
    private TickerRepo trepo;

    public PdfController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @GetMapping("/download_ticket")
    public ResponseEntity<byte[]> getInvoicePdf(
            @RequestParam Long registrationId) {
        try {
            Ticket ticket = trepo.findByRegistrationId(registrationId);
            if (ticket == null) {
                return ResponseEntity.notFound().build();
            }

            byte[] pdfBytes = pdfService.generatePdf(ticket.getId());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=ticket.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            System.err.println("Error generating PDF: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}