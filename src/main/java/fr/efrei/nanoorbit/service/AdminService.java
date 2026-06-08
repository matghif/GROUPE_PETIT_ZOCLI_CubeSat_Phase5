package fr.efrei.nanoorbit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static fr.efrei.nanoorbit.constant.SqlQueries.*;

@Service
public class AdminService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> getSatellitesPourAdmin() {
        return jdbcTemplate.queryForList(SELECT_SATELLITES_ADMIN);
    }

    public List<Map<String, Object>> getStatutsSatellite() {
        return jdbcTemplate.queryForList(SELECT_STATUTS_SATELLITE);
    }

    public void modifierStatutSatellite(String idSatellite, int idStatut) {
        jdbcTemplate.update(UPDATE_SATELLITE_STATUT_BY_ID, idStatut, idSatellite);
    }

    public List<Map<String, Object>> getSatellitesOperationnels() {
        return jdbcTemplate.queryForList(SELECT_SATELLITES_OPERATIONNELS);
    }

    public List<Map<String, Object>> getStationsActives() {
        return jdbcTemplate.queryForList(SELECT_STATIONS_ACTIVES);
    }

    public void planifierFenetreCommunication(String idSatellite, String idStation, String dateDebut, int duree, double elevation) {
        jdbcTemplate.update(INSERT_FENETRE_COM, idSatellite, idStation, dateDebut, duree, elevation);
    }

    public List<Map<String, Object>> getMissionsActives() {
        return jdbcTemplate.queryForList(SELECT_MISSIONS_ACTIVES);
    }

    public boolean verifierParticipationExiste(String idSatellite, String idMission) {
        Integer count = jdbcTemplate.queryForObject(CHECK_PARTICIPATION, Integer.class, idSatellite, idMission);
        return count != null && count > 0;
    }

    public String getStatutMission(String idMission) {
        try {
            return jdbcTemplate.queryForObject(SELECT_STATUT_MISSION, String.class, idMission);
        } catch (Exception e) {
            return "Inconnu";
        }
    }

    public void ajouterParticipation(String idSatellite, String idMission, String role) {
        jdbcTemplate.update(INSERT_PARTICIPATION, idSatellite, idMission, role);
    }

    public int desorbiterSatelliteProc(String idSatellite) {
        try {
            Integer idStatutDesorbite = jdbcTemplate.queryForObject(SELECT_ID_STATUT_DESORBITE, Integer.class);
            if (idStatutDesorbite != null) {
                jdbcTemplate.update(UPDATE_SATELLITE_STATUT_BY_ID, idStatutDesorbite, idSatellite);
            }
        } catch (Exception ex) {
            return -1;
        }
        return -1;
    }
}