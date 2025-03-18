package com.lgs;

import java.io.*;


import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
//import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

//import java.io.File;
//import java.io.FileNotFoundException;
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.Map;
//import java.util.List;
import java.util.Arrays;
//import java.util.ArrayList;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

//@WebServlet("/FileUploadServlet")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
    maxFileSize = 1024 * 1024 * 10,      // 10 MB
    maxRequestSize = 1024 * 1024 * 15)   // 15 MB
public class FileUploadServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
  private static HashMap<String, List<String>> userCount= new HashMap<String, List<String>>();
  private static HashMap<String, List<String>> licenseCount= new HashMap<String, List<String>>();
  private static TreeMap<String, HashMap<String, Integer>> loginTimeCount = new TreeMap<String, HashMap<String, Integer>>();
  private static List<List<String>> rawDataArray = new ArrayList<>();

    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Get the file part from the request
        Part filePart = request.getPart("file");
	      userCount.clear();
	      licenseCount.clear();
	      rawDataArray.clear();
	      loginTimeCount.clear();
        
        // Process the file and read its contents line by line
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(filePart.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
               //perform function to store data
               Pattern pattern = Pattern.compile("logged in with", Pattern.CASE_INSENSITIVE);
               Matcher matcher = pattern.matcher(line);
               boolean matchFound = matcher.find();
               if(matchFound) {
                  buildHashMap(line);
               }
//                lines.add(line);
            }
        }
        
        String fileName = getSubmittedFileName(filePart);

        List<List<String>> userCountArray = convertHashMapToArray(userCount, null);
        List<List<String>> licenseCountArray = convertHashMapToArray(licenseCount, null);
        // Store the lines in the request attribute
//        request.setAttribute("lines", lines);
//        request.setAttribute("fileName", getSubmittedFileName(filePart));
        
        // Set the data as an attribute in the request
        
        
        // Create Excel workbook
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Data Sheet");
        
        // Populate Excel sheet with data
        int rowNum = 0;
        for (List<String> rowData : userCountArray) {
            Row row = sheet.createRow(rowNum++);
            int cellNum = 0;
            for (String cellData : rowData) {
                Cell cell = row.createCell(cellNum++);
                cell.setCellValue(cellData);
            }
        }
        
        // Auto-size columns for better readability
        for (int i = 0; i < userCountArray.get(0).size(); i++) {
            sheet.autoSizeColumn(i);
        }
        
        // Store the workbook in session for download
        
        

        
        // Forward to the result page
