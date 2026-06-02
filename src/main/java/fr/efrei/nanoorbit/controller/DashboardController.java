package fr.efrei.nanoorbit.controller;

import fr.efrei.nanoorbit.model.User;
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

    @GetMapping("/vue/{nomVue}")
    public String afficherVue(@PathVariable String nomVue, HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("user");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        String sql = "";
        String titre = "";

        switch (nomVue) {
            case "alertes":
                sql = "SELECT * FROM vue_alertes_instruments";
                titre = "Alertes Instruments";
                break;
            case "communications":
                sql = "SELECT * FROM vue_bilan_communications";
                titre = "Bilan des Communications";
                break;
            case "missions":
                sql = "SELECT * FROM vue_tableau_de_bord_missions";
                titre = "Missions Publiques";
                break;
            case "operationnels":
                sql = "SELECT * FROM vue_satellites_operationnels";
                titre = "Satellites Opérationnels";
                break;
            case "historique":
                sql = "SELECT f.date_heure_debut, f.duree, s.nom_satellite, s2.nom_station, s3.nom_statut \n" +
                        "FROM fenetrecommunication f\n" +
                        "INNER JOIN satellite s ON s.id_satellite = f.id_satellite  \n" +
                        "INNER JOIN station s2 ON s2.id_station = f.id_station\n" +
                        "INNER JOIN statut s3 ON s3.id_statut = f.id_statut AND s3.nom_statut = 'Realisee' AND s3.type_statut = 'FenetreCommunication'\n" +
                        "ORDER BY f.date_heure_debut desc ;";
                titre = "Historique des Fenêtres de Communication ";
                break;
            default:
                return "redirect:/dashboard";
        }

        List<Map<String, Object>> resultats = jdbcTemplate.queryForList(sql);

        if ("alertes".equals(nomVue)) {
            long compteurCritique = 0;
            for (Map<String, Object> ligne : resultats) {
                for (Map.Entry<String, Object> entree : ligne.entrySet()) {
                    if (entree.getKey().toLowerCase().contains("priorit")) {
                        if (entree.getValue() != null && "CRITIQUE".equalsIgnoreCase(entree.getValue().toString().trim())) {
                            compteurCritique++;
                        }
                    }
                }
            }
            model.addAttribute("compteurCritique", compteurCritique);
        }

        model.addAttribute("username", loggedInUser.getUsername());
        model.addAttribute("role", loggedInUser.getRoles());
        model.addAttribute("donnees", resultats);
        model.addAttribute("titreVue", titre);
        model.addAttribute("vueActive", nomVue);

        return "dashboard";
    }
}