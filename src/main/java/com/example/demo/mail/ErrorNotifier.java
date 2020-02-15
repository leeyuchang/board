package com.example.demo.mail;

import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;

public interface ErrorNotifier {

    public void notifySystemError(Email from, String subject, Email to, Content content);

}