//        request.getRequestDispatcher("/result.jsp").forward(request, response);
        HttpSession session = request.getSession();
        session.setAttribute("excelWorkbook", workbook);
        session.setAttribute("fileName", fileName);
        session.setAttribute("dataList", userCountArray);
        session.setAttribute("licenseCount", licenseCountArray);
        response.sendRedirect("result.jsp");
    }
    
    // Helper method to extract file name from the Part
    private String getSubmittedFileName(Part part) {
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return "unknown";
    }
    
    private static void buildHashMap(String data) {
      // if data varible holds logout data, then add to the rawDataArray
      Pattern loginPatternRaw = Pattern.compile("logged in with", Pattern.CASE_INSENSITIVE);
      Pattern logoutPatternRaw = Pattern.compile("logged out", Pattern.CASE_INSENSITIVE);

      Matcher loginMatcherRaw = loginPatternRaw.matcher(data);
      Matcher logoutMatcherRaw = logoutPatternRaw.matcher(data);

      boolean loginMatchFound = loginMatcherRaw.find();
      boolean logoutMatchFound = logoutMatcherRaw.find();

      // declared and initialized outside both conditionals as it is used by both
      String rawDataString = data;

      if(logoutMatchFound) {
          rawDataString = rawDataString.replaceAll(",.+-\\s", " ");
          rawDataArray.add(Arrays.asList(rawDataString.split(" ")));
      }

      else if (loginMatchFound) {

          String loginTimeString = data;
          String userNameData = data;
          String userName = userNameData.replaceAll("^([^']+)'","");
          userName = userName.replaceAll("'.+","");
          String licenseName = data.replaceAll(".*logged in with ", "");

          //extract login time
          String loginTimeRegex = "(?<=\\s)[0-9]{2}";
          Pattern loginPattern = Pattern.compile(loginTimeRegex);
          Matcher loginMatcher = loginPattern.matcher(loginTimeString);

          if (loginMatcher.find()) {
              // System.out.println(loginTimeString);
              Integer loginHour = Integer.parseInt(loginMatcher.group());
              if (0 <= loginHour && loginHour < 1) {
                  addToLoginTimeCount("00-01", licenseName);
              }
              else if (1 <= loginHour && loginHour < 2) {
                  addToLoginTimeCount("01-02", licenseName);
              }
              else if (2 <= loginHour && loginHour < 3) {
                  addToLoginTimeCount("02-03", licenseName);
              }
              else if (3 <= loginHour && loginHour < 4) {
                  addToLoginTimeCount("03-04", licenseName);
              }
              else if (4 <= loginHour && loginHour < 5) {
                  addToLoginTimeCount("04-05", licenseName);
              }
              else if (5 <= loginHour && loginHour < 6) {
                  addToLoginTimeCount("05-06", licenseName);
              }
              else if (6 <= loginHour && loginHour < 7) {
                  addToLoginTimeCount("06-07", licenseName);
              }
              else if (7 <= loginHour && loginHour < 8) {
                  addToLoginTimeCount("07-08", licenseName);
              }
              else if (8 <= loginHour && loginHour < 9) {
                  addToLoginTimeCount("08-09", licenseName);
              }
              else if (9 <= loginHour && loginHour < 10) {
                  addToLoginTimeCount("09-10", licenseName);
              }
              else if (10 <= loginHour && loginHour < 11) {
                  addToLoginTimeCount("10-11", licenseName);
              }
              else if (11 <= loginHour && loginHour < 12) {
                  addToLoginTimeCount("11-12", licenseName);
              }
              else if (12 <= loginHour && loginHour < 13) {
                  addToLoginTimeCount("12-13", licenseName);
              }
              else if (13 <= loginHour && loginHour < 14) {
                  addToLoginTimeCount("13-14", licenseName);
              }
              else if (14 <= loginHour && loginHour < 15) {
                  addToLoginTimeCount("14-15", licenseName);
              }
              else if (15 <= loginHour && loginHour < 16) {
                  addToLoginTimeCount("15-16", licenseName);
              }
              else if (16 <= loginHour && loginHour < 17) {
                  addToLoginTimeCount("16-17", licenseName);
              }
              else if (17 <= loginHour && loginHour < 18) {
                  addToLoginTimeCount("17-18", licenseName);
              }
              else if (18 <= loginHour && loginHour < 19) {
                  addToLoginTimeCount("18-19", licenseName);
              }
              else if (18 <= loginHour && loginHour < 19) {
                  addToLoginTimeCount("19-20", licenseName);
              }
              else if (20 <= loginHour && loginHour < 21) {
                  addToLoginTimeCount("20-21", licenseName);
              }
              else if (21 <= loginHour && loginHour < 22) {
                  addToLoginTimeCount("21-22", licenseName);
              }
              else if (22 <= loginHour && loginHour < 23) {
                  addToLoginTimeCount("22-23", licenseName);
              }
              else {
                  addToLoginTimeCount("23-24", licenseName);
              }
          }

          //raw data
          rawDataString = rawDataString.replaceAll(",(?<=,).+-\\s", " ");
          rawDataArray.add(Arrays.asList(rawDataString.split(" ")));

          // count users 
          if (userCount.containsKey(userName)){
              int userIncrement = Integer.parseInt(userCount.get(userName).get(1)) + 1;
              userCount.put(userName, Arrays.asList(licenseName, Integer.toString(userIncrement)));
          }
          else {
              userCount.put(userName, Arrays.asList(licenseName, "1"));
          } 
          
          // count licenses
          if (licenseCount.containsKey(licenseName)){
              int licenseIncrement = Integer.parseInt(licenseCount.get(licenseName).get(0)) + 1;
              licenseCount.put(licenseName, Arrays.asList(Integer.toString(licenseIncrement)));
          }
          else {
              licenseCount.put(licenseName, Arrays.asList("1"));
          } 
      }

  }
    
  /**
  * Takes the hour bounds and adds them to {@code loginTimeCount}.
  * @param hourBounds The hour (upper and lower limit) in which user logged in.
  * @param licenseName The license user logged in with.
  * @return void This method does not return any value.
  */
  // Helper function to add to the loginTimeCount hashmap
  private static void addToLoginTimeCount(String hourBounds, String licenseName) {
      if (loginTimeCount.containsKey(hourBounds)){
          if (loginTimeCount.get(hourBounds).containsKey(licenseName)){
              int licenseIncrement = loginTimeCount.get(hourBounds).get(licenseName) + 1;
              loginTimeCount.get(hourBounds).put(licenseName, licenseIncrement);
          }
          else {
              loginTimeCount.get(hourBounds).put(licenseName, 1);
          } 
      }
      else {
          loginTimeCount.put(hourBounds, new HashMap<String, Integer>());
          addToLoginTimeCount(hourBounds, licenseName);
      } 
  }
  
  
  //not used
	/**
	* Takes a hashmap and returns it into an array list.
	* @param map The hashmap to convert to array list.
	* @param loginMap The hashmap to convert login time data to array list.
	* @return The array list obtained.
	*/
	public static List<List<String>> convertHashMapToArray(Map<String, List<String>> map, TreeMap<String, HashMap<String, Integer>> loginMap) {
	    List<List<String>> countArr = new ArrayList<>();
	    if (loginMap == null) {
	        if (map.size() == 0) {
	            return countArr;
	        }
	        int id = 1;
	        boolean userMap = true;
	        List<String> firstElement = map.values().iterator().next();
	        if (firstElement.size() == 1) userMap = false;
	
	        for (String key : map.keySet()) {
	            List<String> arr = new ArrayList<>();
	            if (userMap) arr.add(Integer.toString(id));
	            arr.add(key);
	            for (String value : map.get(key)){
	                arr.add(value);
	            }
	            countArr.add(arr);
	            id++;
	        }
	    }
	    else {
	        if (loginMap.size() == 0) {
	            return countArr;
	        }
	        for(String k: loginMap.keySet()) {
	            for(String ik: loginMap.get(k).keySet()) {
	                ArrayList<String> row = new ArrayList<>();
	                row.add(k);
	                row.add(ik);
	                row.add(String.valueOf(loginMap.get(k).get(ik)));
	                countArr.add(row);
	            }
	        }
	    }
	    return countArr;
	}
}


