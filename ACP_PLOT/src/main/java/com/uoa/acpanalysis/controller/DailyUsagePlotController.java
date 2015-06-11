package com.uoa.acpanalysis.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uoa.acpanalysis.model.DailyUsagePlot;
import com.uoa.acpanalysis.model.Record;
import com.uoa.acpanalysis.model.SessionBean;
import com.uoa.acpanalysis.reader.ACPReader;
import com.uoa.acpanalysis.wrapper.ParentWrapper;

@Controller
public class DailyUsagePlotController {
	
	@Autowired
	SessionBean sessionBean;

	@RequestMapping(value="/dailyUsage",method = RequestMethod.GET)
	public String getDailyUsagePage(ModelMap model) {

		model.addAttribute("message", "Spring 3 MVC Hello World");
		
		return "dailyUsage";

	}

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value="/dailyUsage",method=RequestMethod.POST,produces = "application/json")
    public @ResponseBody HashMap  getDailyStudentUsage(@RequestParam String[] plotBandFrom,@RequestParam String[] plotBandTo,@RequestParam String[] plotBandLabel, @ModelAttribute(value="plot1") DailyUsagePlot plotValues, BindingResult result){
        @SuppressWarnings("rawtypes")
		HashMap model = new HashMap();
    	String returnText;
        String rtest ="hey";
        if(!result.hasErrors()){
            //userList.add(user);
            returnText = "User has been added to the list. Total number of users are "  ;//userList.size();
        }else{
            returnText = "Sorry, an error has occur. User has not been added to list.";
        }
        /////////////////////////////////////////////////
        ACPReader acpReader = new ACPReader();
      		
		//Should be moved to an home page flow and added to session
		acpReader.parse("Second.txt");
		acpReader.setLectureTimes();
		String dailyUsage = null;
		String dailyUsageLecture = null;
		if(plotValues.getChartType().equalsIgnoreCase("1")){
			dailyUsage = acpReader.printDailyStudentUsage(701, false);
			dailyUsageLecture = acpReader.printDailyStudentUsage(701, true);
		} else {
			dailyUsage = acpReader.printUniqueDailyStudentUsage(701, false);
			dailyUsageLecture = acpReader.printUniqueDailyStudentUsage(701, true);
		}
			
		ParentWrapper test = new ParentWrapper();
		test.credits.put("enabled", "false");
		
		test.chart.put("type", "column") ;
		test.chart.put("zoomType", "x") ;
		
		test.title.put("text", plotValues.getTitle());
		
		test.xAxis.put("type", "datetime");
		test.xAxis.put("tickmarkPlacement", "between");
		test.xAxis.put("crosshair", "true");
		
		test.yAxis.put("min", 0);
		test.yAxis.put("title", new HashMap().put("text", plotValues.getyAxisText())); 
		
		model.put("wrapp", test);
        model.put("returnText", returnText);
        model.put("plotValues", plotValues);
        
        model.put("dailyUsage", dailyUsage);
        model.put("dailyUsageLecture", dailyUsageLecture);
		
		return model;
    }
	
}