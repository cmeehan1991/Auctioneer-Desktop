package com.cbmwebdevelopment.output;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.cbmwebdevelopment.alerts.Alerts;
import com.cbmwebdevelopment.items.Item;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author cmeehan
 */
public class ItemList {

    Row row;
    Item item;
    private final String auctionId;
    private final String USER_HOME = System.getProperty("user.home");
    private int rowNum = 1;

    public ItemList(String auctionId) {
        this.auctionId = auctionId;
    }

    private void createExcel(FileOutputStream fileOut, ByteArrayOutputStream output) throws IOException {
        Workbook wb = new HSSFWorkbook();
        Sheet sheet = wb.createSheet("Winning Bids");

        row = sheet.createRow(0);

        // Create the header row
        row.createCell(0).setCellValue("Item ID");
        row.createCell(1).setCellValue("Item Name");
        row.createCell(2).setCellValue("Item Description");
        row.createCell(3).setCellValue("Auction Type");
        row.createCell(4).setCellValue("Item Status");
        row.createCell(5).setCellValue("Item Category");
        item = new Item();
        System.out.println(this.auctionId);
        item.itemInformation(this.auctionId).forEach((key, value) -> {
            System.out.println(item);
            row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(key);
            row.createCell(1).setCellValue(value.get(0));
            row.createCell(2).setCellValue(value.get(1));
            row.createCell(3).setCellValue(value.get(2));
            row.createCell(4).setCellValue(value.get(3));
            row.createCell(5).setCellValue(value.get(4));
            rowNum++;
        });

        if (fileOut == null) {
            wb.write(output);
            output.close();
        }

        if (output == null) {
            wb.write(fileOut);
            wb.close();
            fileOut.close();
        }
    }

    /**
     * Creates and email client to email the invoice in PDF format to either one
     * or multiple recipients. Recipients must be separated by a semicolon (;)
     *
     * @param id
     * @param recipient
     * @throws IOException
     * @throws FileNotFoundException
     * @throws ParseException
     */
    public void emailList(String recipient) throws IOException, FileNotFoundException, ParseException {
        // Set up the email
        String smtpHost = "mail.cbmwebdevelopment.com";
        int smtpPort = 25;

        String sender = "connor.meehan@cbmwebdevelopment.com";
        String messageContent = "This message was automatically generated by Auctioneer for The Pregnancy Center of Hilton Head. Please do not respond to this email.\n\n";
        String subject = "Auction Item List."; // Need to add the date here

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
            createExcel(null, outputStream);
            byte[] bytes = outputStream.toByteArray();

            // Construct the pdf body part
            DataSource dataSource = new ByteArrayDataSource(bytes, "application/excel");
            MimeBodyPart pdfBodyPart = new MimeBodyPart();
            pdfBodyPart.setDataHandler(new DataHandler(dataSource));
            pdfBodyPart.setFileName("Auction Items.xls");

            // Construct the mime multi part
            MimeMultipart mimeMultiPart = new MimeMultipart();
            mimeMultiPart.addBodyPart(textBodyPart);
            mimeMultiPart.addBodyPart(pdfBodyPart);

            // create the sender/recipient addresses
            InternetAddress iaSender = new InternetAddress(sender);

            // Construct the mime message
            MimeMessage mimeMessage = new MimeMessage(session);
            // Check if there is only one recipient or handle multiple
            if (recipient.contains(";")) {
                String[] recipients = recipient.trim().split(";");
                for (String to : recipients) {
                    mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to.trim()));
                }
            } else {
                mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            }

            mimeMessage.setSender(iaSender);
            mimeMessage.setFrom(iaSender);
            mimeMessage.setSubject(subject);
            mimeMessage.setContent(mimeMultiPart);

            Transport.send(mimeMessage);

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
     * Exports the winning bids to an excel file on the user's machine.
     *
     */
    public void exportWinningBidsToExcel() {
        // Instantiate the workbook

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Table To");
        fileChooser.setInitialDirectory(new File(USER_HOME + "/Documents"));
        fileChooser.setInitialFileName("Auction Items.xls");
        try {
            FileOutputStream fileOut = new FileOutputStream(fileChooser.showSaveDialog(new Stage()).toString());
            createExcel(fileOut, null);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
