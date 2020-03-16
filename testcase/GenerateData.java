import java.util.*;
import java.io.*;

class GenerateData{
    private static Random rand = new Random();

    private static int randomInteger(int s, int e){
        return rand.nextInt(e - s + 1) + s;
    }

    private static String combine(int a, int b, int c){
        return String.valueOf(a) + "," 
            + String.valueOf(b) + ","
            + String.valueOf(c) + "\n";
    }

    public static void main(String[] args){
        int[] vertexNum = {10, 100, 1000, 2000, 3000, 4000};
        for(int i = 0; i < vertexNum.length; i ++){
            System.out.printf("generating LargeTestData%d\n", i);
            int N = vertexNum[i];
            int start = 0, end = N - 1;
            try{
                File file = new File("LargeTestData" + String.valueOf(i) + ".txt");
                BufferedWriter output = new BufferedWriter(new FileWriter(file));
                // write down N, start, end
                output.write(combine(N, start, end));
                for(int k1 = 0; k1 < N; k1 ++){
                    for(int k2 = k1 + 1; k2 < N; k2 ++){
                        output.write(combine(k1, k2, randomInteger(0, N)));
                    }
                }
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}