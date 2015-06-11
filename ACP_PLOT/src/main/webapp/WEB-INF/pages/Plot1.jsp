<!doctype html>
<html lang=''>
<head>
   <meta charset='utf-8'>
   <meta http-equiv="X-UA-Compatible" content="IE=edge">
   <meta name="viewport" content="width=device-width, initial-scale=1">
   <link rel="stylesheet" href="css/styles.css"/>
   <link rel="stylesheet" type="text/css" media="screen,projection" href="css/view_form1.css" />
   <link rel="stylesheet" href="css/colpick.css">	
	<script src="http://code.jquery.com/jquery-latest.min.js" type="text/javascript"></script>
	<script src="script/colpick.js"></script>
   <script src="script/script.js"></script>
   <script src="script/calendar.js"></script>
 
   <script src="http://code.highcharts.com/highcharts.js"></script>
   <script src="http://code.highcharts.com/highcharts-more.js"></script>
   <script src="http://code.highcharts.com/modules/exporting.js"></script>
   <title>Plot 1</title>
</head>
<body>
	<script type="text/javascript">
	var chartValues;
	var resp;	
	var counter = 1;
	var limit = 10;
	
	//Colour Picker
		
		function doAjaxPost() {
			// get the form values
			var title = $('#element_1').val();
			var xAxisText = $('#element_2').val();
			var yAxisText = $('#element_3').val();
			var durationBand = $('#element_4').val();
			
			var name5_1 = $('#element_5_1').val();
			var name5_2 = $('#element_5_2').val();
			var name5_3 = $('#element_5_3').val();
			
			var name6_1 = $('#element_6_1').val();
			var name6_2 = $('#element_6_2').val();
			var name6_3 = $('#element_6_3').val();
			
			var name7_1 = $('#element_7_1').val();
			var name7_2 = $('#element_7_2').val();
			var name7_3 = $('#element_7_3').val();
			
			
			var assg1Timeline = new Date(name5_3, name5_1 - 1, name5_2, "0", "0", "0", "0");
			var assg1InMillis = assg1Timeline.getTime();
			
			var assg2Timeline = new Date(name6_3, name6_1 - 1, name6_2, "0", "0", "0", "0");
			var assg2InMillis = assg2Timeline.getTime();
			
			var exam1Timeline = new Date(name7_3, name7_1 - 1, name7_2, "0", "0", "0", "0");
			var examInMillis = exam1Timeline.getTime();
			
			var plotBandFrom = [];
			var plotBandTo = [];
			var plotBandLabel = [];
			var plotBandColour = [];
			var plotBF = $("#dynamicInputPlotBand :input");
			var count;
			for (i = 0; i < plotBF.length; i++) {
			var rem = i%4;
			if(rem==0){
				count = i/4;
			    plotBandFrom[count] = $(plotBF[i]).val();
			} else if(rem==1) {
				plotBandTo[count] = $(plotBF[i]).val();
			}else if(rem==2) {
				plotBandLabel[count] = $(plotBF[i]).val();
			} else if(rem==3) {
				plotBandColour[count] = $(plotBF[i]).val();
			}
			}
			
			$.ajax({
				type : "POST",
				url : "/SpringMVC/firstplot",
				data : "title=" + title + 
					   "&xAxisText=" + xAxisText+ 
					   "&yAxisText=" + yAxisText+
					   "&assg1Time=" + assg1InMillis+
					   "&assg2Time=" + assg2InMillis+
					   "&examTime=" + examInMillis+
					   "&timeBand=" + durationBand+
					   "&plotBandFrom=" + plotBandFrom+
					   "&plotBandTo=" + plotBandTo+
					   "&plotBandLabel=" + plotBandLabel+
					  "&plotBandColour=" + plotBandColour,
				success : function(response) {
					// we have the response
					resp = response;
					chartValues = resp.wrapp;
					$('.chart').show();	
					drawChart();
				},
				error : function(e) {
					alert('Error: ' + e);
				}
			});
		}
		
		
	
		function drawChart() {
			$(function() {
				Highcharts.setOptions({
					global: {
						useUTC: false
					}
				});		
				
				$('#container').highcharts(

				{

			        credits: {
			            enabled: false
			        },
					"chart" : {
						"type" : "column",
						zoomType : 'x'
					},
					"title" : {
						"text" : resp.plotValues.title
					},
					"xAxis" : {
						plotBands : chartValues.plotBands.plotBands ,
						plotLines: [{ // mark the weekend
		                color: 'red',
		                width: 2,
		                value: resp.plotValues.assg1Time,
		                label: {
							text: 'Assignment 1',
							 style: {
								color: 'green',
								fontWeight: 'bold'
		                    }
							},
		                dashStyle: 'ShortDash'
		            }, { // mark the weekend
		                color: 'red',
		                width: 2,
		                value: resp.plotValues.assg2Time,
		                label: {
							text: 'Assignment 2',
							 style: {
								color: 'green',
								fontWeight: 'bold'
		                    }
							},
		                dashStyle: 'ShortDash'
		            }, { // mark the weekend
		                color: 'red',
		                width: 2,
		                value: resp.plotValues.examTime,
		                label: {
							text: 'Test',
							 style: {
								color: 'green',
								fontWeight: 'bold'
		                    }
							},
		                dashStyle: 'ShortDash'
		            }],
						"crosshair" : "true",
						"type" : "datetime",
						"tickmarkPlacement" : "between"
					},
					"yAxis" : {
						"min" : 0,
						 "title": {
				                "text": resp.plotValues.yAxisText
				                //align: 'center'
				            }, 
		            lineWidth: 1,
		            minorGridLineWidth: 0,
		            minorTickInterval: 'auto',
		            minorTickPosition: 'inside',
		            minorTickWidth: 1,
		            minorTickLength: 4
					},
					"series" : chartValues.series[0].data
				});
			});
		};
								
		
		function addInput(divName){
		     if (counter == limit)  {
		          alert("You have reached the limit of adding " + counter + " inputs");
		     }
		     else {
		          var newdiv = document.createElement('div');
		          newdiv.innerHTML = "<li><label class='description'>Plot Band From Time "+counter+
					"</label><div><input name='plotbandFrom[]' class='element text medium' type='text' maxlength='255' value='' /></div></li>"+
					
					"<li><label class='description'>Plot Band To Time "+counter+
					"</label><div><input name='plotbandTo[]' class='element text medium' type='text' maxlength='255' value='' /></div></li>"+
					
					"<li><label class='description'>Plot Band Label "+counter+
					"</label><div><input name='plotbandLabel[]' class='element text medium' type='text' maxlength='255' value='' /></div></li>"+ 
					
					"<li><label class='description'>Plot Band Colour "+counter+
					"</label><div><input class='picker' type='text' value='e2f71e'/></div></li>";
		          document.getElementById(divName).appendChild(newdiv);
		          showColorPick();
		          counter++;
		         
		     }
		     
		}
	</script>

	<div id='topnav'>
