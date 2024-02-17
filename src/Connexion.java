import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class Connexion {

    public Connection renvoi() {
        Connection connection = null;

        String dbUrl = "jdbc:postgresql://localhost:5432/temphum"; // chemin pour accéder à la base de données

        Properties properties = new Properties();
        properties.setProperty("user", "postgres");
        properties.setProperty("password", "Marikoben10");

        try {
            connection = DriverManager.getConnection(dbUrl, properties);
            System.out.println("Connecté à la base de données PostgreSQL");

            // Créer les tables nécessaires
            createTables(connection);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }

    private void createTables(Connection connection) {
        createEnregistrementsTable(connection);
        createCapteursTable(connection);
        createActuateursTable(connection);
       
    }

    private void createEnregistrementsTable(Connection connection) {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS enregistrements (id SERIAL PRIMARY KEY, nomobjet VARCHAR(255) NOT NULL, addressip INT)";
        executeCreateTable(connection, createTableSQL, "enregistrements");
    }

    private void createCapteursTable(Connection connection) {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS capteurs (" +
                "id SERIAL PRIMARY KEY, " +
                "id_enregistrements INTEGER REFERENCES enregistrements(id), " +
                "typemesure VARCHAR(255) NOT NULL, " + 
                "valeur INT NOT NULL, " +
                "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL)";
        executeCreateTable(connection, createTableSQL, "capteurs");
    }

    private void createActuateursTable(Connection connection) {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS actuateurs (" +
                "id SERIAL PRIMARY KEY, " +
                "id_enregistrements INTEGER REFERENCES enregistrements(id), " +
                "type_action VARCHAR(255) NOT NULL, " +
                "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL)";
        executeCreateTable(connection, createTableSQL, "actuateurs");
    }

   

    private void executeCreateTable(Connection connection, String createTableSQL, String tableName) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(createTableSQL)) {
            preparedStatement.executeUpdate();
            System.out.println("Table '" + tableName + "' créée avec succès.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
