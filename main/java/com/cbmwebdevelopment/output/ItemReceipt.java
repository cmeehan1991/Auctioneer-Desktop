/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbmwebdevelopment.output;

import static com.cbmwebdevelopment.main.Values.CURRENCY_FORMAT;

import java.awt.EventQueue;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;

import com.cbmwebdevelopment.alerts.Alerts;
import com.cbmwebdevelopment.tablecontrollers.CheckoutItemTableViewController.ItemData;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.scene.control.TableView;

/**
 *
 * @author cmeehan
 */
public class ItemReceipt {

    /**
     * Gets the AWT event back onto the main UI thread. 
     * @param run 
     */
    private static void invokeInUI(Runnable run) {
        if (EventQueue.isDispatchThread()) {
            run.run();
        } else {
            EventQueue.invokeLater(run);
        }
    }
    private final String bidderId, bidderName, numberOfItems, totalDue, billingAddress;
    private final TableView tableView;
    private final String USER_HOME = System.getProperty("user.home");
    private final String OS = System.getProperty("os.name");
    private PrinterJob job;
    private String temp;

    private File file;

    public ItemReceipt(String bidderId, String bidderName, String numberOfItems, String totalDue, String billingAddress, TableView tableView) {
        this.bidderId = bidderId;
        this.bidderName = bidderName;
        this.numberOfItems = numberOfItems;
        this.billingAddress = billingAddress;
        this.totalDue = totalDue;
        this.tableView = tableView;
    }

    /**
     * Bidder information such as billing address, name, etc.
     * 
     * @return PDF Table
     * 
     * @throws IOException
     * @throws ParseException 
     */
    private Table bidderInformationTable() throws IOException, ParseException {
        Table table = new Table(3);
        table.setWidthPercent(100f);

        table.addCell(new Cell()
                .add(new Paragraph("Bidder ID:"))
                .setFont(PdfFontFactory.createFont(FontConstants.TIMES_BOLD))
                .setFontSize(12).setBorder(Border.NO_BORDER).setWidthPercent(15f));

        table.addCell(new Cell()
                .add(new Paragraph(bidderId))
                .setFont(PdfFontFactory.createFont(FontConstants.TIMES_ROMAN))
                .setFontSize(12).setBorder(Border.NO_BORDER).setWidthPercent(42.5f));

        table.addCell(new Cell()
                .add(new Paragraph("Billing Address:"))
                .setFont(PdfFontFactory.createFont(FontConstants.TIMES_BOLD))
                .setFontSize(12).setBorder(Border.NO_BORDER));

        table.addCell(new Cell()
                .add(new Paragraph("Name:"))
                .setFont(PdfFontFactory.createFont(FontConstants.TIMES_BOLD))
                .setFontSize(12).setBorder(Border.NO_BORDER));

        table.addCell(new Cell()
                .add(new Paragraph(bidderName))
                .setFont(PdfFontFactory.createFont(FontConstants.TIMES_ROMAN))
                .setFontSize(12).setBorder(Border.NO_BORDER));

        table.addCell(new Cell(3, 1)
                .add(new Paragraph(billingAddress))
                .setFont(PdfFontFactory.createFont(FontConstants.TIMES_ROMAN))
                .setFontSize(12).setBorder(Border.NO_BORDER));

        table.addCell(new Cell()
                .add(new Paragraph("No. Items"))
                .setFont(PdfFontFactory.createFont(FontConstants.TIMES_BOLD))
                .setFontSize(12).setBorder(Border.NO_BORDER));

        table.addCell(new Cell()
                .add(new Paragraph(numberOfItems))
                .setFont(PdfFontFactory.createFont(FontConstants.TIMES_ROMAN))
                .setFontSize(12).setBorder(Border.NO_BORDER));

        table.addCell(new Cell()
                .add(new Paragraph("Total Due:"))
                .setFont(PdfFontFactory.createFont(FontConstants.TIMES_BOLD))
                .setFontSize(12).setBorder(Border.NO_BORDER));

        table.addCell(new Cell()
                .add(new Paragraph(CURRENCY_FORMAT.format(Double.parseDouble(CURRENCY_FORMAT.parse(totalDue).toString()))))
                .setFont(PdfFontFactory.createFont(FontConstants.TIMES_ROMAN))
                .setFontSize(12).setBorder(Border.NO_BORDER));

        table.setBorder(Border.NO_BORDER);

        return table;
    }

