<%--suppress HtmlUnknownTarget --%>
<!-- chart.jsp-->
<%@ page contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en" xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Home Measurement Dashboard</title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="/webjars/bootstrap/css/bootstrap.min.css" />
    <script src="/webjars/jquery/jquery.min.js"></script>
    <script src="/webjars/bootstrap/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
    <script type="text/javascript">
        google.charts.load('current', {packages: ['corechart']});
        google.charts.setOnLoadCallback(drawChart);

        // Callback that creates and populates a data table,
        // instantiates the pie chart, passes in the data and
        // draws it.
        function drawChart() {

            // Create the data table.
            <%--var data = google.visualization.arrayToDataTable([--%>
            <%--        ['Country', 'Area(square km)'],--%>
            <%--    <c:forEach items="${sensors}" var="sensor" >--%>
            <%--        <c:forEach items="${sensor}" var="measurement"--%>
            <%--            ['${sensor.key}', ${entry.value}],--%>
            <%--    </c:forEach>--%>
            <%--]);--%>

            const data = new google.visualization.DataTable();
            data.addColumn('datetime', 'Time');
            data.addColumn('number', 'Sensor1');
            data.addColumn('number', 'Sensor2');
            data.addRows([
                [new Date('2018-05-10T10:30:00-0800'),56.8,46.1],
                [new Date('2018-05-10T10:35:00-0800'),55.2,48.4],
                [new Date('2018-05-10T10:40:00-0800'),55.7,49.9],
                [new Date('2018-05-10T10:45:00-0800'),47.8,55.2],
                [new Date('2018-05-10T10:50:00-0800'),44.3,55.2],
                [new Date('2018-05-10T10:55:00-0800'),43.8,55.4],
                [new Date('2018-05-10T11:00:00-0800'),41.6,55.8],
                [new Date('2018-05-10T11:05:00-0800'),37.8,55.2],
                [new Date('2018-05-10T11:10:00-0800'),33.8,55.5],
                [new Date('2018-05-10T11:15:00-0800'),33.8,55.3]
            ]);
console.log("here");
            // Set chart options
            const options = {
                'title': 'Area-wise Top Seven Countries in the World',
                curveType: 'function',
                legend: {position: 'bottom'},
                'width': 900,
                'height': 500
            };

            // Instantiate and draw our chart, passing in some options.
            const chart = new google.visualization.LineChart(document.getElementById('curve_chart'));
            chart.draw(data, options);
        }
    </script>
</head>
<body>
<!--/*@thymesVar id="sensor" type="java.util.Map.Entry"*/-->
<!--/*@thymesVar id="sensorrecord" type="eu.seatter.homemeasurement.collector.model.SensorRecord"*/-->
<div class="container-fluid" style="margin-top: 20px">
    <div class="row">
        <div class="col-md-6 col-md-offset-3">
            <div class="panel panel-primary">

                <div class="panel-heading">
                    <h1 class="panel-title">Measurement Dashboard</h1>
                </div>
                <div class="panel-body">
<%--                    <div th:if="${not #lists.isEmpty(sensors)}">--%>





<%--                    </div>--%>
                    <div id="curve_chart"></div>
                </div>
            </div>
        </div>
    </div>

</div>