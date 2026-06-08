package fr.efrei.nanoorbit.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SqlQueries {

    public static final String TABLE_SATELLITES =
            "SELECT id_satellite, nom_satellite, format_cube_sat, date_lancement FROM Satellite";

    public static final String VUE_ALERTES_INSTRUMENTS =
            "SELECT * FROM vue_alertes_instruments";

    public static final String VUE_BILAN_COMMUNICATIONS =
            "SELECT * FROM vue_bilan_communications";

    public static final String VUE_TABLEAU_DE_BORD_MISSIONS =
            "SELECT * FROM vue_tableau_de_bord_missions";

    public static final String VUE_SATELLITES_OPERATIONNELS =
            "SELECT * FROM vue_satellites_operationnels";

    public static final String VUE_HISTORIQUE =
            "SELECT f.date_heure_debut, f.duree, s.nom_satellite, s2.nom_station, s3.nom_statut \n" +
                    "FROM fenetrecommunication f\n" +
                    "INNER JOIN satellite s ON s.id_satellite = f.id_satellite  \n" +
                    "INNER JOIN station s2 ON s2.id_station = f.id_station\n" +
                    "INNER JOIN statut s3 ON s3.id_statut = f.id_statut AND s3.nom_statut = 'Realisee' AND s3.type_statut = 'FenetreCommunication'\n" +
                    "ORDER BY f.date_heure_debut desc ;";

    public static final String SELECT_SATELLITES_ADMIN =
            "SELECT s.id_satellite, s.nom_satellite, s.format_cube_sat, s.date_lancement, st.nom_statut FROM Satellite s JOIN Statut st ON s.id_statut_operationnel = st.id_statut";

    public static final String SELECT_STATUTS_SATELLITE =
            "SELECT id_statut, nom_statut FROM Statut WHERE type_statut = 'Satellite'";

    public static final String UPDATE_SATELLITE_STATUT_BY_ID =
            "UPDATE Satellite SET id_statut_operationnel = ? WHERE id_satellite = ?";

    public static final String SELECT_SATELLITES_OPERATIONNELS =
            "SELECT s.id_satellite, s.nom_satellite FROM Satellite s JOIN Statut st ON s.id_statut_operationnel = st.id_statut WHERE st.nom_statut = 'Opérationnel'";

    public static final String SELECT_STATIONS_ACTIVES =
            "SELECT id_station, nom_station FROM Station";

    public static final String INSERT_FENETRE_COM =
            "INSERT INTO FenetreCommunication (id_satellite, id_station, date_heure_debut, duree, elevation_max, id_statut) VALUES (?, ?, ?, ?, ?, (SELECT id_statut FROM Statut WHERE type_statut = 'FenetreCommunication' AND nom_statut LIKE 'Planifi%' LIMIT 1))";

    public static final String SELECT_MISSIONS_ACTIVES =
            "SELECT m.id_mission, m.nom_mission, st.nom_statut FROM Mission m JOIN Statut st ON m.id_statut = st.id_statut";

    public static final String CHECK_PARTICIPATION =
            "SELECT COUNT(*) FROM Participe WHERE id_satellite = ? AND id_mission = ?";

    public static final String INSERT_PARTICIPATION =
            "INSERT INTO Participe (id_satellite, id_mission, role_satellite) VALUES (?, ?, ?)";

    public static final String SELECT_STATUT_MISSION =
            "SELECT st.nom_statut FROM Mission m JOIN Statut st ON m.id_statut = st.id_statut WHERE m.id_mission = ?";

    public static final String SELECT_ID_STATUT_DESORBITE =
            "SELECT id_statut FROM Statut WHERE type_statut = 'Satellite' AND nom_statut LIKE 'Désorbit%' LIMIT 1";
}