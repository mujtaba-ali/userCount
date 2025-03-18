<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>License Usage Report</title>
    <!-- Include Chart.js from CDN -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/3.9.1/chart.min.js"></script>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f5f5f5;
            color: #333;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background-color: #fff;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            padding: 20px;
        }
        h1 {
            color: #2c3e50;
            margin-top: 0;
            padding-bottom: 15px;
            border-bottom: 1px solid #eee;
        }
        .chart-container {
            position: relative;
            height: 400px;
            margin: 20px 0;
        }
        .data-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 30px;
        }
        .data-table th, .data-table td {
            padding: 12px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        .data-table th {
            background-color: #f8f9fa;
            font-weight: 600;
        }
        .data-table tr:hover {
            background-color: #f1f1f1;
        }
        .timestamp {
            text-align: right;
            color: #6c757d;
            font-size: 0.9em;
            margin-top: 20px;
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
    </style>
</head>
<body>
    <div class="container">
        <h1>License Usage Report</h1>
        
        <%
        // Get the license data from the request attribute
        List<List<String>> licenseData = (List<List<String>>) session.getAttribute("licenseCount");
        
        // Check if data exists
        boolean hasData = licenseData != null && !licenseData.isEmpty();
        
        if (!hasData) {
        %>
            <div class="alert">
                <p>No license usage data available. Please try again later.</p>
            </div>
        <%
        } else {
        %>
            <div class="chart-container">
                <canvas id="licenseChart"></canvas>
            </div>
            
            <table class="data-table">
                <thead>
                    <tr>
                        <th>License Name</th>
                        <th>Usage Count</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                    for(List<String> row : licenseData) {
                        String licenseName = row.get(0);
                        String usageCount = row.get(1);
                    %>
                    <tr>
                        <td><%= licenseName %></td>
                        <td><%= usageCount %></td>
                    </tr>
                    <%
                    }
                    %>
                </tbody>
            </table>
            
            <p class="timestamp">Report generated on: <%= new java.util.Date() %></p>
            <a href="index.html" class="back-link">Back to Upload Page</a>
    		<a href="result.jsp" class="back-link">Back to Table</a>
            
            <script>
                // Extract data for Chart.js
                const licenseNames = [];
                const usageCounts = [];
                
                <% 
                for(List<String> row : licenseData) {
                %>
                    licenseNames.push('<%= row.get(0) %>');
                    usageCounts.push(<%= row.get(1) %>);
                <%
                }
                %>
                
                // Create chart
                const ctx = document.getElementById('licenseChart').getContext('2d');
                const licenseChart = new Chart(ctx, {
                    type: 'bar',
                    data: {
                        labels: licenseNames,
                        datasets: [{
                            label: 'License Usage Count',
                            data: usageCounts,
                            backgroundColor: 'rgba(54, 162, 235, 0.7)',
                            borderColor: 'rgba(54, 162, 235, 1)',
                            borderWidth: 1
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: {
                                position: 'top',
                            },
                            title: {
                                display: true,
                                text: 'License Usage Distribution'
                            }
                        },
                        scales: {
                            y: {
                                beginAtZero: true,
                                title: {
                                    display: true,
                                    text: 'Usage Count'
                                },
                                ticks: {
                                    precision: 0
                                }
                            },
                            x: {
                                title: {
                                    display: true,
                                    text: 'License Name'
                                }
                            }
                        }
                    }
                });
            </script>
        <% } %>
    </div>
</body>
</html>