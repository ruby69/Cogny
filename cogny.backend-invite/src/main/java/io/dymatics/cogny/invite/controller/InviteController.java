package io.dymatics.cogny.invite.controller;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import io.dymatics.cogny.invite.service.InviteService;

@Controller
public class InviteController implements ErrorController {
    private static final Calendar calendar = Calendar.getInstance();

    @Autowired private InviteService inviteService;

    @RequestMapping(value = "{code}")
    public Object redirect(@PathVariable("code") String code) {
        String redirectUrl = inviteService.getRedirectUrl(code);
        if (redirectUrl == null) {
            return new RedirectView("x/" + code, true);
        } else {
            return new RedirectView(redirectUrl);
        }
    }

    @RequestMapping(value = "x/{code}")
    @ResponseBody
    public Object invalid(@PathVariable("code") String code) {
        return "Unavailable code : " + code;
    }

    @RequestMapping(value = "healthcheck")
    @ResponseBody
    public Object healthcheck() {
        return calendar.getTime().toString();
    }

    @RequestMapping("/error")
    @ResponseBody
    public String handleError() {
        return "Unavailable";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
