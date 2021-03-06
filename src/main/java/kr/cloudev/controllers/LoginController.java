package kr.cloudev.controllers;

import kr.cloudev.models.view.LoginModel;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class LoginController {

    @RequestMapping("/login")
    public ModelAndView login(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        GitHub github = (GitHub) session.getAttribute("github");

        if (github != null) {
            response.sendRedirect("/");
            return null;
        }

        LoginModel model = new LoginModel();

        model.setTitle("Login - Cloudev");

        return new ModelAndView("login", "model", model);
    }

    @PostMapping("/login.do")
    public String doLogin(HttpServletRequest request) {
        HttpSession session = request.getSession();
        String token = request.getParameter("token");

        if (token != null) {
            session.setAttribute("token", token);

            return "redirect:/login_confirm.do";
        }

        return "redirect:/login";
    }

    @RequestMapping("/login_confirm.do")
    public String doLoginConfirm(HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession();
        String token = (String) session.getAttribute("token");

        GitHub github = new GitHubBuilder().withOAuthToken(token).build();

        if (github.isCredentialValid()) {
            session.setAttribute("github", github);
            session.setAttribute("user", github.getMyself());

            return "redirect:/";
        }
        session.setAttribute("token", null);

        return "redirect:/login";
    }

    @RequestMapping("/login_out.do")
    public String doLoginOut(HttpServletRequest request) {
        HttpSession session = request.getSession();

        session.setAttribute("token", null);
        session.setAttribute("github", null);
        session.setAttribute("user", null);

        return "redirect:/";
    }

}
