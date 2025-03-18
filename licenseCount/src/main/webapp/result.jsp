<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="java.io.PrintWriter" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Data Table</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <style>
        :root {
            --primary-color: #4a6fdc;
            --primary-hover: #3a5fc6;
            --background-color: #f8f9fa;
            --table-header-bg: #e9ecef;
            --table-border: #dee2e6;
            --text-color: #212529;
            --text-muted: #6c757d;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            line-height: 1.6;
            color: var(--text-color);
            background-color: var(--background-color);
            margin: 0;
            padding: 20px;
        }
        
        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
        }
        
        h1 {
            color: var(--primary-color);
            margin-top: 0;
            padding-bottom: 15px;
            border-bottom: 1px solid var(--table-border);
        }
        
        .table-responsive {
            overflow-x: auto;
            margin-bottom: 20px;
        }
        
        .data-table {
            width: 100%;
            border-collapse: collapse;
            border: 1px solid var(--table-border);
            font-size: 0.95rem;
        }
        
        .data-table th {
            background-color: var(--table-header-bg);
            padding: 12px 15px;
            text-align: left;
            font-weight: 600;
            border: 1px solid var(--table-border);
            position: sticky;
            top: 0;
        }
        
        .data-table td {
            padding: 10px 15px;
            border: 1px solid var(--table-border);
        }
        
        .data-table tr:nth-child(even) {
            background-color: rgba(0, 0, 0, 0.02);
        }
        
        .data-table tr:hover {
            background-color: rgba(74, 111, 220, 0.05);
        }
        
        .no-data {
            text-align: center;
            padding: 30px;
            color: var(--text-muted);
            font-style: italic;
        }
        
        .table-info {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
        }
        
        .table-title {
            font-size: 1.2rem;
            font-weight: 500;
            color: var(--text-color);
        }
        
        .table-stats {
            color: var(--text-muted);
            font-size: 0.9rem;
        }
        
        .search-container {
            margin-bottom: 20px;
        }
        
        .search-input {
            padding: 8px 12px;
            border: 1px solid var(--table-border);
            border-radius: 4px;
            width: 250px;
            font-size: 0.9rem;
        }
        
        .btn {
            padding: 8px 15px;
            background-color: var(--primary-color);
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 0.9rem;
            transition: background-color 0.2s;
        }
        
        .btn:hover {
            background-color: var(--primary-hover);
        }
        
        .footer {
            margin-top: 20px;
            text-align: right;
            font-size: 0.8rem;
            color: var(--text-muted);
        }
        
        .back-link {
            margin-top: 20px;
            display: inline-block;
            padding: 10px 15px;
            background-color: rgb(82, 110, 213);
            color: white;
            text-decoration: none;
            border-radius: 4px;
        }
        .back-link:hover {
            background-color: rgb(82, 110, 243);
        }
        
        @media (max-width: 768px) {
            .container {
                padding: 15px;
            }
            
            .data-table th, 
            .data-table td {
                padding: 8px 10px;
                font-size: 0.9rem;
            }
            
            .search-input {
                width: 100%;
                margin-bottom: 10px;
            }
            
            .table-info {
                flex-direction: column;
                align-items: flex-start;
            }
            
            .table-stats {
                margin-top: 5px;
            }
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>Login Report</h1>
        
        <div class="search-container">
            <input type="text" id="searchInput" class="search-input" placeholder="Search data..." onkeyup="searchTable()">
        </div>
        
        <div class="table-info">
            <div class="table-title">
                <i class="fas fa-table"></i> Users, their Licenses and Login Counts
            </div>
            <%
                List<List<String>> dataList = (List<List<String>>) session.getAttribute("dataList");
            	List<List<String>> licenseCount = (List<List<String>>) session.getAttribute("licenseCount");
               session.setAttribute("licenseCount", licenseCount);
            
                int rowCount = (dataList != null) ? dataList.size() : 0;
                int colCount = (rowCount > 0) ? dataList.get(0).size() : 0;
            %>
            <div class="table-stats">
                <i class="fas fa-info-circle"></i> <%= rowCount %> rows &times; <%= colCount %> columns
            </div>
        </div>
        
        <div class="table-responsive">
            <table class="data-table" id="dataTable">
            <% 
                if (dataList != null && !dataList.isEmpty() && !dataList.get(0).isEmpty()) {
                    // First row as headers
                    List<String> headers = dataList.get(0);
            %>
                <thead>
                    <tr>
                        <th>SL No</th>
                        <th>User Name</th>
                        <th>License Name</th>
                        <th># of Logins</th>
                    </tr>
                </thead>
                <tbody>
                <% 
                    // Data rows starting from index 1
                    for (int i = 0; i < dataList.size(); i++) {
                        List<String> row = dataList.get(i);
                %>
                    <tr>
                    <% for (String cell : row) { %>
                        <td><%= cell %></td>
                    <% } %>
                    </tr>
                <% 
                    }
                %>
                </tbody>
            <% 
                } else { 
            %>
                <tr>
                    <td class="no-data" colspan="5">No data available</td>
                </tr>
            <% } %>
            </table>
        </div>
        
        <div class="footer">
            Generated on <%= new java.util.Date() %>
        </div>
    </div>
    
    <script>
        function searchTable() {
            const input = document.getElementById('searchInput');
            const filter = input.value.toUpperCase();
            const table = document.getElementById('dataTable');
            const rows = table.getElementsByTagName('tr');
            
            // Loop through all table rows (skip header)
            for (let i = 1; i < rows.length; i++) {
                const cells = rows[i].getElementsByTagName('td');
                let found = false;
                
                // Loop through all cells in current row
                for (let j = 0; j < cells.length; j++) {
                    const cell = cells[j];
                    if (cell) {
                        const text = cell.textContent || cell.innerText;
                        if (text.toUpperCase().indexOf(filter) > -1) {
                            found = true;
                            break;
                        }
                    }
                }
                
                // Show/hide based on whether text was found
                rows[i].style.display = found ? '' : 'none';
            }
        }
    </script>
    <a href="index.html" class="back-link">Back to Upload Page</a>
    <a href="licenseChart.jsp" class="back-link">To Chart</a>
    <a href="ExportToExcelServlet" class="back-link">Export to Excel</a>
</body>
</html>