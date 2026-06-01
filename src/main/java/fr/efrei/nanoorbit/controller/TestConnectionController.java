package fr.efrei.nanoorbit.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestConnectionController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/test-db")
    public String testerConnexion(Model model) {
        try {
            String nomBase = jdbcTemplate.queryForObject("SELECT DATABASE();", String.class);
            Integer nbSatellites = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM SATELLITE;", Integer.class);

            model.addAttribute("status", "Succès");
            model.addAttribute("message", "Connexion réussie à la base de données : " + nomBase);
            model.addAttribute("details", "Nombre de satellites trouvés dans la table : " + nbSatellites);
        } catch (Exception e) {
            model.addAttribute("status", "Erreur");
            model.addAttribute("message", "Échec de la connexion à MySQL.");
            model.addAttribute("details", e.getMessage());
        }

        return "test-connection";
    }

}
