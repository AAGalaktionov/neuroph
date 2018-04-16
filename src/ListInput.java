import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ListInput {
    ArrayList<Double[]> inputList = new ArrayList<>();
    ArrayList<Double[]> outputList = new ArrayList<>();
    String addres;

    ListInput(String addres) {
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
            strMas = everything.split("\\s");


            int count = 0;
            for (int i = 0; i < strMas.length; i += 4) {
                Double[] tmpList = new Double[3];
                Double[] tmpListOut = new Double[1];
                tmpList[0] = (Double.parseDouble(strMas[i]));
                tmpList[1] = (Double.parseDouble(strMas[i+1]));
                tmpList[2] = (Double.parseDouble(strMas[i+2])+40);
                tmpListOut[0] = (Double.parseDouble(strMas[i + 3])) ;
                inputList.add(count, tmpList);
                outputList.add(count, tmpListOut);
                count++;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
