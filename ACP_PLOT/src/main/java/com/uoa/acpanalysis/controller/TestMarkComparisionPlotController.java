package com.uoa.acpanalysis.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.uoa.acpanalysis.Utilities.Utilities;
import com.uoa.acpanalysis.model.Marks;
import com.uoa.acpanalysis.model.Plot1;
import com.uoa.acpanalysis.model.User;
import com.uoa.acpanalysis.model.formdata.GroupingCategory;
import com.uoa.acpanalysis.model.formdata.markAnalysis;
import com.uoa.acpanalysis.reader.ACPReader;
import com.uoa.acpanalysis.wrapper.Label;
import com.uoa.acpanalysis.wrapper.ParentWrapper;
import com.uoa.acpanalysis.wrapper.Series;

@Controller
/*@RequestMapping("/firstplot")*/
public class TestMarkComparisionPlotController {
	
	private   ArrayList<Date> se701startTimes = new ArrayList<Date>();
	private   ArrayList<Date> se701endTimes = new ArrayList<Date>();
	private   ArrayList<Date> se751startTimes = new ArrayList<Date>();
	private   ArrayList<Date> se751endTimes = new ArrayList<Date>();
	
	private   ArrayList<Date> ownStartTimes = new ArrayList<Date>();
	private   ArrayList<Date> ownEndTimes = new ArrayList<Date>();
	
	private   ArrayList<Date> lastMinstartTimes = new ArrayList<Date>();
	private   ArrayList<Date> lastMinendTimes = new ArrayList<Date>();
	
	private   SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@RequestMapping(value="/markComparision",method = RequestMethod.GET)
	public String printWelcomefirst(ModelMap model) {

		model.addAttribute("message", "Spring 3 MVC Hello World");
		return "markComparision";

	}

    @SuppressWarnings("unchecked")
	@RequestMapping(value="/markComparision",method=RequestMethod.POST,produces = "application/json")
    public @ResponseBody HashMap  addUser(@RequestBody markAnalysis markAnalysis) throws ParseException{
        @SuppressWarnings("rawtypes") 
		HashMap model = new HashMap();
         
        //Converting the date values from String to a Date type and setting it in marAnalaysis obj itself
        for (int i=0; i<markAnalysis.getCategories().size();i++ ) {
        		markAnalysis.getCategories().get(i).setStartTimesDate(Utilities.getDate(markAnalysis.getCategories().get(i).getStartTimes()));
        		markAnalysis.getCategories().get(i).setEndTimesDate(Utilities.getDate(markAnalysis.getCategories().get(i).getEndTimes()));
        }

        	
        // TODO Remove Or modifiy acc to....
        //Once logic for a radio button with lecture timings is ready..
        setLectureTimes();
       GroupingCategory catg1 = new GroupingCategory();
       catg1.setStartTimesDate(se701startTimes);
       catg1.setEndTimesDate(se701endTimes); 
       catg1.setCategoryName("LectureTime");
       catg1.setThreshold(15);
       catg1.setPercentageValue(10);
       markAnalysis.getCategories().add(catg1); 
       
/*       GroupingCategory catg2 = new GroupingCategory();
       catg2.setStartTimesDate(ownStartTimes);
       catg2.setEndTimesDate(ownEndTimes); 
       catg2.setCategoryName("OwnTime");
       catg2.setThreshold(20);
       catg2.setPercentageValue(25);
       markAnalysis.getCategories().add(catg2); */
       
        /*if(!result.hasErrors()){
            //userList.add(user);
            returnText = "User has been added to the list. Total number of users are "  ;//userList.size();
        }else{
            returnText = "Sorry, an error has occur. User has not been added to list.";
        }*/
       
       
        /////////////////////////////////////////////////
        ACPReader acpReader = new ACPReader();
		
		acpReader.parse("Second.txt");
		acpReader.parseMarksTest("example_marks.txt");
		acpReader.setLectureTimes();
		
		Map<String, List<User>> userByCategory = acpReader.getUsageCategory(markAnalysis.getCategories());
		
		// Need to write logic to find this
		int numberOfMarks = 2;
			
		///////////////////////////
		/// The two tests whom u need to plot
		
		String test1Name = markAnalysis.getTest1NameForComparision();
		String test2Name = markAnalysis.getTest2NameForComparision();
		
		//Remove
		test1Name = "Test1";
		test2Name = "Test2";
		
		int test1Index = 0;
		int test2Index = 0;
		
		//To find index of the required Marks for comparison in MarksList
		Entry<String, List<User>> entryTemp = userByCategory.entrySet().iterator().next();
		List<User> userListTemp = (List<User>)entryTemp.getValue(); 
		List<Marks> tempObj = userListTemp.get(0).getMarks();
		for (int i=0;i<tempObj.size();i++) {
			if(tempObj.get(i).getName().equalsIgnoreCase(test1Name)){
				test1Index = i;
			} else if (tempObj.get(i).getName().equalsIgnoreCase(test2Name)){
				test2Index = i;
			}
		}
		
		
		// THe index found above is used to get the difference in marks between required tests.
		// And thats added to a new seriesList.
		List<Series> markComparisionList = new ArrayList<Series>();
		for (Map.Entry<String, List<User>> entry : userByCategory.entrySet())
		{	
			Series series = new Series();
			series.name = entry.getKey().replace('+', ' ');
			List<User> userList = (List<User>)entry.getValue();
			for(User user : userList){
			    Float usageMark[] =  new Float[2];
			    usageMark[0]= user.getMarks().get(test1Index).getMarks();
			    usageMark[1] =user.getMarks().get(test2Index).getMarks();
			    series.data.add(usageMark);
			}			
			markComparisionList.add(series);
		}
		
		
		/////////////////////////////
		
		ParentWrapper test = new ParentWrapper();
		test.credits.put("enabled", "false");
		
		test.chart.put("type", "column") ;
		test.chart.put("zoomType", "x") ;
		
		//test.title.put("text", plotValues.getTitle());
		
		test.xAxis.put("type", "datetime");
		test.xAxis.put("tickmarkPlacement", "between");
		test.xAxis.put("crosshair", "true");
		
		test.yAxis.put("min", 0);
		//test.yAxis.put("title", new HashMap().put("text", plotValues.getyAxisText()));
		
		List plotBandValues = new ArrayList();
		
        test.plotBands.put("plotBands", plotBandValues);
		Series series = new Series();
		series.data = acpReader.getProjectWiseAnalysis();
		
		test.series.add(series);
        ////////////////////////////////////////////////   
        
        
        model.put("wrapp", test);
        model.put("markComparisionList", markComparisionList);
        model.put("userByCategory", userByCategory);
   //     model.put("returnText", returnText);
       // model.put("plotValues", plotValues);
        return model;
    }
    
