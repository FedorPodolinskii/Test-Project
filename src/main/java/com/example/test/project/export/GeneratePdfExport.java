package com.example.test.project.export;

import com.example.test.project.entity.Vacation;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.TreeMap;

public class GeneratePdfExport {


    public static ByteArrayInputStream vacationsExport(List<Vacation> vacations, TreeMap<String, Long> freeDays) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(70);
            table.setWidths(new int[]{1, 3, 3, 3});
            BaseFont baseFont = BaseFont.createFont("/assets/fonts/ArialMT.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font font = new Font(baseFont, 12, Font.NORMAL);

            PdfPCell hcell;
            hcell = new PdfPCell(new Phrase("ID", font));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("ФИО сотрудника", font));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Дата начала", font));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Дата Окончания", font));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(hcell);

            for (Vacation vacation : vacations) {
                PdfPCell cell;

                cell = new PdfPCell(new Phrase(vacation.getVacationId().toString(), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(vacation.getEmployee().getFullName(), font));
                cell.setPaddingLeft(5);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(vacation.getVacationStartDate().toString(), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setPaddingRight(5);
                table.addCell(cell);

                cell = new PdfPCell(new Phrase(vacation.getVacationEndDate().toString(), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                cell.setPaddingRight(5);
                table.addCell(cell);


                String employeeName = vacation.getEmployee().getFullName();

                LocalDate startDate = vacation.getVacationStartDate();
                LocalDate endDate = vacation.getVacationEndDate();
                Long spentDays = -(ChronoUnit.DAYS.between(startDate, endDate));
                freeDays.merge(employeeName, spentDays, Long::sum);
            }

            PdfPTable spentDaysTable = new PdfPTable(2);
            spentDaysTable.setWidthPercentage(70);
            spentDaysTable.setWidths(new int[]{3, 3});

            hcell = new PdfPCell(new Phrase("ФИО сотрудника", font));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            spentDaysTable.addCell(hcell);

            hcell = new PdfPCell(new Phrase("Осталось дней которые можно взять", font));
            hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
            spentDaysTable.addCell(hcell);

            freeDays.forEach((s, aLong) ->{
                PdfPCell cell;
                cell = new PdfPCell(new Phrase(s, font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                spentDaysTable.addCell(cell);

                cell = new PdfPCell(new Phrase(String.valueOf(aLong), font));
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                spentDaysTable.addCell(cell);
            });


            PdfWriter.getInstance(document, out);
            document.open();
            document.add(table);
            document.newPage();
            document.add(spentDaysTable);

            document.close();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
        return new ByteArrayInputStream(out.toByteArray());
    }


}
