package com.uoa.acpanalysis.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.uoa.acpanalysis.model.Plot1;
import com.uoa.acpanalysis.reader.ACPReader;
import com.uoa.acpanalysis.wrapper.Label;
import com.uoa.acpanalysis.wrapper.ParentWrapper;
import com.uoa.acpanalysis.wrapper.Series;

@Controller
/*@RequestMapping("/firstplot")*/
public class FirstPlotController {

	@RequestMapping(value="/firstplot",method = RequestMethod.GET)
	public String printWelcomefirst(ModelMap model) {

		model.addAttribute("message", "Spring 3 MVC Hello World");
		return "Plot1";

	}

    @RequestMapping(value="/firstplot",method=RequestMethod.POST,produces = "application/json")
    public @ResponseBody HashMap  addUser(@RequestParam String[] plotBandColour, @RequestParam String[] plotBandFrom,@RequestParam String[] plotBandTo,@RequestParam String[] plotBandLabel, @ModelAttribute(value="plot1") Plot1 plotValues, BindingResult result){
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
		if (plotValues.getTimeBand() != 0) {
			acpReader.setDurationFactor(plotValues.getTimeBand());
		}
		acpReader.parse("Second.txt");
		acpReader.setLectureTimes();
		
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
		
		List plotBandValues = new ArrayList();
		for(int i=0; i< plotBandFrom.length ; i++){
			HashMap plotband = new HashMap();
			if (!plotBandColour[i].equalsIgnoreCase("") && null != plotBandColour[i]  ) {
				plotband.put("color", "#"+plotBandColour[i]);
			} else {
				plotband.put("color", "#FCFFC5");
			}
			plotband.put("from", plotBandFrom[i]);
			plotband.put("to", plotBandTo[i]);
			
			//Label for plotband
			Label lab = new Label();
			lab.setText(plotBandLabel[i]);
			HashMap style = new HashMap();
			style.put("color", "blue");
			style.put("fontWeight", "bold");
			
			plotband.put("label", lab);
			plotBandValues.add(plotband);
		}
        test.plotBands.put("plotBands", plotBandValues);
		Series series = new Series();
		series.data = acpReader.getProjectWiseAnalysis();
		
		test.series.add(series);
        ////////////////////////////////////////////////   
        
        
        model.put("wrapp", test);
        model.put("returnText", returnText);
        model.put("plotValues", plotValues);
        return model;
    }
/*
    @RequestMapping(value="/ShowUsers.htm")
    public String showUsers(ModelMap com.uoa.acpanalysis.model){
        com.uoa.acpanalysis.model.addAttribute("Users", userList);
        return "ShowUsers";
    }*/
	
	
	
}