    //To be removed. Just using temporary!!
	public   void setLectureTimes() {
			
			// 701
			se701startTimes.add(resolveDate	("2015-03-05", "10:00:00")); // T	2
			se701endTimes.add(resolveDate	("2015-03-05", "12:00:00")); // T		
			se701startTimes.add(resolveDate	("2015-03-06", "09:00:00")); // F	2
			se701endTimes.add(resolveDate	("2015-03-06", "11:00:00")); // F
	
			se701startTimes.add(resolveDate	("2015-03-12", "10:00:00")); // T	2
			se701endTimes.add(resolveDate	("2015-03-12", "12:00:00")); // T		
			se701startTimes.add(resolveDate	("2015-03-13", "09:00:00")); // F	2
			se701endTimes.add(resolveDate	("2015-03-13", "11:00:00")); // F
			
			se701startTimes.add(resolveDate	("2015-03-19", "10:00:00")); // T	2
			se701endTimes.add(resolveDate	("2015-03-19", "12:00:00")); // T		
			se701startTimes.add(resolveDate	("2015-03-20", "09:00:00")); // F	2
			se701endTimes.add(resolveDate	("2015-03-20", "11:00:00")); // F
			
			se701startTimes.add(resolveDate	("2015-03-26", "10:00:00")); // T	2
			se701endTimes.add(resolveDate	("2015-03-26", "12:00:00")); // T		
			se701startTimes.add(resolveDate	("2015-03-27", "09:00:00")); // F	2
			se701endTimes.add(resolveDate	("2015-03-27", "11:00:00")); // F	
			se701startTimes.add(resolveDate	("2015-04-02", "10:00:00")); // T	2
			se701endTimes.add(resolveDate	("2015-04-02", "12:00:00")); // F
			
			// 751
			se751startTimes.add(resolveDate	("2015-03-05", "08:00:00")); // T	2
			se751endTimes.add(resolveDate	("2015-03-05", "10:00:00")); // T		
			se751startTimes.add(resolveDate	("2015-03-06", "13:00:00")); // F	2
			se751endTimes.add(resolveDate	("2015-03-06", "15:00:00")); // F
	
			se751startTimes.add(resolveDate	("2015-03-12", "08:00:00")); // T	2
			se751endTimes.add(resolveDate	("2015-03-12", "10:00:00")); // T		
			se751startTimes.add(resolveDate	("2015-03-13", "13:00:00")); // F	2
			se751endTimes.add(resolveDate	("2015-03-13", "15:00:00")); // F
			
			se751startTimes.add(resolveDate	("2015-03-19", "08:00:00")); // T	2
			se751endTimes.add(resolveDate	("2015-03-19", "10:00:00")); // T		
			se751startTimes.add(resolveDate	("2015-03-20", "13:00:00")); // F	2
			se751endTimes.add(resolveDate	("2015-03-20", "15:00:00")); // F
			
			se751startTimes.add(resolveDate	("2015-03-26", "08:00:00")); // T	2
			se751endTimes.add(resolveDate	("2015-03-26", "10:00:00")); // T		
			se751startTimes.add(resolveDate	("2015-03-27", "13:00:00")); // F	2
			se751endTimes.add(resolveDate	("2015-03-27", "15:00:00")); // F
			
			se751startTimes.add(resolveDate	("2015-04-02", "08:00:00")); // T	2
			se751endTimes.add(resolveDate	("2015-04-02", "10:00:00")); // F
			
			lastMinstartTimes.add(resolveDate("2015-04-04", "00:00:00"));
			lastMinendTimes.add(resolveDate	("2015-04-24", "00:00:00"));
			
			//Own times
			ownStartTimes.add(resolveDate	("2015-03-05", "00:00:00")); // T	2
			ownEndTimes.add(resolveDate	("2015-04-02", "00:00:00")); 
			
		}
			private   Date resolveDate(String date, String time) {
				try {
					return date_format.parse(date+" "+time);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				return null;
}
	
	
	
}