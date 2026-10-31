package five2nine.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.JavaMailSender;
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String mailUsername;

    public void sendPasswordResetEmail(
            String to,
            String resetLink
    ) {
        try{
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(mailUsername);
        helper.setTo(to);
        helper.setSubject("Password Reset Request: Reset your Five2Nine password");

        String html = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Password Reset</title>
                    <style>
                        * { box-sizing: border-box; }
                        body { margin: 0; padding: 0; background-color: #f4f7f6; font-family: Arial, Helvetica, sans-serif; color: #1f2937; }
                        .email-wrapper { width: 100%%; padding: 40px 16px; }
                        .email-container { max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 16px; overflow: hidden; border: 1px solid #e5e7eb; box-shadow: 0 8px 30px rgba(0, 0, 0, 0.06); }
                        .header { background: linear-gradient(135deg, #1F8A70, #16745f); padding: 32px 40px; text-align: center; }
                        .logo { display: inline-block; margin-bottom: 12px; color: #ffffff; font-size: 24px; font-weight: 700; letter-spacing: -0.5px; }
                        .header-title { margin: 0; color: #ffffff; font-size: 25px; font-weight: 700; line-height: 1.3; }
                        .content { padding: 40px; }
                        .icon-wrapper { width: 64px; height: 64px; margin: 0 auto 24px; border-radius: 50%%; background-color: #e7f5f0; display: flex; align-items: center; justify-content: center; }
                        .lock-icon { font-size: 28px; }
                        .greeting { margin: 0 0 16px; font-size: 17px; font-weight: 600; color: #111827; }
                        .text { margin: 0 0 20px; font-size: 15px; line-height: 1.7; color: #6b7280; }
                        .button-wrapper { text-align: center; margin: 32px 0; }
                        .reset-button { display: inline-block; padding: 14px 30px; background-color: #1F8A70; color: #ffffff !important; text-decoration: none; border-radius: 8px; font-size: 15px; font-weight: 700; letter-spacing: 0.2px; }
                        .expiry-box { margin: 28px 0; padding: 16px 18px; background-color: #fff8e6; border: 1px solid #f3dfaa; border-radius: 8px; }
                        .expiry-title { margin: 0 0 6px; font-size: 14px; font-weight: 700; color: #8a6500; }
                        .expiry-text { margin: 0; font-size: 13px; line-height: 1.6; color: #8a6500; }
                        .security-box { margin-top: 28px; padding: 18px; background-color: #f8faf9; border-radius: 10px; border: 1px solid #e5ebe8; }
                        .security-title { margin: 0 0 8px; font-size: 14px; font-weight: 700; color: #374151; }
                        .security-text { margin: 0; font-size: 13px; line-height: 1.6; color: #6b7280; }
                        .footer { padding: 24px 40px; background-color: #f8faf9; border-top: 1px solid #e5e7eb; text-align: center; }
                        .footer-text { margin: 0; font-size: 12px; line-height: 1.6; color: #9ca3af; }
                        .footer-brand { margin-top: 8px; font-size: 12px; font-weight: 600; color: #1F8A70; }
                        @media only screen and (max-width: 600px) {
                            .email-wrapper { padding: 20px 10px; }
                            .header { padding: 28px 24px; }
                            .content { padding: 30px 24px; }
                            .footer { padding: 22px 24px; }
                            .header-title { font-size: 22px; }
                            .reset-button { display: block; width: 100%%; padding: 14px 20px; }
                        }
                    </style>
                </head>
                <body>
                    <div class="email-wrapper">
                        <div class="email-container">
                            <div class="header">
                                <div class="logo">Five2Nine</div>
                                <h1 class="header-title">Reset Your Password</h1>
                            </div>
                            <div class="content">
                                <div class="icon-wrapper">
                                    <span class="lock-icon">🔐</span>
                                </div>
                                <p class="greeting">Hello,</p>
                                <p class="text">We received a request to reset the password associated with your Five2Nine account.</p>
                                <p class="text">If you made this request, click the button below to securely create a new password.</p>
                                <div class="button-wrapper">
                                    <a href="%s" class="reset-button">Reset My Password</a>
                                </div>
                                <div class="expiry-box">
                                    <p class="expiry-title">⏱ This link expires in 10 minutes</p>
                                    <p class="expiry-text">For security reasons, this password-reset link is valid for only 10 minutes. After that, you will need to request a new reset link.</p>
                                </div>
                                <p class="text">If you did not request a password reset, you can safely ignore this email. Your password will remain unchanged.</p>
                                <div class="security-box">
                                    <p class="security-title">🛡 Security Notice</p>
                                    <p class="security-text">Never share your password or password-reset link with anyone. Five2Nine will never ask you to provide your password through email.</p>
                                </div>
                            </div>
                            <div class="footer">
                                <p class="footer-text">This is an automated email. Please do not reply to this message.</p>
                                <p class="footer-text">If you continue to experience problems, please contact our support team.</p>
                                <div class="footer-brand">© 2026 Five2Nine. All rights reserved.</div>
                            </div>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(resetLink);

        helper.setText(html, true);

        mailSender.send(message);
    }
        catch (MessagingException e) {

            throw new RuntimeException(
                    "Failed to send password reset email", e
            );
        }
    }
}
