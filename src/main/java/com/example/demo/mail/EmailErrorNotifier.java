package com.example.demo.mail;

import java.io.IOException;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

import org.springframework.beans.factory.annotation.Value;

import lombok.extern.java.Log;

@Log
// @Component
public class EmailErrorNotifier implements ErrorNotifier {

    @Value("${sendgrid.api.key}")
    private String SENDGRID_API_KEY;

    @Override
    public void notifySystemError(Email from, String subject, Email to, Content content) {
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(SENDGRID_API_KEY);
        Request request = new Request();
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException ex) {
            log.info(ex + "");
            throw new RuntimeException(ex.getCause());
        }
    }
}