import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Equipements {
    private int id;
    private String nomobjet;
    private int addressip;

    public Equipements(int id, String nomobjet, int addressip) {
        this.id = id;
        this.nomobjet = nomobjet;
        this.addressip = addressip;
    }

    public static final String TABLE_NAME = "Equipements";

    public void createTable(Connection connection) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (id SERIAL PRIMARY KEY, nomobjet VARCHAR(255) NOT NULL, addressip INT)";
        executeCreateTable(connection, createTableSQL, TABLE_NAME);
    }

    protected void executeCreateTable(Connection connection, String createTableSQL, String tableName) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(createTableSQL)) {
            preparedStatement.executeUpdate();
            System.out.println("Table '" + tableName + "' créée avec succès.");
        }
    }

    public int getId() {
        return id;
    }

    public String getNomobjet() {
        return nomobjet;
    }

    public int getAddressip() {
        return addressip;
    }
}

