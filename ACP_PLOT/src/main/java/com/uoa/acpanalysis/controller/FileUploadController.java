package com.uoa.acpanalysis.controller;
import java.util.ArrayList;
import java.util.List;






import org.springframework.beans.factory.annotation.Autowired;
/*import org.slf4j.Logger;
import org.slf4j.LoggerFactory;*/
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.uoa.acpanalysis.model.Record;
import com.uoa.acpanalysis.model.SessionBean;
import com.uoa.acpanalysis.reader.ACPReader;
 
/**
 * Handles requests for the application file upload requests
 */
@Controller
@SessionAttributes("records")
public class FileUploadController {
 
   /* private static final Logger logger = LoggerFactory
            .getLogger(FileUploadController.class);*/
	
	private List<Record> records = new ArrayList<Record>();
	
	@Autowired
	SessionBean sessionBean;
	
	@RequestMapping(value="/uploadFile",method = RequestMethod.GET)
	public String getUploadPage(ModelMap model) {

		return "uploadMultiple";

	}
 
    /**
     * Upload single file using Spring Controller
     */
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public String uploadFileHandler(@RequestParam("name") String name,
            @RequestParam("file") MultipartFile file) {
 
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                ACPReader acpReader = new ACPReader();
                records = acpReader.parseNew(bytes);
                sessionBean.setRecords(records);;
                               
                return "configuration";
            } catch (Exception e) {
                return "You failed to upload " + name + " => " + e.getMessage();
            }
        }
		return "configuration";
    }
 
    /**
     * Upload multiple file using Spring Controller
     */
    @RequestMapping(value = "/uploadMultipleFile", method = RequestMethod.POST)
    public String uploadMultipleFileHandler(@RequestParam("name") String[] names,
            @RequestParam("file") MultipartFile[] files) {
 
        if (files.length != names.length)
            return "Mandatory information missing";
 
        String message = "";
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            String name = names[i];
            try {
                byte[] bytes = file.getBytes();
                ACPReader acpReader = new ACPReader();
                records = acpReader.parseNew(bytes);
                sessionBean.setRecords(records);
                sessionBean.setTemp(6);
                System.out.println(sessionBean.getTemp());
                               
                return "configuration";
            } catch (Exception e) {
                return "You failed to upload " + name + " => " + e.getMessage();
            }
        }
        return message;
    }
}