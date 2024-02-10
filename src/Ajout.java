import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;


import java.util.Scanner;

public class Ajout {
        
    public  void Ajouterdonnee(Connection connection, Scanner scanner) {
        try {
            System.out.println("\nAjouter de nouveaux donnee:\n");
            
            System.out.print("\n Objet connectee associee ");
            System.out.print("Nom Objet: ");
            String nomObjet = scanner.nextLine();

            System.out.print("Adrress Ip : ");
            int Adrressip = scanner.nextInt();
            scanner.nextLine(); 
            // Insertion des données dans la table 'objetconnectee'
            String insC = "INSERT INTO Objetconnectes (nom_Objet, addressIp, timestamp) VALUES (?, ?, ?)";
try (PreparedStatement donneeC = connection.prepareStatement(insC)) {
    donneeC.setString(1, nomObjet);
    donneeC.setInt(2, Adrressip);
    donneeC.setTimestamp(3, new java.sql.Timestamp(new Date().getTime()));
    donneeC.executeUpdate();
    System.out.println("Nouvel utilisateur ajouté avec succès à la table 'enregistrements.'");
}


            // Insertion des données dans la table 'mesures' (c'est un exemple, vous pouvez ajuster selon vos besoins)
            System.out.print("type du capteur: ");
            String typeCapteur = scanner.nextLine();

            System.out.print("Valeur: ");
            String valeur = scanner.nextLine();

            String insM = "INSERT INTO Capteurs (id_Objetconnectes, type_capteur, valeur) VALUES (?, ?, ?)";
            try (PreparedStatement donneeM = connection.prepareStatement(insM)) {
                donneeM.setInt(1, dernierindex(connection)); // Récupérer le dernier ID inséré
                donneeM.setString(2, typeCapteur);
                donneeM.setString(3, valeur);
                donneeM.executeUpdate();
                System.out.println("Données insérées dans la table 'mesures'.");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
   
    public static int dernierindex(Connection connection) {
        try {
            // Récupérer le dernier ID inséré dans la table 'enregistrements'
            String query = "SELECT MAX(id) FROM Objetconnectes ";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Retourne -1 en cas d'erreur ou si aucune entrée n'est trouvée
    }
}
