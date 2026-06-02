package fr.efrei.nanoorbit.controller;

import fr.efrei.nanoorbit.model.User;
import fr.efrei.nanoorbit.service.DashboardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@Controller
public class DashboardController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DashboardService dashboardService;

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
        model.addAttribute("donnees", satellites);
        model.addAttribute("titreVue", "Liste des Satellites");
        model.addAttribute("vueActive", "brute");

        return "dashboard";
    }

    @GetMapping("/vue/{viewName}")
    public String afficherVue(@PathVariable String viewName, HttpSession session, Model model) {
        return loadView(viewName, session, model);
    }

    private String loadView(String viewName, HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("user");
        if (loggedInUser == null) {
            return "redirect:/login";
        }
        String title = dashboardService.getTitleForView(viewName);
        if ("Vue Inconnue".equals(title)) {
            return "redirect:/dashboard";
        }

        List<Map<String, Object>> resultats = dashboardService.getDataForView(viewName);

        if ("alertes".equals(viewName)) {
            long compteurCritique = dashboardService.calculateCriticalCount(resultats);
            model.addAttribute("compteurCritique", compteurCritique);
        }

        model.addAttribute("username", loggedInUser.getUsername());
        model.addAttribute("role", loggedInUser.getRoles());
        model.addAttribute("donnees", resultats);
        model.addAttribute("titreVue", title);
        model.addAttribute("vueActive", viewName);

        return "dashboard";
    }
}