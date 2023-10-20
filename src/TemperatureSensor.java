import com.fazecast.jSerialComm.*;


public class TemperatureSensor {

    public static void main(String[] args) {
        // Définition du port série
        SerialPort serialPort = SerialPort.getCommPort("COM3"); // Remplacez par le port de votre Arduino
        serialPort.setBaudRate(9600);

        // Ouvrir le port série
        if (serialPort.openPort()) {
            System.out.println("Port série ouvert avec succès.");
        } else {
            System.err.println("Impossible d'ouvrir le port série.");
            return;
        }

        // Attente de l'initialisation du port série
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        

       // Lecture en continu de données depuis le port série
        try {
            while (true) {
                byte[] readBuffer = new byte[20];
                int numRead = serialPort.readBytes(readBuffer, readBuffer.length);

                if (numRead > 0) {
                    String data = new String(readBuffer, 0, numRead);
                    System.out.print("Données temperature : " + data);

                  
                    byte[] HumiditeData = new byte[20];
                    int HumiditeRead = serialPort.readBytes(HumiditeData, HumiditeData.length);

                    if (HumiditeRead > 0) {
                        String HumiditeValue = new String(HumiditeData, 0, HumiditeRead);
                        System.out.println("Humidite : " + HumiditeValue + "mv");
                    }
                }

                Thread.sleep(1000);
                System.out.println(); // Ajoutez une nouvelle ligne pour la prochaine série de lectures
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Fermeture du port série
            serialPort.closePort();
            System.out.println("Port série fermé.");
        }
    }
}
