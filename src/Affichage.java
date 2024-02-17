import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Stack;

public class Affichage {

    public static void afficherEnregistrements(Connection connection) {
        String query = "SELECT * FROM enregistrements";
        afficherTable(connection, query, "enregistrements");
    }

    public static void afficherCapteurs(Connection connection) {
        String query = "SELECT capteurs.id, capteurs.id_enregistrements, enregistrements.nomobjet, enregistrements.addressip, capteurs.typemesure, capteurs.valeur, capteurs.timestamp FROM capteurs JOIN enregistrements ON capteurs.id_enregistrements = enregistrements.id";
        afficherTable(connection, query, "capteurs");
    }

    public static void afficherActuateurs(Connection connection) {
        String query = "SELECT actuateurs.id, actuateurs.id_enregistrements, enregistrements.nomobjet, enregistrements.addressip, actuateurs.type_action, actuateurs.timestamp FROM actuateurs JOIN enregistrements ON actuateurs.id_enregistrements = enregistrements.id";
        afficherTable(connection, query, "actuateurs");
    }

    private static void afficherTable(Connection connection, String query, String tableName) {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                System.out.println("\nAffichage des données de la table '" + tableName + "':");
                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String nomObjet = resultSet.getString("nomobjet");
                    int addressIP = resultSet.getInt("addressip");
                    System.out.println("ID: " + id + ", Nom Objet: " + nomObjet + ", Adresse IP: " + addressIP);

                    if (tableName.equals("capteurs")) {
                        String typeMesure = resultSet.getString("typemesure");
                        int valeur = resultSet.getInt("valeur");
                        System.out.println("   Type de Mesure: " + typeMesure + ", Valeur: " + valeur);
                    }

                    if (tableName.equals("actuateurs")) {
                        String typeAction = resultSet.getString("type_action");
                        System.out.println("   Type d'Action: " + typeAction);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
     public void afficherDonneesPile(Stack<String> donnees) {
        System.out.println("\nAffichage des données de la pile :\n");

        if (donnees.isEmpty()) {
            System.out.println("La pile est vide.");
        } else {
            for (String donnee : donnees) {
                System.out.println(donnee);
            }
        }
    }
}
