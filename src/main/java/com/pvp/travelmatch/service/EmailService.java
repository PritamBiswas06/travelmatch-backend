package com.pvp.travelmatch.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

//    public void sendEmail(String to, String subject, String body) {
//
//        SimpleMailMessage message = new SimpleMailMessage();
//
//        message.setTo(to);
//        message.setSubject(subject);
//        message.setText(body);
//
//        mailSender.send(message);
//    }



    public void sendHtmlEmail(String to, String subject, String htmlBody) {

        try {
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("pritamkumaruemlab@gmail.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            mailSender.send(message);

        } catch (Exception e) {
            System.out.println("Email failed but registration continues");
            e.printStackTrace();
        }
    }

    public void sendOtpEmail(String email, String otp) {

        String html = """
<html>
<body style="font-family:Arial;background:#f4f6fb;padding:30px;">

<div style="max-width:600px;margin:auto;background:white;border-radius:12px;
box-shadow:0 10px 40px rgba(0,0,0,0.1);overflow:hidden;">

<div style="background:#0d78e3;color:white;padding:20px;text-align:center;font-size:22px;">
✈ TravelMatch
</div>

<div style="padding:30px;text-align:center;">

<h2>Email Verification</h2>

<p>Your TravelMatch verification code is:</p>

<div style="font-size:32px;font-weight:bold;
letter-spacing:6px;margin:20px 0;">
%s
</div>

<p>This code will expire in 10 minutes.</p>

<p style="font-size:12px;color:#888;">
If you didn't request this, you can safely ignore this email.
</p>

</div>

</div>

</body>
</html>
""".formatted(otp);

        sendHtmlEmail(
                email,
                "Your TravelMatch Verification Code",
                html
        );
    }
}