    /**
     * Creates and email client to email the invoice in PDF format to either one or multiple recipients. 
     * Recipients must be separated by a semicolon (;)
     * 
     * @param id
     * @param recipient
     * @throws IOException
     * @throws FileNotFoundException
     * @throws ParseException 
     */
    public void emailPDF(String id, String recipient) throws IOException, FileNotFoundException, ParseException {
        // Set up the email
        String smtpHost = "mail.cbmwebdevelopment.com";
        int smtpPort = 25;

        String sender = "connor.meehan@cbmwebdevelopment.com";
        String messageContent = "This message was automatically generated by Auctioneer for The Pregnancy Center of Hilton Head. Please do not respond to this email.\n\n";
        String subject = this.bidderName + " item receipt."; // Need to add the date here

        // Mailer properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", smtpHost);
        properties.put("mail.smtp.port", smtpPort);
        Session session = Session.getDefaultInstance(properties, null);

        ByteArrayOutputStream outputStream = null;

        try {
            // Construct the text body part
            MimeBodyPart textBodyPart = new MimeBodyPart();
            textBodyPart.setText(messageContent);

            // now write the PDF content to the output stream
            outputStream = new ByteArrayOutputStream();
            pdf(null, outputStream);
            byte[] bytes = outputStream.toByteArray();

            // Construct the pdf body part
            DataSource dataSource = new ByteArrayDataSource(bytes, "application/pdf");
            MimeBodyPart pdfBodyPart = new MimeBodyPart();
            pdfBodyPart.setDataHandler(new DataHandler(dataSource));
            pdfBodyPart.setFileName("Item Receipt: " + id + ".pdf");

            // Construct the mime multi part
            MimeMultipart mimeMultiPart = new MimeMultipart();
            mimeMultiPart.addBodyPart(textBodyPart);
            mimeMultiPart.addBodyPart(pdfBodyPart);

            // create the sender/recipient addresses
            InternetAddress iaSender = new InternetAddress(sender);
            InternetAddress iaRecipient = new InternetAddress("cmeehan@elon.edu");

            // Construct the mime message
            MimeMessage mimeMessage = new MimeMessage(session);
            // Check if there is only one recipient or handle multiple
            if (recipient.contains(";")) {
                String[] recipients = recipient.trim().split(";");
                for (String to : recipients) {
                    mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
                    System.out.println(to);
                }
            } else {
                mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            }

            mimeMessage.setSender(iaSender);
            mimeMessage.setFrom(iaSender);
            mimeMessage.setSubject(subject);
            mimeMessage.setContent(mimeMultiPart);

            System.out.println("Constructed the mime message");

            Transport.send(mimeMessage);

            System.out.println("Sent the message");

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Message Sent");
            alert.setHeaderText("Message Successfully Sent");
            alert.showAndWait();

        } catch (MessagingException ex) {
            System.err.println(ex.getMessage());
            ArrayList<String> list = new ArrayList<>();
            list.add(ex.getMessage());
            Alert alert = new Alerts().errorAlert("Email Failure", "Email failed to send", "Cause:", list);
            alert.showAndWait();
        } finally {
            // clean off
            if (null != outputStream) {
                try {
                    outputStream.close();
                    outputStream = null;
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
    }

    /**
     * Invoice item table. 
     * Contains each item won by the specified bidder. 
     * 
     * @return PDF Table
     * 
     * @throws IOException 
     */
    private Table itemTable() throws IOException {
        Table table = new Table(4);
        table.setWidthPercent(100f);

        table.addCell(new Cell(1, 4)
                .add(new Paragraph("Items"))
                .setFont(PdfFontFactory.createFont(FontConstants.TIMES_BOLD))
                .setFontSize(18f)
                .setBorder(Border.NO_BORDER));

        table.addCell(new Cell()
                .add(new Paragraph("Item ID"))
                .setFont(PdfFontFactory.createFont(FontConstants.TIMES_BOLD))
                .setFontSize(12));

        table.addCell(new Cell()
                .add(new Paragraph("Item Name"))
                .setFont(PdfFontFactory.createFont(FontConstants.TIMES_BOLD))
                .setFontSize(12));

        table.addCell(new Cell()
                .add(new Paragraph("Item Description"))
                .setFont(PdfFontFactory.createFont(FontConstants.TIMES_BOLD))
                .setFontSize(12));

        table.addCell(new Cell()
                .add(new Paragraph("Winning Bid"))
                .setFont(PdfFontFactory.createFont(FontConstants.TIMES_BOLD))
                .setFontSize(12));

        ObservableList<ItemData> data = tableView.getItems();

        data.forEach((item) -> {
            try {
                String itemId = item.getItemId() == null ? "" : item.getItemId();
                String itemName = item.getItemName() == null ? "" : item.getItemName();
                String itemDescription = item.getItemDescription() == null ? "" : item.getItemDescription();
                Double itemAmount = CURRENCY_FORMAT.parse(item.getWinningBidAmount()).toString() == null ? 0.00 : Double.parseDouble(CURRENCY_FORMAT.parse(item.getWinningBidAmount()).toString());
                table.addCell(new Cell()
                        .add(new Paragraph(itemId))
                        .setFont(PdfFontFactory.createFont(FontConstants.TIMES_ROMAN))
                        .setFontSize(12));

                table.addCell(new Cell()
                        .add(new Paragraph(itemName))
                        .setFont(PdfFontFactory.createFont(FontConstants.TIMES_ROMAN))
                        .setFontSize(12));

                table.addCell(new Cell()
                        .add(new Paragraph(itemDescription))
                        .setFont(PdfFontFactory.createFont(FontConstants.TIMES_ROMAN))
                        .setFontSize(12));

                table.addCell(new Cell()
                        .add(new Paragraph(CURRENCY_FORMAT.format(itemAmount)))
                        .setFont(PdfFontFactory.createFont(FontConstants.TIMES_ROMAN))
                        .setFontSize(12));

            } catch (IOException | ParseException ex) {
                System.err.println(ex.getMessage());
            }

        });

        return table;
    }

    /**
     * Logic for creating the invoice PDF. 
     * Must have either String file destination or ByteArrayOutputStream destination. 
     * Whichever one is not used must be null
     * 
     * @param fileDest - null or String type file destination
     * @param byteDest - null or ByteArrayOutputStream
     * @return PDF created and saved to the specified destination
     * 
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ParseException 
     */
    private Document pdf(String fileDest, ByteArrayOutputStream byteDest) throws FileNotFoundException, IOException, ParseException {
        PdfWriter writer;
        if (fileDest != null) {
            writer = new PdfWriter(fileDest);
        } else if (byteDest != null) {
            writer = new PdfWriter(byteDest);
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Invalid Destination");
            alert.setHeaderText("Invalide file destination selected.");
            alert.setContentText("The programmatically selected file destination has failed. Please try again or contact your systems administrator for assistance.");
            alert.showAndWait();
            return null;
        }
        PdfDocument pdf = new PdfDocument(writer);

        Document document = new Document(pdf);

        Image image = new Image(ImageDataFactory.create(getClass().getResource("/images/auction.jpg")));
        image.setWidth(150f);
        image.setHorizontalAlignment(HorizontalAlignment.CENTER);

        document.add(image);
        document.add(bidderInformationTable());
        document.add(itemTable());
        document.add(new Paragraph("Thank you for your contribution to the continued success of The Pregnancy Center and Clinic of the Low Country.")
                .setFontSize(18)
                .setFont(PdfFontFactory.createFont(FontConstants.TIMES_BOLD))
                .setUnderline());

        document.close();

        System.out.println("Closed");

        return document;
    }

    /**
     * Creates a PDDocument to be read by the PrinterJob. 
     * @param dest
     * @return
     * @throws IOException 
     */
    public PDDocument printablePdf(String dest) throws IOException {
        PDDocument pdf = PDDocument.load(new File(dest));
        return pdf;
    }

    /**
     * Print the receipt.
     *
     * @param id
     * @throws java.io.IOException
     */
    public void printReceipt(String id) throws IOException {
        job = PrinterJob.getPrinterJob();
        PageFormat format = job.defaultPage();

        job.setPrintable(null, format);

        if (OS.contains("Mac")) {
            temp = "/tmp/" + id + ".pdf";
        }

        if (OS.contains("Windows")) {
            temp = "/local/" + id + ".pdf";
        }
        invokeInUI(() -> {
            try {
                pdf(temp, null);
                job.setPageable(new PDFPageable(printablePdf(temp)));
                // Open the print dialog
                if (job.printDialog()) {
                    job.print();
                }
            } catch (PrinterException | IOException | NullPointerException | ParseException ex) {
                System.out.println("Error: ");
                System.err.println(ex.getMessage());
                ArrayList<String> errors = new ArrayList<>();
                errors.add(ex.getMessage());
                Alert alert = new Alerts().errorAlert("Printer Error", "Failed to print invoice.", "Error:", errors);
                alert.showAndWait();
            }
        });
    }

    /**
     * Saves the invoice as a PDF to the user's machine.
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ParseException 
     */
    public void saveAsPDF() throws FileNotFoundException, IOException, ParseException {
        FileChooser fc = new FileChooser();
        System.out.println(USER_HOME);
        fc.setInitialDirectory(new File(USER_HOME + "/Documents"));
        fc.setSelectedExtensionFilter(new ExtensionFilter("PDF", ".pdf"));
        fc.setInitialFileName(bidderName + " Items.pdf");
        String saveTo = fc.showSaveDialog(new Stage()).toString();
        pdf(saveTo, null);
    }
}
