import java.io.File;

import java.io.BufferedWriter;
import java.io.FileWriter;

import java.io.BufferedReader;
import java.io.StringReader;

import java.io.InputStreamReader;

import java.io.IOException;
import java.io.FileNotFoundException;

import java.util.HashMap;

class InOut
{
    public static void save(HashMap<String, Double> map, String fpath)
            throws IOException
    {
        File file = new File(fpath);
        if (file.exists())
        {
            file.delete();
        }
        file.createNewFile();
        
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
        for (String key : map.keySet())
        {
            writer.write(key + " " + Double.toString(map.get(key)) + "\n");
        }
        writer.flush();
        writer.close();
    }
    
    public static HashMap<String, Double> load(String data)
            throws IOException
    {
        HashMap<String, Double> map = new HashMap<String, Double>();
        BufferedReader reader = new BufferedReader(new StringReader(data));

        String line, key;
        double value;
        int splitAt;
        while ((line = reader.readLine()) != null)
        {
            splitAt = line.lastIndexOf(" ");
            key = line.substring(0, splitAt);
            value = Double.parseDouble(
                    line.substring(splitAt + 1, line.length()));
            map.put(key, value);
        }
        
        return map;
    }
    
    // https://stackoverflow.com/questions/26830617/
    public static String command(String cmd)
            throws IOException, InterruptedException
    {
        Process p;
        p = Runtime.getRuntime().exec(cmd);
        p.waitFor();
        
        BufferedReader reader =
                new BufferedReader(
                        new InputStreamReader(p.getInputStream()));
        StringBuffer output = new StringBuffer();
        
        String line = "";
        while ((line = reader.readLine()) != null) {
            output.append(line + "\n");
        }
        return output.toString();
    }
    
}

