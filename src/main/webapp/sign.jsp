<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head><title>DSS Browser POST</title></head>
    <body>
        <p>Redirecting to the DSS Server...</p>
        <form name="BrowserPostForm" method="post" action="https://www.e-contract.be/dss-ws/start">
            <input type="hidden" name="PendingRequest" value="<%=session.getAttribute("PENDING-REQUEST")%>"/>
        </form>
        <script type="text/javascript">
            window.onload = function () {
                document.forms["BrowserPostForm"].submit();
            }
        </script>
    </body>
</html>
