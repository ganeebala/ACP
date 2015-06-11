import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.uoa.acpanalysis.model.User;
import com.uoa.acpanalysis.reader.ACPReader;


public class ACPReaderTest {

	public static void main(String[] args) {
		ACPReader acpReader = new ACPReader();
		acpReader.parse("Second.txt");
		acpReader.setLectureTimes();
		
		/*Map<String, User> userMap = acpReader.countOccurrencesByType();
		
		for (Map.Entry<String, User> entry : userMap.entrySet())
		{	
			User user = (User)entry.getValue();
		    System.out.println(entry.getKey() + "/" + "Actively Used: "+user.getActivelyUsed()+ "/" + "LastMin Used: "+user.getLastMinUsage()+ "/" + "Own time Used: "+user.getOwnTimeUsed());
		}*/
		
		//Map<String, List<User>> userByCategory = acpReader.getUsageCategory();
		
		/*for (Map.Entry<String, List<User>> entry : userByCategory.entrySet())
		{	
			List<User> userList = (List<User>)entry.getValue();
			System.out.println("###"+entry.getKey()+"/n");
			for(User user : userList){
			    System.out.println(entry.getKey() + "/" + "Actively Used: "+user.getActivelyUsed()+ "/" + "LastMin Used: "+user.getLastMinUsage()+ "/" + "Own time Used: "+user.getOwnTimeUsed());
			}
		}*/
		
		
	}

}
