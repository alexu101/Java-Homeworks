<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>File Upload</title>
    <script src="https://www.google.com/recaptcha/api.js" async defer></script>
</head>
<body>
<form id="fileUploadForm" action="file-upload-servlet" method="post" enctype="multipart/form-data">
    <label for="textFile">Upload File:</label>
    <input type="file" id="textFile" name="textFile" accept=".txt" required>
    <div class="g-recaptcha" data-sitekey="6LdYXF8qAAAAALp6LBlSEJJX0agsYo_35oUjQl5N"></div> <!-- Use your actual site key -->
    <br><br>
    <input type="submit" value="Submit">
</form>
</body>
</html>
