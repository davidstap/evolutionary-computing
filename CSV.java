import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class CSV
{
  // for testing purposes
  public static void main(String[] args)
  {
    double[][] data = {{1,8,9.3,6.2},{1,9,6,2},{2,9.9,2,1},{2,9.9,2,1}};
    
    CSV csv = new CSV();
    
    csv.ArrayToCSV(data);
    
  }
  
  public void ArrayToCSV(double[][] data)
  {
      try 
      {
        String path="stats.csv";
        File file = new File(path);
        
        if (!file.exists()) { file.createNewFile(); }
        
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
                
        for (int i=0; i<data.length; i++)
        {
          for (int j=0; j<data[i].length; j++)
          {
            bw.write(String.valueOf(data[i][j]));
            if (j != data[i].length-1)
            {
              bw.write(',');
            }
          }
          bw.write('\n');
        }        
        // Close connection
        bw.close();
      }
      catch(Exception e) { System.out.println(e); }
  }
}