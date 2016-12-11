import java.io.*;
import java.util.*;

import java.net.*;


/**
 * Write a description of class Main here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Main
{
    private static String[] codes = new String[]{"P1230", "P1231", "P1232", "P1233", "P1234", "P1235", "P1236", "P1237", "P1238", "P1239", 
                                    "P1270", "B1426", "B1427", "B1428", "B1429", "B1430", "C1095", "C1096", "C1097", "C1098", "C1099",
                                    "C1100", "C1101", "C1102"};
    private static int vid;
    private static String rpmCode;
    private static String fuelCode;
    private static String seatCode;
    private static String absCode;
    private static final String RPMCODE = "P1270";
    public static void main(String [] args){        
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter vehicle ID: ");
        vid = scanner.nextInt();
        
        
        File f = new File("/home/pi/Desktop/OBDII Software Emulator");
        File[] matchingFiles = f.listFiles(new FilenameFilter(){
            public boolean accept(File dir, String name){
                return name.startsWith("OBDII20") && name.endsWith("csv");
            }

        });
        for(File file : matchingFiles){
            System.out.println(file.toString());
            filterCodes(file);
        }
    }
    
    private static void filterCodes(File f){
        try (BufferedReader br = new BufferedReader(new FileReader(f))){
            String line;
            while ((line = br.readLine()) != null){
                Random r = new Random();
                int rnum = r.nextInt((6500-1000) + 1) + 1000;
                rpmCode = Integer.toString(rnum);
                String[] string = line.split(" ");
                    for (String s : string){
                        for(String code : codes){
                            if (s.equals(code)){
                                if (s.equals(RPMCODE)){
                                    rpmCode = RPMCODE;
                                    break;
                                }
                                if (s.startsWith("P")){
                                    fuelCode =s ;
                                }
                                else if (s.startsWith("B")){
                                    seatCode =s;
                                }
                                else if (s.startsWith("C")){
                                    absCode =s;
                                }

                            }
                        }
                    }
                }
            upload2DB();
        }catch(FileNotFoundException e){
            System.out.println("File could not be found");
        }
        catch(IOException e){
            System.out.println("IOexception");
        }
    }
   
    private static void upload2DB(){
        try{
            String srl = "http://68.147.216.78/webserver/updateCodes.php";
            URL url = new URL(srl);
            URLConnection con = url.openConnection();
            HttpURLConnection http = (HttpURLConnection)con;
            http.setRequestMethod("POST");
            http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            http.setRequestProperty("charset", "UTF-8");
            String urlParams = "vid="+vid+"&rpm="+rpmCode+"&fc="+fuelCode+"&sc="+seatCode+"&abc="+absCode;
            System.out.println(urlParams);
            http.setRequestProperty("Content-Length", Integer.toString(urlParams.getBytes().length));
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                    wr.writeBytes(urlParams);
            wr.flush();
            wr.close();
            
    
    
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + urlParams);
    
        BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
    
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
    
            //print result
            System.out.println(response.toString());
        }catch(MalformedURLException e){
        }catch(IOException e){}
   }
}