<ul>
   <li class='active'><a href='#'><span>Home</span></a></li>
   <li><a href='#'><span>File Upload</span></a></li>
   <li><a href='#'><span>Plot Configuration</span></a></li>
   <li class='last'><a href='#'><span>Contact</span></a></li>
</ul>
</div>

<br><br><br><br>
<div id='leftandcontent'>
<div id='leftnav'>
<ul>
   <li class='active'><a href='#'><span>Home</span></a></li>
   <li class='has-sub'><a href='#'><span>Usage Analysis Charts</span></a>
      <ul>
         <li><a href='/firstplot'><span>Chart A</span></a></li>
         <li><a href='#'><span>Chart B</span></a></li>
         <li class='last'><a href='#'><span>Product 3</span></a></li>
      </ul>
   </li>
   <li class='has-sub'><a href='#'><span>Usage vs Marks Analysis Charts</span></a>
      <ul>
         <li><a href='#'><span>Company</span></a></li>
         <li class='last'><a href='#'><span>Contact</span></a></li>
      </ul>
   </li>
   <li class='has-sub'><a href='#'><span>Other Charts</span></a>
   <ul>
         <li><a href='#'><span>Chart A</span></a></li>
         <li class='last'><a href='#'><span>Chart b</span></a></li>
      </ul>
   </li>
   <li class='last'><a href='#'><span>xyz Chart</span></a></li>
