package fr.efrei.nanoorbit.controller;

import fr.efrei.nanoorbit.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Controller
public class LoginController {

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        if (session.getAttribute("user") != null) {
            return "redirect:/dashboard";
        }
        return "login";
    }

    @PostMapping("/login")
    public String loginProcess(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        try (Connection conn = DriverManager.getConnection(dbUrl, username, password)) {

            String sqlRole = "SELECT DISTINCT GRANTEE FROM information_schema.USER_PRIVILEGES WHERE GRANTEE LIKE ?";
            String detectRole = "analyste_data"; // Rôle par défaut si aucun privilège global n'est trouvé

            try (PreparedStatement stmt = conn.prepareStatement(sqlRole)) {
                stmt.setString(1, "'" + username + "'@'%'");
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String grantee = rs.getString("GRANTEE");

                        if (grantee != null && grantee.startsWith("'")) {
                            detectRole = grantee.substring(1, grantee.indexOf("'", 1));
                        }
                    }
                }
            }

            User user = new User(username, detectRole);
            session.setAttribute("user", user);

            return "redirect:/dashboard";

        } catch (Exception e) {
            model.addAttribute("error", "Identifiant ou mot de passe MySQL incorrect.");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout=true";
    }
}