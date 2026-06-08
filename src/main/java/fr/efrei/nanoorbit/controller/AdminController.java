package fr.efrei.nanoorbit.controller;

import fr.efrei.nanoorbit.model.User;
import fr.efrei.nanoorbit.service.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.Arrays;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    private boolean checkAccess(HttpSession session, String... authorizedRoles) {
        User user = (User) session.getAttribute("user");
        if (user == null) return false;
        return Arrays.asList(authorizedRoles).contains(user.getRoles());
    }

    private void populateBaseData(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("username", user.getUsername());
            model.addAttribute("role", user.getRoles());
        }
        model.addAttribute("satellites", new ArrayList<>());
        model.addAttribute("statutsAvailable", new ArrayList<>());
        model.addAttribute("satellitesOp", new ArrayList<>());
        model.addAttribute("stations", new ArrayList<>());
        model.addAttribute("missions", new ArrayList<>());
        model.addAttribute("currentTab", null);
    }

    @GetMapping
    public String adminHome(HttpSession session, Model model) {
        if (!checkAccess(session, "operateur_sat", "admin_nano", "resp_mission")) {
            return "redirect:/login";
        }
        populateBaseData(session, model);
        return "admin-home";
    }

    @GetMapping("/statut")
    public String vueStatut(HttpSession session, Model model) {
        if (!checkAccess(session, "operateur_sat", "admin_nano")) {
            return "redirect:/admin";
        }
        populateBaseData(session, model);
        model.addAttribute("satellites", adminService.getSatellitesPourAdmin());
        model.addAttribute("statutsAvailable", adminService.getStatutsSatellite());
        model.addAttribute("currentTab", "statut");
        return "admin-home";
    }

    @PostMapping("/statut/modifier")
    public String modifierStatut(@RequestParam String idSatellite, @RequestParam int idStatut, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!checkAccess(session, "operateur_sat", "admin_nano")) {
            return "redirect:/login";
        }
        adminService.modifierStatutSatellite(idSatellite, idStatut);
        redirectAttributes.addFlashAttribute("successMessage", "Le statut du satellite a été mis à jour avec succès.");
        return "redirect:/admin/statut";
    }

    @GetMapping("/fenetre")
    public String vueFenetre(HttpSession session, Model model) {
        if (!checkAccess(session, "operateur_sat", "admin_nano")) {
            return "redirect:/admin";
        }
        populateBaseData(session, model);
        model.addAttribute("satellitesOp", adminService.getSatellitesOperationnels());
        model.addAttribute("stations", adminService.getStationsActives());
        model.addAttribute("currentTab", "fenetre");
        return "admin-home";
    }

    @PostMapping("/fenetre/planifier")
    public String planifierFenetre(@RequestParam String idSatellite, @RequestParam String idStation,
                                   @RequestParam String dateDebut, @RequestParam int duree,
                                   @RequestParam double elevation, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!checkAccess(session, "operateur_sat", "admin_nano")) {
            return "redirect:/login";
        }
        if (duree < 1 || duree > 900) {
            redirectAttributes.addFlashAttribute("errorMessage", "La durée doit être comprise entre 1 et 900 secondes.");
            return "redirect:/admin/fenetre";
        }
        try {
            adminService.planifierFenetreCommunication(idSatellite, idStation, dateDebut, duree, elevation);
            redirectAttributes.addFlashAttribute("successMessage", "La fenêtre de communication a été planifiée.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de l'insertion : " + e.getMessage());
        }
        return "redirect:/admin/fenetre";
    }

    @GetMapping("/mission")
    public String vueMission(HttpSession session, Model model) {
        if (!checkAccess(session, "resp_mission", "admin_nano")) {
            return "redirect:/admin";
        }
        populateBaseData(session, model);
        model.addAttribute("satellitesOp", adminService.getSatellitesOperationnels());
        model.addAttribute("missions", adminService.getMissionsActives());
        model.addAttribute("currentTab", "mission");
        return "admin-home";
    }

    @PostMapping("/mission/assigner")
    public String assignerMission(@RequestParam String idSatellite, @RequestParam String idMission,
                                  @RequestParam String roleSatellite, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!checkAccess(session, "resp_mission", "admin_nano")) {
            return "redirect:/login";
        }
        String statutMission = adminService.getStatutMission(idMission);
        if ("Terminée".equalsIgnoreCase(statutMission) || "Termine".equalsIgnoreCase(statutMission)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Impossible d'assigner un satellite : la mission est terminée.");
            return "redirect:/admin/mission";
        }
        if (adminService.verifierParticipationExiste(idSatellite, idMission)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ce satellite participe déjà à cette mission.");
            return "redirect:/admin/mission";
        }
        adminService.ajouterParticipation(idSatellite, idMission, roleSatellite);
        redirectAttributes.addFlashAttribute("successMessage", "Le satellite a été assigné à la mission.");
        return "redirect:/admin/mission";
    }

    @PostMapping("/satellites/desorbiter")
    public String desorbiterSatellite(@RequestParam String idSatellite, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!checkAccess(session, "admin_nano")) {
            return "redirect:/login";
        }
        int result = adminService.desorbiterSatelliteProc(idSatellite);
        if (result >= 0) {
            redirectAttributes.addFlashAttribute("successMessage", "Satellite désorbité via procédure stockée. Fenêtres annulées : " + result);
        } else {
            redirectAttributes.addFlashAttribute("successMessage", "Satellite désorbité avec succès.");
        }
        return "redirect:/admin/statut";
    }
}