import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import com.uoa.acpanalysis.model.Marks;
import com.uoa.acpanalysis.model.SessionBean;
import com.uoa.acpanalysis.model.User;
import com.uoa.acpanalysis.model.formdata.GroupingCategory;
import com.uoa.acpanalysis.reader.ACPReader;
import com.uoa.acpanalysis.wrapper.Series;


public class UsageMarkAnalysisTest {
	
	private static  ArrayList<Date> se701startTimes = new ArrayList<Date>();
	private  static ArrayList<Date> se701endTimes = new ArrayList<Date>();
	private  static ArrayList<Date> se751startTimes = new ArrayList<Date>();
	private  static ArrayList<Date> se751endTimes = new ArrayList<Date>();
	
	private  static ArrayList<Date> lastMinstartTimes = new ArrayList<Date>();
	private  static ArrayList<Date> lastMinendTimes = new ArrayList<Date>();
	
	private static  SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static ACPReader acpReader = new ACPReader();
	
	static SessionBean sessionBean = new SessionBean();

	public static void main(String[] args) {
	
		acpReader.parse("Second.txt");
		setLectureTimes();
		acpReader.parseMarksTest("example_marks.txt");	
		
		List<GroupingCategory> categories = new ArrayList<GroupingCategory>();
		
		GroupingCategory catg1 = new GroupingCategory();
		catg1.setCategoryName("catg1");
		catg1.setPercentageValue(33);
		catg1.setThreshold(20);
		
		catg1.setStartTimesDate(se701startTimes);
		catg1.setEndTimesDate(se701endTimes);
		
		GroupingCategory catg2 = new GroupingCategory();
		catg2.setCategoryName("catg2");
		catg2.setPercentageValue(33);
		catg2.setThreshold(20);
		
		catg2.setStartTimesDate(lastMinstartTimes);
		catg2.setEndTimesDate(lastMinendTimes);
		
		categories.add(catg1);
		categories.add(catg2);
		/*Map<String, User> userMap = acpReader.countOccurrencesByType();
		
		for (Map.Entry<String, User> entry : userMap.entrySet())
		{	
			User user = (User)entry.getValue();
		    System.out.println(entry.getKey() + "/" + "Actively Used: "+user.getActivelyUsed()+ "/" + "LastMin Used: "+user.getLastMinUsage()+ "/" + "Own time Used: "+user.getOwnTimeUsed());
		}*/
		
		Map<String, List<User>> userByCategory = acpReader.getUsageCategory(categories);
		
		for (Map.Entry<String, List<User>> entry : userByCategory.entrySet())
		{	
			List<User> userList = (List<User>)entry.getValue();
			System.out.println("###"+entry.getKey()+"\n");
			for(User user : userList){
			    System.out.println(user.getUserId() + "/" + user.getTotalUsage());
			    for (Entry<String, Integer> entry2 : user.categoryWiseUsage.entrySet())
				{	
					System.out.print(entry2.getKey()+":"+entry2.getValue());
					System.out.print(" ");
				}
			    System.out.println("");
			}
		}
		int configParameter =0;
		// Need to write logic to find this
		int numberOfMarks = 2;
		List<List<Series>> listOfSeriesList = new ArrayList<List<Series>>();		
		for (int i = 0; i < numberOfMarks; i++) {
			List<Series> seriesList = new ArrayList<Series>();
			for (Map.Entry<String, List<User>> entry : userByCategory.entrySet())
			{	
				Series series = new Series();
				series.name = entry.getKey().replace('+', ' ');
				List<User> userList = (List<User>)entry.getValue();
				for(User user : userList){
				    Float usageMark[] =  new Float[2];
				    usageMark[0]= (float) user.getTotalUsage();
				    usageMark[1] = user.getMarks().get(i).getMarks();
				    series.data.add(usageMark);
				}			
				seriesList.add(series);
			}
			listOfSeriesList.add(seriesList);
		}
		
System.out.println("");		
		
		
	}

public static  void setLectureTimes() {
		
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
		
	}
		private static  Date resolveDate(String date, String time) {
			try {
				return date_format.parse(date+" "+time);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return null;
}

}
