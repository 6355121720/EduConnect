package com.educonnect.event.service;

import com.educonnect.event.model.Ticket;
import com.educonnect.event.repo.EventsRepo;
import com.educonnect.event.repo.RegistrationRepo;
import com.educonnect.event.repo.TickerRepo;
import com.educonnect.event.utility.QRCodeGenerator;
import com.educonnect.user.entity.Users;
import com.educonnect.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
public class PdfService {
    private final TemplateEngine templateEngine;

    public PdfService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

//    @Autowired
//    RegistrationRepo rrepo;
//
//    @Autowired
//    UserRepository urepo;
//
//    @Autowired
//    EventsRepo erepo;

    @Autowired
    TickerRepo tickerRepo;

    public byte[] generatePdf(Long registrationId , Users user) throws Exception {

        
        Long ticketID = tickerRepo.findByRegistrationIdAndUser(registrationId , user).getId();

        if(ticketID == null){
            throw new RuntimeException("Ticket not found for the given registration ID and user");
        }
        Ticket ticket = tickerRepo.findById(ticketID)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        String name = ticket.getUser().getFullName();
        UUID userId = ticket.getUser().getId();
        Long eventId = ticket.getEvent().getId();
        String eventName = ticket.getEvent().getEventName();
        Long registrationId = ticket.getRegistration().getId();

        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("userId", userId);
        context.setVariable("eventId", eventId);
        context.setVariable("eventname", eventName);
        context.setVariable("registrationId", registrationId);
        context.setVariable("printDate", java.time.LocalDate.now().toString());

        byte[] qr = QRCodeGenerator.generateQRCode(String.valueOf(ticketID));
        if (qr == null || qr.length == 0) {
            throw new RuntimeException("Failed to generate QR code");
        }
        String qrBase64 =java.util.Base64.getEncoder().encodeToString(qr);
        context.setVariable("qrCode", qrBase64);

        String htmlContent = templateEngine.process("regticket", context);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(htmlContent);
            renderer.layout();
            renderer.createPDF(baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate PDF", e);
        }
    }
}