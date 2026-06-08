package fr.efrei.nanoorbit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static fr.efrei.nanoorbit.constant.SqlQueries.DELETE_SATELLITE;
import static fr.efrei.nanoorbit.constant.SqlQueries.INSERT_SATELLITE;

@Service
public class AdminService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void ajouterSatellite(String idSatellite, String nomSatellite, String format, LocalDate dateLancement) {
        jdbcTemplate.update(INSERT_SATELLITE, idSatellite, nomSatellite, format, dateLancement);
    }

    public void supprimerSatellite(String idSatellite) {
        jdbcTemplate.update(DELETE_SATELLITE, idSatellite);
    }
}