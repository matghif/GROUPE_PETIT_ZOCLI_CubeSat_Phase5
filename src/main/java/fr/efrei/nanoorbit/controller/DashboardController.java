package fr.efrei.nanoorbit.controller;

import fr.efrei.nanoorbit.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class DashboardController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("user");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        String sql = "SELECT id_satellite, nom_satellite, format_cube_sat, date_lancement FROM Satellite";
        List<Map<String, Object>> satellites = jdbcTemplate.queryForList(sql);

        model.addAttribute("username", loggedInUser.getUsername());
        model.addAttribute("role", loggedInUser.getRoles());
        model.addAttribute("satellites", satellites);

        return "dashboard";
    }
    
}