//-----------------------------------------------------
//
//package com.mycompany.app;
//

//
//
//
//public class Count {
//    // variables to store user and license information
//    
//    /**
//    * Takes start and end date and returns {@code userCount} data as array list to populate the table.
//    * @param startDate The starting date for license usage calculation.
//    * @param endDate The ending date for license usage calculation.
//    * @param selectedPath The directory path of log files.
//    * @param selectedFiles The directory path of log files.
//    * @return The array list of user license usage data.
//    */
//    public static List<List<String>> createTableData(LocalDate startDate, LocalDate endDate, String selectedPath, File[] selectedFiles) {
//        
//        // empty all maps each time function is called
//        userCount.clear();
//        licenseCount.clear();
//        rawDataArray.clear();
//        loginTimeCount.clear();
//
//        if (selectedFiles == null)
//            getData(startDate, endDate, selectedPath);
//        else getData(selectedFiles);
//        return convertHashMapToArray(userCount, null);
//    }
//
//    /**
//     * Takes no parameters and returns {@code licenseCount} to populate the bar chart.
//     * @return The data used in bar chart.
//     */
//    public static String[][] createGraphData(){
//        return conveArrayListToStringArray(convertHashMapToArray(licenseCount, null));
//    }
//
//    public static String[][] createLoginTimeData(){
//        return conveArrayListToStringArray(convertHashMapToArray(null, loginTimeCount));
//    }
//
//    /**
//    * Takes start and end date and checks which license files fall in between
//    * @param startDate The starting date for license usage calculation.
//    * @param endDate The ending date for license usage calculation.
//    * @param selectedPath The directory path of log files.
//    * @return void This method does not return any value.
//    */
//    private static void getData(LocalDate starDate, LocalDate endDate, String selectedPath){
//        try {
//            // File dir = new File("D:\\Polarion\\data\\logs\\main");
//            // File dir = new File("/Users/mujtabaali/Downloads/lgs/logs");
//            // File dir = new File("C:\\Users\\Mohama61\\Downloads\\LicenseUserCount");
//            File dir = new File(selectedPath);
//
//            File[] directoryListing = dir.listFiles();
//            if (directoryListing != null) {
//                DateTimeFormatter parser = DateTimeFormatter.ofPattern("yyyyMMdd");
//                for (File myObj : directoryListing) {
//                    // look for the date in filename and match with dates available
//
//                    String fileName = myObj.getName();
//                    Pattern filePattern = Pattern.compile("^log4j-licensing");
//                    Matcher fileMatcher = filePattern.matcher(fileName);
//                    boolean fileMatchFound = fileMatcher.find();
//
//                    if (fileMatchFound) {
//                        Pattern datePattern = Pattern.compile("[0-9]{8}");
//                        Matcher dateMatcher = datePattern.matcher(fileName);
//                        boolean dateMatchFound = dateMatcher.find();
//                        if (dateMatchFound) {
//                            LocalDate fileDate = LocalDate.parse(dateMatcher.group(0), parser);
//                            
//                            if ((fileDate.isEqual(starDate) || fileDate.isAfter(starDate)) && (fileDate.isEqual(endDate) || fileDate.isBefore(endDate))) {
//                                Scanner myReader = new Scanner(myObj);
//                                while (myReader.hasNextLine()) {
//                                    String data = myReader.nextLine();
//                                    // Pattern pattern = Pattern.compile("logged in with", Pattern.CASE_INSENSITIVE);
//                                    // Matcher matcher = pattern.matcher(data);
//                                    // boolean matchFound = matcher.find();
//                                    // if(matchFound) {
//                                        buildHashMap(data);
//                                    // }
//                                }
//                                myReader.close();
//                                
//                            }
//                        }
//                        else {
//                            throw new Exception("Date not found in the file name");
//                        }
//                    }
//                }
//            } else {
//                throw new Exception("Select a Valid Directory");
//            }
//        } catch (FileNotFoundException e) {
//            System.out.println("An error occurred.");
//            e.printStackTrace();
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//    }
//
//    /**
//    * Takes manually selected files and checks the files for login pattern.
//    * @param selectedFiles The manually selected log files.
//    * @return void This method does not return any value.
//    */
//    private static void getData(File[] selectedFiles){
//        try {
//            File[] directoryListing = selectedFiles;
//            if (directoryListing != null) {
//                for (File myObj : directoryListing) {
//                    Scanner myReader = new Scanner(myObj);
//                    while (myReader.hasNextLine()) {
//                        String data = myReader.nextLine();