</ul>
</div>
<div id="formchart">
<div id='content'>
	<!-- Form section -->

	    <!-- Text container -->
	    <img id="top" src="images/top.png" alt="">
			<div id="form_container">

				<h1><a>Untitled Form</a></h1>
				<div id="form" class ="appnitro">
				<div class="form_description">
					<h2>All project downloads along timeline</h2>
					<p>Edit the necessary fields to reflect in the plots</p>
				</div>
					<ul >

					<li id="li_1"><label class="description" for="element_1">Title
					</label>
						<div>
							<input id="element_1" name="element_1"
								class="element text medium" type="text" maxlength="255" value="" />
						</div></li>
					<li id="li_2"><label class="description" for="element_2">x
							Axis text </label>
						<div>
							<input id="element_2" name="element_2"
								class="element text medium" type="text" maxlength="255" value="" />
						</div></li>
					<li id="li_3"><label class="description" for="element_3">y
							Axis text </label>
						<div>
							<input id="element_3" name="element_3"
								class="element text medium" type="text" maxlength="255" value="" />
						</div></li>
					<li id="li_4"><label class="description" for="element_4">Duration Band
					</label>
						<div>
							<input id="element_4" name="element_4"
								class="element text medium" type="text" maxlength="255" value="" />
						</div></li>
					
					<div id="dynamicInputPlotBand">
				        						
				     </div>
				     <input type="button" value="Add Plotband values" onClick="addInput('dynamicInputPlotBand');"/>	
					
					<script type="text/javascript">
					function showColorPick() {
						$('.picker').colpick({
									layout:'hex',
									submit:0,
									colorScheme:'dark',
									onChange:function(hsb,hex,rgb,el,bySetColor) {
										$(el).css('border-color','#'+hex);
										// Fill the text box just if the color was set using the picker, and not the colpickSetColor function.
										if(!bySetColor) $(el).val(hex);
									}
								}).keyup(function(){
									$(this).colpickSetColor(this.value);
							});
							}
					</script>
					
					<li id="li_5"><label class="description" for="element_5">Assignment 1
							Timeline </label> <span> <input id="element_5_1"
							name="element_5_1" class="element text" size="2" maxlength="2"
							value="" type="text"> / <label for="element_5_1">MM</label>
					</span> <span> <input id="element_5_2" name="element_5_2"
							class="element text" size="2" maxlength="2" value="" type="text">
							/ <label for="element_5_2">DD</label>
					</span> <span> <input id="element_5_3" name="element_5_3"
							class="element text" size="4" maxlength="4" value="" type="text">
							<label for="element_5_3">YYYY</label>
					</span> <span id="calendar_5"> <img id="cal_img_5"
							class="datepicker" src="images/calendar.gif" alt="Pick a date.">
					</span> <script type="text/javascript">
						Calendar.setup({
							inputField : "element_5_3",
							baseField : "element_5",
							displayArea : "calendar_5",
							button : "cal_img_5",
							ifFormat : "%B %e, %Y",
							onSelect : selectDate
						});
					</script></li>
					
					<li id="li_6"><label class="description" for="element_6">Assignment 2 
							Timeline </label> <span> <input id="element_6_1"
							name="element_6_1" class="element text" size="2" maxlength="2"
							value="" type="text"> / <label for="element_6_1">MM</label>
					</span> <span> <input id="element_6_2" name="element_6_2"
							class="element text" size="2" maxlength="2" value="" type="text">
							/ <label for="element_6_2">DD</label>
					</span> <span> <input id="element_6_3" name="element_6_3"
							class="element text" size="4" maxlength="4" value="" type="text">
							<label for="element_6_3">YYYY</label>
					</span> <span id="calendar_6"> <img id="cal_img_6"
							class="datepicker" src="images/calendar.gif" alt="Pick a date.">
					</span> <script type="text/javascript">
						Calendar.setup({
							inputField : "element_6_3",
							baseField : "element_6",
							displayArea : "calendar_6",
							button : "cal_img_6",
							ifFormat : "%B %e, %Y",
							onSelect : selectDate
						});
					</script></li>
					
					<li id="li_7"><label class="description" for="element_7">Exam 
							Timeline </label> <span> <input id="element_7_1"
							name="element_7_1" class="element text" size="2" maxlength="2"
							value="" type="text"> / <label for="element_7_1">MM</label>
					</span> <span> <input id="element_7_2" name="element_7_2"
							class="element text" size="2" maxlength="2" value="" type="text">
							/ <label for="element_7_2">DD</label>
					</span> <span> <input id="element_7_3" name="element_7_3"
							class="element text" size="4" maxlength="4" value="" type="text">
							<label for="element_7_3">YYYY</label>
					</span> <span id="calendar_7"> <img id="cal_img_7"
							class="datepicker" src="images/calendar.gif" alt="Pick a date.">
					</span> <script type="text/javascript">
						Calendar.setup({
							inputField : "element_7_3",
							baseField : "element_7",
							displayArea : "calendar_7",
							button : "cal_img_7",
							ifFormat : "%B %e, %Y",
							onSelect : selectDate
						});
					</script></li>

					<li class="buttons"><input type="hidden" name="form_id"
						value="1002984" /> <input id="saveForm" class="button_text"
						type="submit" name="submit" value="Submit" onclick="doAjaxPost()" /></li>
				</ul>
				</div>
				<div id="footer">

				</div>
			</div>
		<img id="bottom" src="images/bottom.png" alt="">
</div>

<div class="chart">
	<div id="container" style="width:100%; height:500px;"></div>
</div>
</div>
</div>
</body>
</html>
