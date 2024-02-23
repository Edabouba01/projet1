import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class Capteurs extends Equipements {
    protected String typeMesure;
    protected int valeur;

    public Capteurs(int id, String nomobjet, int addressip, String typeMesure, int valeur) {
        super(id, nomobjet, addressip);
        this.typeMesure = typeMesure;
        this.valeur = valeur;
    }

    public void ajouter(Connection connection) throws SQLException {
        // Insertion des données dans la table 'capteurs'
        String insC = "INSERT INTO capteurs (id_Equipements, typemesure, valeur, timestamp) VALUES (?, ?, ?, ?)";
        try (PreparedStatement donneeC = connection.prepareStatement(insC)) {
            donneeC.setInt(1, getId());  // ID de la table 'Equipements'
            donneeC.setString(2, typeMesure);
            donneeC.setInt(3, valeur);
            donneeC.setTimestamp(4, new java.sql.Timestamp(System.currentTimeMillis()));
            donneeC.executeUpdate();
            System.out.println("Nouvel objet ajouté avec succès à la table 'capteurs.'");
        }
    }
}


