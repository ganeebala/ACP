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
	<script type="text/javascript" src="script/jquery.simple-dtpicker.js"></script>
	<link type="text/css" href="css/jquery.simple-dtpicker.css" rel="stylesheet" />
	<script src="script/colpick.js"></script>
   <script src="script/script.js"></script>
 
   <script src="http://code.highcharts.com/highcharts.js"></script>
   <script src="http://code.highcharts.com/highcharts-more.js"></script>
   <script src="http://code.highcharts.com/modules/exporting.js"></script>
   <title>Comparision between various marks</title>
</head>
<body>
	<script type="text/javascript">
	var chartValues;
	var resp;	
	var counter = 1;
	var categoryCounter = 0;
	var limit = 10;
	
	var numberOfMarks;
	
	var improvementSeriesList;
	var userByCategory;
	//Colour Picker
		
		function doAjaxPost() {
			// get the form values
			var title = $('#element_1').val();
			var xAxisText = $('#element_2').val();
			var yAxisText = $('#element_3').val();
			var durationBand = $('#element_4').val();
			
			var wrapperObj = new Object();
			wrapperObj.title = title;
			wrapperObj.xAxisText = xAxisText;
			wrapperObj.yAxisText = yAxisText;
			wrapperObj.categories = [];
			//
			var categoryHtml = $("#category");
			
			for (var i=1; i<=categoryCounter; i++){
				var temp4 = $(categoryHtml).find('#category_'+i);
				
				var catg = $(categoryHtml).find('#category_'+i).find('li').find('input');
				var catgTimeDom = $(categoryHtml).find('#category_'+i);
				var elements = $(categoryHtml).find('#category_'+i).find('li')
				
				var GroupingCategory = new Object();
				GroupingCategory.categoryName =$(catg[0]).val() ;
				GroupingCategory.threshold =$(catg[1]).val() ;
				GroupingCategory.percentageValue =$(catg[2]).val() ;
				GroupingCategory.startTimes = [];
				GroupingCategory.endTimes = [];
				
				var timeCount = $(catgTimeDom).find("div").length;				
				//Hard coded value: Logic Needs to be changed!!
				timeCount = timeCount -4;
				for (var j=1; j<=timeCount ; j++){
					var startTime = $(catgTimeDom).find('#element_category_'+i+'time_'+j+'_start').val();
					GroupingCategory.startTimes.push(startTime);
					var endTime = $(catgTimeDom).find('#element_category_'+i+'time_'+j+'_end').val();
					GroupingCategory.endTimes.push(endTime);
				}
				wrapperObj.categories.push(GroupingCategory);
			}
				
			$.ajax({
				type : "POST",
				url : "/SpringMVC/markComparision",
			      contentType : 'application/json; charset=utf-8',
			      dataType : 'json',
			      data: JSON.stringify(wrapperObj), 
				success : function(response) {
					// we have the response
					 resp = response;
					chartValues = resp.wrapp;
					markComparisionList = resp.markComparisionList;
					userByCategory = resp.userByCategory;

					$('.chart').show();					
					drawChartComparision(markComparisionList);
					generateUserCategoryTable(userByCategory);
				
				},
				error : function(e) {
					alert('Error: ' + e);
				}
			});
		}
		/////////////////////////////////////////////////////

		
		///////////////////////////////////////////////////	
				
		function drawChartComparision(improvementSeriesList) {
			$(function() {
				Highcharts.setOptions({
					global: {
						useUTC: false
					}
				});		
				
				$('#chartComparisionContainer').highcharts(

				{
			        chart: {
			            marginBottom: 300
			        },
			        credits: {
			            enabled: false
			        },
					"chart" : {
						"type" : "scatter",
						zoomType : 'xy'
					},
					"title" : {
						"text" : "CHange it!! COmparision Stuff"
					},
					"xAxis" : {
						title: {
			                enabled: true,
			                text: 'Usage '
			            },
			            startOnTick: true,
			            endOnTick: true,
			            showLastLabel: true
					},
					"yAxis" : {
						 "title": {
				                "text": "change it!!"//resp.plotValues.yAxisText
				                //align: 'center'
				            } , 
		            lineWidth: 1,
		            minorGridLineWidth: 0,
		            minorTickInterval: 'auto',
		            minorTickPosition: 'inside',
		            minorTickWidth: 1,
		            minorTickLength: 4 
					},
					legend: {
			            layout: 'vertical',
			            align: 'left',
			            verticalAlign: 'bottom',
			            x: 0,
			            y: 10,
			           // floating: true,
			            backgroundColor: (Highcharts.theme && Highcharts.theme.legendBackgroundColor) || '#FFFFFF',
			            borderWidth: 1
			        },
			        plotOptions: {
			            scatter: {
			                marker: {
			                    radius: 5,
			                    states: {
			                        hover: {
			                            enabled: true,
			                            lineColor: 'rgb(100,100,100)'
			                        }
			                    }
			                },
			                states: {
			                    hover: {
			                        marker: {
			                            enabled: false
			                        }
			                    }
			                },
			                tooltip: {
			                    headerFormat: '<b>{series.name}</b><br>',
			                    pointFormat: '{point.x} , {point.y} '
			                }
			            }
			        },
					"series" : improvementSeriesList
				});
			});
		};
		
		function generateUserCategoryTable(userByCategory){
			var tableCode = "<table>";
			for (var m in userByCategory){
				tableCode += "<tr>" +"<th>" + "Category :"+m + "</th>" +"</tr>";				
			    for (var i=0;i<userByCategory[m].length;i++){
			    	var x = userByCategory[m][i]; 
			    	tableCode += "<tr>";
			    	tableCode += "<td>"+x.userId+"</td>";
			    	for (var j in userByCategory[m][i].categoryWiseUsage) {
			    		var col = "<td>"+j +" : "+userByCategory[m][i].categoryWiseUsage[j]+"</td>";			    		
			    		tableCode +=col;
			    	}	
			    	tableCode += "</tr>";
			    }
			    
			} 
			tableCode += "</table>";
			$('#generatedTable').empty();
		    $("#generatedTable").append(tableCode);
		}
								
		
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
					"</label><div><input class='picker' name='plotbandColour[]' type='text'/></div></li>";
		          document.getElementById(divName).appendChild(newdiv);
		          showColorPick();
		          counter++;
		         
		     }
		     
		}
		
		function addTime(divName){
			var newdiv = document.createElement('div');
			var timeCount = $("#"+divName).find("div").length;
			
			//Hard coded value: Logic Needs to be changed!!
			timeCount = timeCount -3;
			
			if (timeCount > 10)  {
		          alert("You have reached the limit of adding " + timeCount + " inputs");
		     }
			newdiv.id = divName+"time_"+timeCount;
			newdiv.innerHTML = "<li id='li_"+newdiv.id+"'>"+
									"<label class='description' for='element_"+newdiv.id+"_start'>Start Time "+timeCount+" </label> "+ 
									"<span> <input id='element_"+newdiv.id+"_start' class='datetimepicker'"+
										"name='element_"+newdiv.id+"_start' class='element text' value='' type='text'>"+
									"</span> "+
									"<label class='description' for='element_"+newdiv.id+"_end'>End Time "+timeCount+" </label>"+ 
									"<span> <input id='element_"+newdiv.id+"_end' class='datetimepicker'"+
										"name='element_"+newdiv.id+"_end' class='element text' value='' type='text'>"+
									"</span>"+
								"</li>";
								
								document.getElementById(divName).appendChild(newdiv);
								showTime();		
		}
		
		function addCategory(divName){
			categoryCounter = categoryCounter+1;
			var newdiv = document.createElement('div');
			newdiv.id="category_"+categoryCounter;
			var newid = newdiv.id;
			newdiv.innerHTML ="<label class='description'>Category "+categoryCounter+" </label>"+
							"<li id='li_catg"+categoryCounter+"_1'><label class='description' for='element_catg"+categoryCounter+"_1'>Category Name</label>"+
								"<div>"+
									"<input id='element_catg"+categoryCounter+"_1' name='element_catg"+categoryCounter+"_1' class='element text medium' type='text' maxlength='255' value='' />"+
								"</div>"+
							 "</li>"+
							 "<li id='li_catg"+categoryCounter+"_2'><label class='description' for='element_catg"+categoryCounter+"_2'>Minimum Threshold</label>"+
								"<div>"+
									"<input id='element_catg"+categoryCounter+"_2' name='element_catg"+categoryCounter+"_2' class='element text medium' type='text' maxlength='255' value='' />"+
								"</div>"+
							 "</li>"+
							 "<li id='li_catg"+categoryCounter+"_3'><label class='description' for='element_catg"+categoryCounter+"_3'>Threshold %(of Total usage)</label>"+
								"<div>"+
									"<input id='element_catg"+categoryCounter+"_3' name='element_catg"+categoryCounter+"_3' class='element text medium' type='text' maxlength='255' value='' />"+
								"</div>"+
							 "</li>"+
							 // Plus sign on click on which new time input fields will be displayed
							"<div class='ui-icon ui-icon-plus addRow' onClick='addTime(\""+newid+"\");' >Time+</div>";
					
							document.getElementById(divName).appendChild(newdiv);
							var temp = $("#"+newdiv.id).wrap( "<fieldset></fieldset>" );
							

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
					<h2>Comparision between improvement in marks</h2>
					<p>Enter the necessary fields to reflect in the plots</p>
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
					<script>					
					function showTime() {
						$('.datetimepicker').appendDtpicker({
				            'dateFormat' : 'YYYY/MM/DD hh:mm'
		        			});
						}					
					</script>
					
					
					<br><br>
					<div class="legend">
					<label class="description">Grouping Category Definition</label>
					</div>
					
					<fieldset>
					<div id = 'category'>			
					
					</div>
					</fieldset>					
					<div id="categoryAdd" class="ui-icon ui-icon-plus addRow" onClick="addCategory('category');" >Add category +</div>

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
<div id ="generatedTable" > </div>
<div id="chartComparisionContainer" style='width:100%; height:600px;' class="chart">
	
</div>
</div>
</div>
</body>
</html>
