package fr.efrei.nanoorbit.controller;

import fr.efrei.nanoorbit.model.User;
import fr.efrei.nanoorbit.service.AdminService;
import fr.efrei.nanoorbit.service.DashboardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/satellites")
    public String gererSatellites(HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("user");
        if (loggedInUser == null) {
            return "redirect:/login";
        }

        model.addAttribute("username", loggedInUser.getUsername());
        model.addAttribute("role", loggedInUser.getRoles());
        model.addAttribute("donnees", dashboardService.getDataForView("brute"));

        return "admin-satellites";
    }

    @PostMapping("/satellites/ajouter")
    public String ajouterSatellite(
            @RequestParam String idSatellite,
            @RequestParam String nomSatellite,
            @RequestParam String formatCubeSat,
            @RequestParam LocalDate dateLancement,
            HttpSession session) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        adminService.ajouterSatellite(idSatellite, nomSatellite, formatCubeSat, dateLancement);
        return "redirect:/admin/satellites";
    }

    @PostMapping("/satellites/supprimer")
    public String supprimerSatellite(@RequestParam String idSatellite, HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        adminService.supprimerSatellite(idSatellite);
        return "redirect:/admin/satellites";
    }
}