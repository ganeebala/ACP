<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
<title>Upload Multiple File Request Page</title>
<style>
/* Elegant Aero */
.elegant-aero {
    margin-left:auto;
    margin-right:auto;
	margin-top: 10%
    max-width: 500px;
    background: #D2E9FF;
    padding: 20px 20px 20px 20px;
    font: 12px Arial, Helvetica, sans-serif;
    color: #666;
}
/* .elegant-aero h1 {
    font: 24px "Trebuchet MS", Arial, Helvetica, sans-serif;
    padding: 10px 10px 10px 20px;
    display: block;
    background: #C0E1FF;
    border-bottom: 1px solid #B8DDFF;
    margin: -20px -20px 15px;
}
.elegant-aero h1>span {
    display: block;
    font-size: 11px;
}
 */
/* .elegant-aero label>span {
    float: left;
    margin-top: 10px;
    color: #5E5E5E;
}
.elegant-aero label {
    display: block;
    margin: 0px 0px 5px;
}
.elegant-aero label>span {
    float: left;
    width: 20%;
    text-align: right;
    padding-right: 15px;
    margin-top: 10px;
    font-weight: bold;
} */
.elegant-aero input[type="text"], .elegant-aero input[type="email"], .elegant-aero textarea, .elegant-aero select {
    color: #888;
    width: 70%;
    padding: 0px 0px 0px 5px;
    border: 1px solid #C5E2FF;
    background: #FBFBFB;
    outline: 0;
    -webkit-box-shadow:inset 0px 1px 6px #ECF3F5;
    box-shadow: inset 0px 1px 6px #ECF3F5;
    font: 200 12px/25px Arial, Helvetica, sans-serif;
    height: 30px;
    line-height:15px;
    margin: 2px 6px 16px 0px;
}
.elegant-aero textarea{
    height:100px;
    padding: 5px 0px 0px 5px;
    width: 70%;
}
.elegant-aero select {
    background: #fbfbfb url('down-arrow.png') no-repeat right;
    background: #fbfbfb url('down-arrow.png') no-repeat right;
   appearance:none;
    -webkit-appearance:none;
   -moz-appearance: none;
    text-indent: 0.01px;
    text-overflow: '';
    width: 70%;
}
.elegant-aero .button{
    padding: 10px 30px 10px 30px;
    background: #66C1E4;
    border: none;
    color: #FFF;
    box-shadow: 1px 1px 1px #4C6E91;
    -webkit-box-shadow: 1px 1px 1px #4C6E91;
    -moz-box-shadow: 1px 1px 1px #4C6E91;
    text-shadow: 1px 1px 1px #5079A3;
   
}
.elegant-aero .button:hover{
    background: #3EB1DD;
}
</style>
</head>
<body>
 <div class ='elegant-aero'>
    <form method="POST" action="uploadMultipleFile" enctype="multipart/form-data">
        File1 to upload: <input type="file" name="file"><br /> 
        Name1: <input type="text" name="name"><br /> <br />         
        <input type="submit" value="Upload"> Press here to upload the file!
    </form>
  </div>   
</body>
</html>