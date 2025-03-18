package com.lgs;

import java.io.IOException;
import java.io.OutputStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@WebServlet("/ExportToExcelServlet")
public class ExportToExcelServlet extends jakarta.servlet.http.HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        XSSFWorkbook workbook = (XSSFWorkbook) session.getAttribute("excelWorkbook");
        
        if (workbook != null) {
            // Set response headers
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=userCount.xlsx");
            
            // Write workbook to response output stream
            OutputStream out = response.getOutputStream();
            workbook.write(out);
            
            // Clean up
            out.close();
            workbook.close();
            session.removeAttribute("excelWorkbook");
        } else {
            response.getWriter().println("No Excel file available for download.");
        }
    }
}