package fr.efrei.nanoorbit.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static fr.efrei.nanoorbit.constant.SqlQueries.*;

@Service
public class DashboardService {


    @Autowired
    JdbcTemplate jdbcTemplate;

    public String getTitleForView(String vueName) {
        switch (vueName) {
            case "alertes":
                return "Alertes Instruments";
            case "communications":
                return "Bilan des communications par satellite";
            case "missions":
                return "Tableau de bord des missions";
            case "operationnels":
                return "Tableau de bord des satellites opérationnels";
            case "historique":
                return "Historique des fenêtres de communication";
            default:
                return "Liste des satellites";

        }
    }

    public List<Map<String, Object>> getDataForView(String vueName) {
        String sql;
        switch (vueName) {
            case "brute":
                sql = TABLE_SATELLITES;
                break;
            case "alertes":
                sql = VUE_ALERTES_INSTRUMENTS;
                break;
            case "communications":
                sql = VUE_BILAN_COMMUNICATIONS;
                break;
            case "missions":
                sql = VUE_TABLEAU_DE_BORD_MISSIONS;
                break;
            case "operationnels":
                sql = VUE_SATELLITES_OPERATIONNELS;
                break;
            case "historique":
                sql = VUE_HISTORIQUE;
                break;
            default:
                return new ArrayList<>();
        }
        return jdbcTemplate.queryForList(sql);
    }

    public long calculateCriticalCount(List<Map<String, Object>> results) {
        long criticalCount = 0;
        for (Map<String, Object> line : results) {
            for (Map.Entry<String, Object> entries : line.entrySet()) {
                if (entries.getKey().toLowerCase().contains("priorit")) {
                    if (entries.getValue() != null && "CRITIQUE".equalsIgnoreCase(entries.getValue().toString().trim())) {
                        criticalCount++;
                    }
                }
            }
        }
        return criticalCount;
    }
}
