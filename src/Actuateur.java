import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class Actuateur extends Equipements {
    private String typeAction;

    public Actuateur(int id, String nomobjet, int addressip, String typeAction) {
        super(id, nomobjet, addressip);
        this.typeAction = typeAction;
       
    }

    public void ajouter(Connection connection) throws SQLException {
        String insAct = "INSERT INTO actuateurs (id_Equipements,type_action, timestamp) VALUES (?, ?, ?)";
        try (PreparedStatement donneeact = connection.prepareStatement(insAct)) {
            donneeact.setInt(1, getId());
            donneeact.setString(2, typeAction);
            donneeact.setTimestamp(3, new java.sql.Timestamp(new Date().getTime()));
            donneeact.executeUpdate();
            System.out.println("Nouvel objet ajouté avec succès à la table 'Actuateurs.'");
        }
    }
}






   
   
