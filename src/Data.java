import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Data {
    String[] input;
    String[] output;
    String addres;

    Data(String addres) {
        this.addres = addres;

    }


    ///file parse
    public void fileParse () {
        try (BufferedReader br = new BufferedReader(new FileReader(addres))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String everything = sb.toString();

            String[] strMas;
            String[] tmpInput;
            strMas = everything.split("\\s+");
            input = new String[strMas.length];
            output = new String[strMas.length];

            int count = 0;
            for (int i = 0; i < strMas.length; i += 4) {
                input[i] = strMas[i];
                input[i+1] = strMas[i+1];
                input[i+2] = strMas[i+2];
                output[count] = strMas[i + 3];
                count++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
