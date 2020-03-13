import java.util.*;
import java.io.*;

class Utils{
    public static List<List<Integer>> readData(String filePath){
        List<List<Integer>> ans = new ArrayList<>();
        try{
            File f = new File(filePath);
            BufferedReader buf = new BufferedReader(new FileReader(f));
            String line = "";
            while((line = buf.readLine()) != null){
                String[] words = line.split(",");
                List<Integer> temp = new ArrayList<>();
                for(String w : words){
                    temp.add(Integer.valueOf(w));
                }
                ans.add(temp);
            }
        } catch(Exception e){
            //TODO
        }
        return ans;
    }
}