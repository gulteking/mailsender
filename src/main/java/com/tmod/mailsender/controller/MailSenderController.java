package com.tmod.mailsender.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by gulteking on 4.01.2018 with love.
 */
@RestController
@RequestMapping("/mail")
public class MailSenderController {


    private JavaMailSender emailSender;

    @Autowired
    public MailSenderController(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    @PostMapping
    public void sendMail(
            @RequestParam("file") MultipartFile multipartFile,
            @RequestParam("to") List<String> to,
            @RequestParam("subject") String subject,
            @RequestParam(value = "cc", required = false) List<String> ccList,
            @RequestParam(value = "bcc", required = false) List<String> bccList,
            @RequestParam("body") String multiLineBody,
            @RequestParam("fontfamily") String fontFamily,
            @RequestParam("fontsize") Integer fontSize,
            @RequestParam("fonttype") FontTypeEnum fontType,
            @RequestParam("fontcolor") List<Integer> fontColor,
            @RequestParam("startx") Integer startX,
            @RequestParam("starty") Integer startY,
            @RequestParam("endx") Integer endX,
            @RequestParam("alignment") AlignmentEnum alignment,
            HttpServletResponse servletResponse) throws MessagingException, IOException {
        if (fontColor.size() != 3) {
            servletResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "invalid font color parameter. " +
                    "color array size must be 3");
        }
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        if (ccList != null && !ccList.isEmpty())
            helper.setCc(ccList.toArray(new String[ccList.size()]));

        if (bccList != null && !bccList.isEmpty())
            helper.setBcc(bccList.toArray(new String[bccList.size()]));


        helper.setTo(to.toArray(new String[to.size()]));
        helper.setSubject(subject);
        helper.setText("<html><body><img src='cid:id101'/><body></html>", true);


        Color color = new Color(fontColor.get(0), fontColor.get(1), fontColor.get(2));
        String[] multilineBodyArr = multiLineBody.split("\\r?\\n");

        byte[] imageBytes = writeTextToImage(
                startX,
                startY,
                endX,
                new Font(fontFamily, fontType.ordinal(), fontSize),
                color,
                multilineBodyArr,
                multipartFile,
                alignment);


        ByteArrayResource imageResource = new ByteArrayResource(imageBytes);
        helper.addInline("id101", imageResource, "image/png");

        emailSender.send(message);


    }

    private byte[] writeTextToImage(
            int startX,
            int startY,
            int endX,
            Font textFont,
            Color textColor,
            String[] multiLineText,
            MultipartFile multipartFile,
            AlignmentEnum alignment) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(multipartFile.getInputStream());

        Graphics graphics = bufferedImage.getGraphics();
        graphics.setColor(textColor);
        graphics.setFont(textFont);

        FontMetrics metrics = graphics.getFontMetrics();

        int areaSize = endX - startX;
        for (String text : multiLineText) {
            if (alignment.equals(AlignmentEnum.CENTER)) {
                int startPointX = startX + ((areaSize - metrics.stringWidth(text)) / 2);
                graphics.drawString(text, startPointX, startY);
            } else {
                graphics.drawString(text, startX, startY);

            }
            startY += metrics.getAscent() + 3;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);

        return baos.toByteArray();
    }
}