//                        // Pattern pattern = Pattern.compile("logged in with", Pattern.CASE_INSENSITIVE);
//                        // Matcher matcher = pattern.matcher(data);
//                        // boolean matchFound = matcher.find();
//                        // if(matchFound) {
//                            buildHashMap(data);
//                        // }





//                    }
//                    myReader.close();
//                    
//                }
//            }
//            else {throw new Exception("No files present");}
//        } catch (FileNotFoundException e) {
//            System.out.println("An error occurred.");
//            e.printStackTrace();
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//    }
//
//    /**
//    * Takes the hour bounds and adds them to {@code loginTimeCount}.
//    * @param hourBounds The hour (upper and lower limit) in which user logged in.
//    * @param licenseName The license user logged in with.
//    * @return void This method does not return any value.
//    */
//    // Helper function to add to the loginTimeCount hashmap
//    private static void addToLoginTimeCount(String hourBounds, String licenseName) {
//        if (loginTimeCount.containsKey(hourBounds)){
//            if (loginTimeCount.get(hourBounds).containsKey(licenseName)){
//                int licenseIncrement = loginTimeCount.get(hourBounds).get(licenseName) + 1;
//                loginTimeCount.get(hourBounds).put(licenseName, licenseIncrement);
//            }
//            else {
//                loginTimeCount.get(hourBounds).put(licenseName, 1);
//            } 
//        }
//        else {
//            loginTimeCount.put(hourBounds, new HashMap<String, Integer>());
//            addToLoginTimeCount(hourBounds, licenseName);
//        } 
//    }
//
//    /**
//    * Reads each line in license files and populates {@code userCount} and {@code licenseCount} data.
//    * @param data Each line present in license files.
//    * @return void This method does not return any value.
//    */
//    
//

//    
//    /**
//    * Takes an array list and returns string arrray i.e. String[][].
//    * @param countArr The array list to convert.
//    * @return String array.
//    */
//    public static String[][] conveArrayListToStringArray(List<List<String>> countArr){
//        String[][] array = new String[countArr.size()][];
//    
//        for (int i = 0; i < countArr.size(); i++) {
//            List<String> innerList = countArr.get(i);
//            array[i] = innerList.toArray(new String[0]);
//        }
//        return array; 
//    }
//
//    /**
//    * Takes an array list and returns {@code rawDataArray}.
//    * @return {@code rawDataArray}.
//    */
//    public static List<List<String>> retrieveRawData() {
//        return rawDataArray;
//    }
//    
//}















