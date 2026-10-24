package com.example.gestion_stages.service;

import com.example.gestion_stages.model.EncadrantAcademique;
import com.example.gestion_stages.model.Entreprise;
import com.example.gestion_stages.model.Etudiant;
import com.example.gestion_stages.model.Stage;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PdfService {

    // ── Colours & fonts ──────────────────────────────────────────────────────
    private static final Color HEADER_BG   = new Color(30, 64, 175);   // indigo-800
    private static final Color ACCENT_BG   = new Color(239, 246, 255); // blue-50
    private static final Color DIVIDER     = new Color(203, 213, 225); // slate-300
    private static final Color TEXT_DARK   = new Color(15, 23, 42);    // slate-900
    private static final Color TEXT_MUTED  = new Color(100, 116, 139); // slate-500
    private static final Color STATUS_ACTIVE     = new Color(22, 163, 74);
    private static final Color STATUS_COMPLETED  = new Color(30, 64, 175);
    private static final Color STATUS_CANCELLED  = new Color(185, 28, 28);
    private static final Color STATUS_UNASSIGNED = new Color(120, 113, 108);

    private static final Font FONT_TITLE    = new Font(Font.HELVETICA, 22, Font.BOLD,   Color.WHITE);
    private static final Font FONT_SUBTITLE = new Font(Font.HELVETICA, 11, Font.NORMAL, Color.WHITE);
    private static final Font FONT_SECTION  = new Font(Font.HELVETICA, 13, Font.BOLD,   new Color(30, 64, 175));
    private static final Font FONT_LABEL    = new Font(Font.HELVETICA, 9,  Font.BOLD,   TEXT_MUTED);
    private static final Font FONT_VALUE    = new Font(Font.HELVETICA, 10, Font.NORMAL, TEXT_DARK);
    private static final Font FONT_THEAD    = new Font(Font.HELVETICA, 9,  Font.BOLD,   Color.WHITE);
    private static final Font FONT_TROW     = new Font(Font.HELVETICA, 9,  Font.NORMAL, TEXT_DARK);

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ─────────────────────────────────────────────────────────────────────────
    // PUBLIC API
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Generates a detailed PDF for a single stage, including student, company,
     * and academic supervisor information.
     */
    public byte[] generateSingleRapport(Stage stage) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter.getInstance(doc, out);
            doc.open();

            addPageHeader(doc, "Rapport de Stage", "Fiche détaillée — Réf. #" + stage.getId());
            doc.add(Chunk.NEWLINE);

            // Stage details
            addSectionTitle(doc, "Informations du Stage");
            PdfPTable stageTable = twoColTable();
            addRow(stageTable, "Sujet",       safe(stage.getSujet()));
            addRow(stageTable, "Statut",      formatStatus(stage.getStatus()));
            addRow(stageTable, "Date début",  stage.getDateDebut()  != null ? stage.getDateDebut().format(DATE_FMT)  : "—");
            addRow(stageTable, "Date fin",    stage.getDateFin()    != null ? stage.getDateFin().format(DATE_FMT)    : "—");
            addRow(stageTable, "Outils",      safe(stage.getOutils()));
            doc.add(stageTable);

            // Long-text fields
            if (stage.getDescription() != null) addTextField(doc, "Description",  stage.getDescription());
            if (stage.getObjectifs()   != null) addTextField(doc, "Objectifs",     stage.getObjectifs());
            if (stage.getSolution()    != null) addTextField(doc, "Solution",      stage.getSolution());
            if (stage.getDemarche()    != null) addTextField(doc, "Démarche",      stage.getDemarche());

            // Student
            Etudiant e = stage.getEtudiant();
            addSectionTitle(doc, "Étudiant");
            if (e != null) {
                PdfPTable t = twoColTable();
                addRow(t, "CNE",      safe(e.getCne()));
                addRow(t, "Nom",      safe(e.getNom()) + " " + safe(e.getPrenom()));
                addRow(t, "Email",    safe(e.getEmail()));
                addRow(t, "Tél.",     safe(e.getTel()));
                addRow(t, "Filière",  e.getFiliere() != null ? safe(e.getFiliere().getIntitule()) : "—");
                doc.add(t);
            } else {
                addMuted(doc, "Aucun étudiant assigné.");
            }

            // Company
            Entreprise en = stage.getEntreprise();
            addSectionTitle(doc, "Entreprise d'accueil");
            if (en != null) {
                PdfPTable t = twoColTable();
                addRow(t, "Nom",           safe(en.getNom()));
                addRow(t, "Adresse",       safe(en.getAdresse()));
                addRow(t, "Ville / Pays",  safe(en.getVille()) + " / " + safe(en.getPays()));
                addRow(t, "Tél.",          safe(en.getTelephone()));
                addRow(t, "Email",         safe(en.getEmail()));
                addRow(t, "Responsable",   safe(en.getResponsableEmail()));
                addRow(t, "Encadrant soc.",safe(en.getEncadrantEmail()));
                doc.add(t);
            } else {
                addMuted(doc, "Aucune entreprise assignée.");
            }

            // Academic supervisor
            EncadrantAcademique enc = stage.getEncadrantAcademique();
            addSectionTitle(doc, "Encadrant Académique");
            if (enc != null) {
                PdfPTable t = twoColTable();
                addRow(t, "Nom",           safe(enc.getNom()) + " " + safe(enc.getPrenom()));
                addRow(t, "Département",   safe(enc.getDepartement()));
                addRow(t, "Établissement", safe(enc.getEtablissement()));
                doc.add(t);
            } else {
                addMuted(doc, "Aucun encadrant académique assigné.");
            }

            addFooter(doc);
            doc.close();
            return out.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException("Erreur lors de la génération du PDF", ex);
        }
    }

    /**
     * Generates a summary PDF listing all provided stages as a table.
     */
    public byte[] generateSummaryRapport(List<Stage> stages) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4.rotate(), 30, 30, 40, 40);
            PdfWriter.getInstance(doc, out);
            doc.open();

            addPageHeader(doc, "Rapport Récapitulatif des Stages",
                    stages.size() + " stage(s) trouvé(s)");
            doc.add(Chunk.NEWLINE);

            // Summary table
            PdfPTable table = new PdfPTable(new float[]{1f, 3f, 3f, 3f, 2f, 2f, 2f});
            table.setWidthPercentage(100);
            table.setSpacingBefore(6);

            String[] headers = {"#", "Étudiant", "Sujet", "Entreprise", "Début", "Fin", "Statut"};
            for (String h : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(h, FONT_THEAD));
                cell.setBackgroundColor(HEADER_BG);
                cell.setPadding(7);
                cell.setBorderColor(HEADER_BG);
                table.addCell(cell);
            }

            boolean alternate = false;
            for (Stage s : stages) {
                Color rowBg = alternate ? ACCENT_BG : Color.WHITE;
                alternate = !alternate;

                addTableCell(table, String.valueOf(s.getId()), rowBg);
                addTableCell(table, studentName(s.getEtudiant()), rowBg);
                addTableCell(table, safe(s.getSujet()), rowBg);
                addTableCell(table, s.getEntreprise() != null ? safe(s.getEntreprise().getNom()) : "—", rowBg);
                addTableCell(table, s.getDateDebut() != null ? s.getDateDebut().format(DATE_FMT) : "—", rowBg);
                addTableCell(table, s.getDateFin()   != null ? s.getDateFin().format(DATE_FMT)   : "—", rowBg);
                addTableCell(table, formatStatus(s.getStatus()), rowBg);
            }

            doc.add(table);
            addFooter(doc);
            doc.close();
            return out.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException("Erreur lors de la génération du PDF récapitulatif", ex);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    private void addPageHeader(Document doc, String title, String subtitle) throws DocumentException {
        PdfPTable header = new PdfPTable(1);
        header.setWidthPercentage(100);
        header.setSpacingAfter(4);

        PdfPCell titleCell = new PdfPCell();
        titleCell.setBackgroundColor(HEADER_BG);
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setPaddingTop(18);
        titleCell.setPaddingBottom(12);
        titleCell.setPaddingLeft(16);

        Paragraph p = new Paragraph();
        p.add(new Chunk(title + "\n", FONT_TITLE));
        p.add(new Chunk(subtitle, FONT_SUBTITLE));
        titleCell.addElement(p);
        header.addCell(titleCell);
        doc.add(header);
    }

    private void addSectionTitle(Document doc, String text) throws DocumentException {
        doc.add(Chunk.NEWLINE);
        Paragraph p = new Paragraph(text, FONT_SECTION);
        p.setSpacingAfter(4);
        doc.add(p);
        // thin rule
        doc.add(new Chunk(new com.lowagie.text.pdf.draw.LineSeparator(1f, 100f, DIVIDER, Element.ALIGN_LEFT, -2)));
        doc.add(Chunk.NEWLINE);
    }

    private void addTextField(Document doc, String label, String value) throws DocumentException {
        doc.add(Chunk.NEWLINE);
        Paragraph lbl = new Paragraph(label.toUpperCase(), FONT_LABEL);
        lbl.setSpacingAfter(2);
        doc.add(lbl);

        PdfPTable box = new PdfPTable(1);
        box.setWidthPercentage(100);
        PdfPCell cell = new PdfPCell(new Phrase(value, FONT_VALUE));
        cell.setBackgroundColor(ACCENT_BG);
        cell.setPadding(8);
        cell.setBorderColor(DIVIDER);
        box.addCell(cell);
        doc.add(box);
    }

    private void addMuted(Document doc, String text) throws DocumentException {
        Paragraph p = new Paragraph(text, new Font(Font.HELVETICA, 10, Font.ITALIC, TEXT_MUTED));
        p.setSpacingBefore(4);
        doc.add(p);
    }

    private void addFooter(Document doc) throws DocumentException {
        doc.add(Chunk.NEWLINE);
        doc.add(new Chunk(new com.lowagie.text.pdf.draw.LineSeparator(0.5f, 100f, DIVIDER, Element.ALIGN_LEFT, -2)));
        Paragraph footer = new Paragraph(
                "Généré automatiquement par Gestion des Stages API  •  " +
                java.time.LocalDate.now().format(DATE_FMT),
                new Font(Font.HELVETICA, 8, Font.ITALIC, TEXT_MUTED));
        footer.setSpacingBefore(4);
        footer.setAlignment(Element.ALIGN_CENTER);
        doc.add(footer);
    }

    /** A two-column label/value table */
    private PdfPTable twoColTable() throws DocumentException {
        PdfPTable t = new PdfPTable(new float[]{2f, 5f});
        t.setWidthPercentage(100);
        t.setSpacingBefore(4);
        return t;
    }

    private void addRow(PdfPTable table, String label, String value) {
        PdfPCell lbl = new PdfPCell(new Phrase(label.toUpperCase(), FONT_LABEL));
        lbl.setPadding(6);
        lbl.setBackgroundColor(ACCENT_BG);
        lbl.setBorderColor(DIVIDER);
        table.addCell(lbl);

        PdfPCell val = new PdfPCell(new Phrase(value, FONT_VALUE));
        val.setPadding(6);
        val.setBorderColor(DIVIDER);
        table.addCell(val);
    }

    private void addTableCell(PdfPTable table, String text, Color bg) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FONT_TROW));
        cell.setPadding(5);
        cell.setBackgroundColor(bg);
        cell.setBorderColor(DIVIDER);
        table.addCell(cell);
    }

    private String safe(String s) {
        return s != null ? s : "—";
    }

    private String studentName(Etudiant e) {
        if (e == null) return "—";
        return safe(e.getNom()) + " " + safe(e.getPrenom());
    }

    private String formatStatus(String status) {
        if (status == null) return "—";
        return switch (status) {
            case "ACTIVE"     -> "Actif";
            case "COMPLETED"  -> "Terminé";
            case "CANCELLED"  -> "Annulé";
            case "UNASSIGNED" -> "Non assigné";
            default           -> status;
        };
    }
}
