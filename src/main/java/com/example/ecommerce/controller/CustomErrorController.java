package com.example.ecommerce.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object messageObj = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);

        int status = statusObj != null ? Integer.parseInt(statusObj.toString()) : 500;
        String message = messageObj != null ? messageObj.toString() : "Ein unerwarteter Fehler ist aufgetreten";

        String errorText;

        switch (status) {
            case 400:
                errorText = "Fehlerhafte Anfrage";
                if (message.isEmpty()) message = "Die Anfrage konnte nicht verarbeitet werden.";
                break;
            case 403:
                errorText = "Zugriff verweigert";
                if (message.isEmpty()) message = "Sie haben keine Berechtigung, diese Seite zu sehen.";
                break;
            case 404:
                errorText = "Seite nicht gefunden";
                if (message.isEmpty()) message = "Die angeforderte Seite existiert nicht.";
                break;
            case 500:
                errorText = "Interner Serverfehler";
                if (message.isEmpty()) message = "Es ist ein interner Fehler aufgetreten. Bitte versuchen Sie es später erneut.";
                break;
            default:
                errorText = "Unbekannter Fehler";
                if (message.isEmpty()) message = "Es ist ein Fehler aufgetreten.";
        }

        model.addAttribute("status", status);
        model.addAttribute("error", errorText);
        model.addAttribute("message", message);

        return "error/error";
    }
}
