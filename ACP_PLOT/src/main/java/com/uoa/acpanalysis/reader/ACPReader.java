package com.uoa.acpanalysis.reader;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.VisibilityChecker;
import com.uoa.acpanalysis.model.ActionType;
import com.uoa.acpanalysis.model.Marks;
import com.uoa.acpanalysis.model.Record;
import com.uoa.acpanalysis.model.SessionBean;
import com.uoa.acpanalysis.model.User;
import com.uoa.acpanalysis.model.formdata.GroupingCategory;
import com.uoa.acpanalysis.wrapper.Series;

public class ACPReader {

	private  SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private  SimpleDateFormat date_simple_day_format = new SimpleDateFormat("dd MMMM");
	
	private  SimpleDateFormat date_simple_day_format_mod = new SimpleDateFormat("yyyy-MM-dd");
	
	private int numberOfMarks;
	
	@Autowired
	private  ArrayList<Record> records = new ArrayList<Record>();
	
	// TODO remove the new obj.. only for testing added 
	@Autowired
	SessionBean sessionBean = new SessionBean();
	
	// Might not be required. Added for test
	public SessionBean getSessionBean() {
		return sessionBean;
	}

	public void setSessionBean(SessionBean sessionBean) {
		this.sessionBean = sessionBean;
	}

	private  HashMap<String, Float> User_Marks = new HashMap<String, Float>();
	
	public int getDurationFactor() {
		return DurationFactor;
	}

	public void setDurationFactor(int durationFactor) {
		DurationFactor = durationFactor;
	}

	private int DurationFactor = 180000;//86400000;
	
	int course = 701;
	
	public void parse(String fileName) {
		
		try {
			
			ClassLoader classLoader = getClass().getClassLoader();
			File file = new File(classLoader.getResource(fileName).getFile());
			
			
			FileReader fr = new FileReader(file);
			BufferedReader buf = new BufferedReader(fr);
			
			String line = null;
			int count = 0;
			
			
			while ((line = buf.readLine())!= null) {
//			while ((line = buf.readLine())!= null && count < 10) {
				count++;
				
				StringTokenizer st = new StringTokenizer(line, "|");
				
				String univ = st.nextToken();
				
				String user = st.nextToken();
				boolean isStudent = !user.equals("admin") && !user.equals("ngia003") && !user.equals("acp"); 
				
				ActionType type = resolveActionType(st.nextToken());
				String projectLine = st.nextToken();
				
				int course = -1;
				if (projectLine.contains("701" ))
					course = 701;
				if (projectLine.contains("751" ))
					course = 751;
				
				if (course == this.course) {
					
				String projectName = projectLine.substring(0, projectLine.indexOf('('));
				int projectNumber = resolveProjectNumber(projectLine);
				
				int version = Integer.parseInt(st.nextToken());
				
				String dateStr = st.nextToken();
				String timeStr = st.nextToken();
				Date date = resolveDate(dateStr, timeStr);
				
				Record r = new Record(univ, user, course, isStudent, type, projectName, projectNumber, version, date);
				records.add(r);
				
	//				System.out.println(user);
	//				System.out.println(type);
	//				System.out.println(projectName);
	//				System.out.println(projectNumber);
	//				System.out.println(version);
	//				System.out.println(date);
	//				System.out.println(time);
	//				System.out.println();
				}
			}
//			System.out.println("num records: "+ records.size());
			buf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private  int resolveProjectNumber(String s) {
		int projectNumber = -1;
		try {
			projectNumber = Integer.parseInt(s.substring(s.indexOf('(')+1, s.indexOf(')')));
		} catch (NumberFormatException e) {}
		return projectNumber;
	}
	
	private  Date resolveDate(String date, String time) {
		try {
			return date_format.parse(date+" "+time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private  ActionType resolveActionType(String s) {
		switch (s) {
			case "Downloaded":
				return ActionType.DOWNLOAD_PROJECT;
			case "Upload":
				return ActionType.UPLOAD_NEW_PROJECT;
			case "Uploaded new Version":
				return ActionType.UPLOAD_NEW_VERSION;
			case "Synced":
				return ActionType.SYNC;

		default:
			throw new RuntimeException("Unknown ActionType: "+ s);
		}
	}	

	
	public void main(String[] args) {
		parse("Second.txt");
		
		setLectureTimes();
		
		
		//Map<String, float[]> usageVsMarkMap= getUsageVsMarksByStudent();
		List marksVsUsageList = getUsageVsMarksByStudent();
		
		/*ArrayList cp = getProjectWiseAnalysis();
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		mapper.setVisibilityChecker(VisibilityChecker.Std.defaultInstance().withFieldVisibility(Visibility.ANY));
		String json = "";
		try {
			//json = mapper.writeValueAsString(usageVsMarkMap);
			json = mapper.writeValueAsString(marksVsUsageList);
			//json=json.toLowerCase();
			System.out.println(json);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
		//calculateLectureUsage();
		
		//printDailyStudentUsage(701, true);
		//printDailyStudentUsage(701, false);
		//printUniqueDailyStudentUsage(701, false);
		
	//	calculateStudentType(true);
		
		//calculateLectureUsageAfterInstructorUpload();
		lectureSyncFollowers();
		
	//printTimeOfUse(true);
		
		System.out.println("\nDone");
	}
	
	private  void printTimeOfUse(boolean includeLectures) {
//		HashMap<Integer, Integer> hourlyUse = new HashMap<Integer, Integer>();
		int[] hourluUse = new int[24];
		
		for (Record r: records) {
			if (r.course == 701 && r.isStudent && (!withinLecture(r, 701)||includeLectures)) {
				hourluUse[r.date.getHours()]++;
			}
		}
		
		int totalHits = 0;
		
		System.out.println("701 use, includeLectures("+includeLectures+")");
		for (int i = 0; i < 24; i++) {
			System.out.println(i+"  \""+i+"-"+(i+1)+"\""+"  "+hourluUse[i]);
			totalHits+= hourluUse[i];
		}
		System.out.println("Total 701 hits: "+totalHits);
		
		totalHits = 0;
		hourluUse = new int[24];
		for (Record r: records) {
			if (r.course == 751 && r.isStudent && (!withinLecture(r, 751)||includeLectures)) {
				hourluUse[r.date.getHours()]++;
			}
		}
		
		System.out.println("751 use, includeLectures("+includeLectures+")");
		for (int i = 0; i < 24; i++) {
			System.out.println(i+"  \""+i+"-"+(i+1)+"\""+"  "+hourluUse[i]);
			totalHits+=hourluUse[i];
		}
		System.out.println("Total 751 hits: "+totalHits);
		
		
	}
	
	private  boolean withinLecture(Record r, int course) {
		if (course == 701) {
			for (int i = 0; i < se701startTimes.size(); i++) {
				Date start = se701startTimes.get(i);
				Date end = se701endTimes.get(i);
				
				if (withinLecture(r, start, end, false)) {
					return true;
				}
			}
		} else if (course == 751){
			for (int i = 0; i < se751startTimes.size(); i++) {
				Date start = se751startTimes.get(i);
				Date end = se751endTimes.get(i);
				
				if (withinLecture(r, start, end, false)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private  boolean duringLastMinute(Record r, int course) {
		if (course == 701) {
			for (int i = 0; i < lastMinstartTimes.size(); i++) {
				Date start = lastMinstartTimes.get(i);
				Date end = lastMinendTimes.get(i);
				
				if (withinSpecifiedTime(r, start, end, false)) {
					return true;
				}
			}
		} else if (course == 751){
			for (int i = 0; i < lastMinstartTimes.size(); i++) {
				Date start = lastMinstartTimes.get(i);
				Date end = lastMinendTimes.get(i);
				
				if (withinSpecifiedTime(r, start, end, false)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private  void calculateLectureUsageAfterInstructorUpload() {
		
		// key: lectureDay   value: list representing count of followers after each sync of that lecture
		HashMap<String, ArrayList<Integer>> lectureSyncFollowers701 = new HashMap<String, ArrayList<Integer>>();
		HashMap<String, ArrayList<Integer>> lectureSyncFollowers751 = new HashMap<String, ArrayList<Integer>>();
		
//		int numSyncs = 0;
//		ArrayList<Integer> syncs = new ArrayList<Integer>();
		
		// for each record
		for (int rpos = 0; rpos < records.size(); rpos++) {
			
			Record r = records.get(rpos);
			
			// if instructor uploads new version
			if (r.course == 701 && !r.isStudent  && (r.type ==ActionType.DOWNLOAD_PROJECT||r.type ==ActionType.SYNC || r.type==ActionType.UPLOAD_NEW_VERSION || r.type==ActionType.UPLOAD_NEW_PROJECT ) ) {
				
				// for each 701 lecture
				for (int i = 0; i < se701startTimes.size(); i++) {
					Date start = se701startTimes.get(i);
					Date end = se701endTimes.get(i);
					
					if (withinLecture(r, start, end, true)) {
						
						HashSet<String> list = new HashSet<String>();
						
//						numSyncs++;
//						System.out.println(r.type+" "+r.user+"  "+r.date);
						
						// try the next few records after this instructor upload
						for (int nextToCheck = rpos+1 ; (nextToCheck<(rpos+100))&&(nextToCheck<records.size()); nextToCheck++) {
							Record sr = records.get(nextToCheck);
							if (sr.isStudent && sr.course == 701 && withinLecture(sr, start, end, false) && withinFewMin(r.date, 1,sr.date)) {
//							  System.out.println(sr.type+" "+sr.user+"  "+sr.date);
								list.add(sr.user);
							} 
							// if lecturer, this represents a new upload
							if (!sr.isStudent) {
								break;
							}
						}
//						System.out.println(list.size()+" (unique) students following this sync: "+list.size());
//						for (String user: list) {
//							System.out.println("    "+user);
//						}
						
						ArrayList<Integer> lectureSyncs = lectureSyncFollowers701.get(date_simple_day_format.format(r.date));
						if (lectureSyncs == null) {
							lectureSyncs = new ArrayList<Integer>();
							lectureSyncFollowers701.put(date_simple_day_format.format(r.date), lectureSyncs);
						}
						lectureSyncs.add(list.size());
						
						list.clear();
						
						break;
					}
				}
//				for (String day: lectureSyncFollowers701.keySet()) {
//					System.out.println(day+" had "+lectureSyncFollowers701.get(day).size()+"  synces");
//				}
//				System.out.println();
				
			}
			// if instructor uploads new version
			else if (r.course == 751 && !r.isStudent && (r.type ==ActionType.DOWNLOAD_PROJECT||r.type ==ActionType.SYNC)){// && (r.type==ActionType.UPLOAD_NEW_VERSION || r.type==ActionType.UPLOAD_NEW_PROJECT ) ) {
					
					// for each 751 lecture
					for (int i = 0; i < se751startTimes.size(); i++) {
						Date start = se751startTimes.get(i);
						Date end = se751endTimes.get(i);
						
						if (withinLecture(r, start, end, true)) {
							
							HashSet<String> list = new HashSet<String>();
							
//							System.out.println(r.type+" "+r.user+"  "+r.date);
							
							// try the next few records after this instructor upload
							for (int nextToCheck = rpos+1 ; (nextToCheck<(rpos+100))&&(nextToCheck<records.size()); nextToCheck++) {
								Record sr = records.get(nextToCheck);
								if (sr.isStudent && sr.course == 751 && withinLecture(sr, start, end, false) && withinFewMin(r.date, 5,sr.date)) {
//									System.out.println(sr.type+" "+sr.user+"  "+sr.date);
									list.add(sr.user);
								} 
								// if lecturer, this represents a new upload
								if (!sr.isStudent) {
									break;
								}
							}
//							System.out.println(list.size()+" (unique) students following this sync: "+list.size());
//							for (String user: list) {
//								System.out.println("    "+user);
//							}
//							syncs.add(list.size());
							
							ArrayList<Integer> lectureSyncs = lectureSyncFollowers751.get(date_simple_day_format.format(r.date));
							if (lectureSyncs == null) {
								lectureSyncs = new ArrayList<Integer>();
								lectureSyncFollowers751.put(date_simple_day_format.format(r.date), lectureSyncs);
							}
							lectureSyncs.add(list.size());
							
							list.clear();
							break;
						}
					}
//					for (String day: lectureSyncFollowers751.keySet()) {
//						System.out.println(day+" had "+lectureSyncFollowers751.get(day).size()+"  synces");
//					}
//					System.out.println();
					
				}
		}
		int count = 0;
		System.out.println("\n------- 701 --------------- ");
		for (String day: lectureSyncFollowers701.keySet()) {
			System.out.println(day+" had "+lectureSyncFollowers701.get(day).size()+"  synces");
			count+=lectureSyncFollowers701.get(day).size();
		}
		System.out.println("Total syncs: "+count);
		
		count = 0;
		System.out.println("\n------- 751 --------------- ");
		for (String day: lectureSyncFollowers751.keySet()) {
			System.out.println(day+" had "+lectureSyncFollowers751.get(day).size()+"  synces");
			count+= lectureSyncFollowers751.get(day).size();
		}
		System.out.println("Total syncs: "+count);
	}
	
	
	private  void lectureSyncFollowers(){

		String projectName = "701_Source_to_source_DebugOn";
		// key: lectureDay   value: list representing count of followers after each sync of that lecture
		HashMap<String, ArrayList<Integer>> lectureSyncFollowers701 = new HashMap<String, ArrayList<Integer>>();
		HashMap<String, ArrayList<Integer>> lectureSyncFollowers751 = new HashMap<String, ArrayList<Integer>>();
		
//		int numSyncs = 0;
//		ArrayList<Integer> syncs = new ArrayList<Integer>();
		String SEPARATOR = "\t";
		String END_OF_LINE = "\n";
		StringBuilder sb = new StringBuilder();
		// for each record
		for (int rpos = 0; rpos < records.size(); rpos++) {
			
			Record r = records.get(rpos);
			
			// if instructor uploads new version
			if (r.course == 701
					&& !r.isStudent
					&& (r.type == ActionType.DOWNLOAD_PROJECT
							|| r.type == ActionType.SYNC
							|| r.type == ActionType.UPLOAD_NEW_VERSION 
							|| r.type == ActionType.UPLOAD_NEW_PROJECT)
							// Change it to eqIcs
							//&& r.projectName.equalsIgnoreCase(projectName)
							) {
				
				// for each 701 lecture
				for (int i = 0; i < se701startTimes.size(); i++) {
					Date start = se701startTimes.get(i);
					Date end = se701endTimes.get(i);
					
					if (withinLecture(r, start, end, true)) {
						int version=0;
						HashSet<String> list = new HashSet<String>();
						
//						numSyncs++;
//						System.out.println(r.type+" "+r.user+"  "+r.date);
						
						// try the next few records after this instructor upload
						for (int nextToCheck = rpos+1 ; (nextToCheck<(rpos+100))&&(nextToCheck<records.size()); nextToCheck++) {
							Record sr = records.get(nextToCheck);
							if (sr.isStudent
									&& sr.course == 701
									&& withinLecture(sr, start, end, false)
									&& withinFewMin(r.date, 3, sr.date)
									&& sr.projectName.equalsIgnoreCase(r.projectName)
									&& sr.version == r.version) {
								//							  System.out.println(sr.type+" "+sr.user+"  "+sr.date);
								list.add(sr.user);
							} 
							// if lecturer, this represents a new upload
							if (!sr.isStudent) {
								version = r.version;
								break;
							}
						}
						sb.append(r.projectName+" "+"vx"+version);						
						sb.append(SEPARATOR);
						sb.append(list.size());
						sb.append(END_OF_LINE);
						 System.out.println(list.size()+" (unique) students following this sync: "+list.size());
						 System.out.println(" Project Name : "+r.projectName);
						 System.out.println(" Version : "+version);
						 for (String user: list) {
						 System.out.println("    "+user);
											}
						
						ArrayList<Integer> lectureSyncs = lectureSyncFollowers701.get(date_simple_day_format.format(r.date));
						if (lectureSyncs == null) {
							lectureSyncs = new ArrayList<Integer>();
							lectureSyncFollowers701.put(date_simple_day_format.format(r.date), lectureSyncs);
						}
						lectureSyncs.add(list.size());
						
						list.clear();
						
						break;
					}
				}
//				for (String day: lectureSyncFollowers701.keySet()) {
//					System.out.println(day+" had "+lectureSyncFollowers701.get(day).size()+"  synces");
//				}
//				System.out.println();
				
			}
			// if instructor uploads new version

		}
		
		
		int count = 0;
		System.out.println("\n------- 701 --------------- ");
		for (String day: lectureSyncFollowers701.keySet()) {
			System.out.println(day+" had "+lectureSyncFollowers701.get(day).size()+"  synces");
			count+=lectureSyncFollowers701.get(day).size();
		}
		System.out.println("Total syncs: "+count);
		
		count = 0;
		System.out.println("\n------- 751 --------------- ");
		for (String day: lectureSyncFollowers751.keySet()) {
			System.out.println(day+" had "+lectureSyncFollowers751.get(day).size()+"  synces");
			count+= lectureSyncFollowers751.get(day).size();
		}
		System.out.println("Total syncs: "+count);
		System.out.println("String Builder output"+sb.toString());
	}
	
	

	private  boolean withinFewMin(Date firstEvent, int maxMinDiff, Date secondEvent) {
		Calendar cal = new GregorianCalendar();
		cal.setTime(firstEvent);
		cal.add(Calendar.MINUTE, maxMinDiff);
		Calendar cal2 = new GregorianCalendar();
		cal2.setTime(secondEvent);
		return cal.after(cal2);
	}

	public  String calculateLectureUsage() {
		
		HashMap<Date, HashSet<String>> count701 = new HashMap<Date, HashSet<String>>();
		HashMap<Date, HashSet<String>> count751 = new HashMap<Date, HashSet<String>>();
		System.out.println("In Lecture Usage");
		// for each record 
		for (Record r: records) {
			
			if (r.course == 701 && r.isStudent) {
				
				// for each 701 lecture
				for (int i = 0; i < se701startTimes.size(); i++) {
					Date start = se701startTimes.get(i);
					Date end = se701endTimes.get(i);
					
					if (withinLecture(r, start, end, false)) {
						HashSet<String> list;
						
						if (count701.containsKey(start)) {
							list = count701.get(start);
						} else {
							list = new HashSet<String>();
							count701.put(start, list);
						}
						list.add(r.user);						
						break;
					}
				}
				
			} else if (r.course == 751 && r.isStudent){
				
				// for each 751 lecture
				for (int i = 0; i < se751startTimes.size(); i++) {
					Date start = se751startTimes.get(i);
					Date end = se751endTimes.get(i);
					
					if (withinLecture(r, start, end, false)) {
						HashSet<String> list;
						
						if (count751.containsKey(start)) {
							list = count751.get(start);
						} else {
							list = new HashSet<String>();
							count751.put(start, list);
						}
						list.add(r.user);						
						break;
					}
				}
				
			}
			
		}
		
		String valuesForlectureUsage = "";
		
		System.out.println("\n------- 701 --------------- ");
		for (Date start: se701startTimes) {
			if (count701.get(start) != null){
				System.out.println(date_simple_day_format_mod.format(start)+":"+count701.get(start).size());
				valuesForlectureUsage = valuesForlectureUsage+date_simple_day_format_mod.format(start)+":"+count701.get(start).size()+";";
			}	else{
				System.out.println(date_simple_day_format_mod.format(start)+":0");
			//valuesForlectureUsage = valuesForlectureUsage+";"+date_simple_day_format_mod.format(start)+":"+count701.get(start).size();
			}
			}
		System.out.println(valuesForlectureUsage);
		System.out.println("\n------- 751 --------------- ");
		for (Date start: se751startTimes) {
			if (count751.get(start) != null)
				System.out.println(date_simple_day_format_mod.format(start)+":"+count751.get(start).size());
			else
				System.out.println(date_simple_day_format_mod.format(start)+":0");
		}
		
		return valuesForlectureUsage;
	}
	
	private  boolean withinLecture(Record r, Date ls, Date le, boolean startAt5Past) {
		Calendar rec = new GregorianCalendar();
		rec.setTime(r.date);
		
		Calendar start = new GregorianCalendar();
		start.setTime(ls);
		Calendar end = new GregorianCalendar();
		end.setTime(le);
		
		if (startAt5Past)
			start.add(Calendar.MINUTE, 5);
//		end.add(Calendar.MINUTE, 0);
		
		return (rec.before(end) && rec.after(start));
	}
	
	private  boolean withinSpecifiedTime(Record r, Date ls, Date le, boolean startAt5Past) {
		Calendar rec = new GregorianCalendar();
		rec.setTime(r.date);
		
		Calendar start = new GregorianCalendar();
		start.setTime(ls);
		Calendar end = new GregorianCalendar();
		end.setTime(le);
	
		return (rec.before(end) && rec.after(start));
	}

	private  ArrayList<Date> se701startTimes = new ArrayList<Date>();
	private  ArrayList<Date> se701endTimes = new ArrayList<Date>();
	private  ArrayList<Date> se751startTimes = new ArrayList<Date>();
	private  ArrayList<Date> se751endTimes = new ArrayList<Date>();
	
	private  ArrayList<Date> lastMinstartTimes = new ArrayList<Date>();
	private  ArrayList<Date> lastMinendTimes = new ArrayList<Date>();
	
	public  void setLectureTimes() {
		
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
	
	private  void calculateStudentType(boolean uniqueDaysOnly) {
		HashSet<String> students701 = new HashSet<String>();
		HashSet<String> students751 = new HashSet<String>();
		
		for (Record r: records) {
			if (r.isStudent && r.course == 701) {
				students701.add(r.user);
			} else if (r.isStudent) {
				students751.add(r.user);
			}
		}

		HashSet<String> both = new HashSet<String>();
		both.addAll(students701);
		both.retainAll(students751);
		
		System.out.println("Registered 701: "+students701.size());
		System.out.println("Registered 751: "+students751.size());
		System.out.println("Registered Both: "+both.size());
		
		HashMap<String, Integer> count701 = new HashMap<String, Integer>();
		HashMap<String, Integer> count751 = new HashMap<String, Integer>();
		
		if (uniqueDaysOnly) {
			HashSet<String> days = new HashSet<String>();
			
			for (String user: students701) {
				days.clear();
				for (Record r: records) {
					if (r.isStudent && r.user.equals(user) && r.course == 701){
						days.add(date_simple_day_format.format(r.date));
					}
				}
				count701.put(user,days.size());
			}
			for (String user: students751) {
				days.clear();
				for (Record r: records) {
					if (r.isStudent && r.user.equals(user) && r.course == 751){
						days.add(date_simple_day_format.format(r.date));
					}
				}
				count751.put(user,days.size());
			}
		} else {
		
			for (String user: students701) {
				int count = 0;
				for (Record r: records) {
					if (r.isStudent && r.user.equals(user) && r.course == 701){
						count++;
					}
				}
				count701.put(user,count);
			}
			for (String user: students751) {
				int count = 0;
				for (Record r: records) {
					if (r.isStudent && r.user.equals(user) && r.course == 751){
						count++;
					}
				}
				count751.put(user,count);
			}
		}
		

		// registered-701 only (i..e. not registered in 751)
		System.out.println("registered-701 only (i..e. not registered in 751)");
		for (String user: students701) {
			if (!both.contains(user))
				System.out.println("     "+user+", "+count701.get(user));
		}
		
		// registered-751 only (i..e. not registered in 701)
		System.out.println("registered-751 only (i..e. not registered in 701)");
		for (String user: students751) {
			if (!both.contains(user))
				System.out.println("     "+user+", "+count751.get(user));
		}
		
		// registered-both for 701
		System.out.println("Registered-both, 701 usage");
		for (String user: both) {
			System.out.println("     "+user+", "+count701.get(user));
		}
		
		// registered-both for 751
		System.out.println("Registered-both, 751 usage");
		for (String user: both) {
			System.out.println("     "+user+", "+count751.get(user));
		}
		
		System.out.println("701 usage");
		for (Entry<String, Integer> e: count701.entrySet()) {
			System.out.println("   "+e.getKey()+ ", "+e.getValue());
		}
		System.out.println("751 usage");
		for (Entry<String, Integer> e: count751.entrySet()) {
			System.out.println("   "+e.getKey()+ ", "+e.getValue());
		}
	}
	
	public  String printUniqueDailyStudentUsage(int course, boolean onlyInLecture) {
		ArrayList<String> keys = new ArrayList<String>();
		ArrayList<Integer> counts = new ArrayList<Integer>();
		String dailyUsage="";
		ArrayList<String> includedForDay = new ArrayList<String>();
		
		for (Record r: records) {
			if (r.isStudent && r.course == course && (withinLecture(r, course) || !onlyInLecture) ) {
				Date d = r.date;
				String curDay = date_simple_day_format_mod.format(d);
				
				if (keys.contains(curDay)) {
					if (!includedForDay.contains(r.user)) {
						includedForDay.add(r.user);
						counts.set(counts.size()-1, counts.get(counts.size()-1)+1);
					}
				} else {
					includedForDay.clear();
					includedForDay.add(r.user);
					keys.add(curDay);
					counts.add(1);
				}
			}
		}
		
		int count = 0;
		
		for ( int i = 0; i < keys.size(); i++ ) {
//			System.out.println(""+keys.get(i)+ "\t"+ counts.get(i));
			count += counts.get(i);
			System.out.println(i+ " \""+keys.get(i)+ "\"\t"+ counts.get(i));
			dailyUsage=dailyUsage+keys.get(i)+"-"+counts.get(i)+";";
		}
		
		System.out.println("total = "+count);
		return dailyUsage;
	}
	
	
	public String printDailyStudentUsage(int course, boolean withinLectureOnly) {
		ArrayList<String> keys = new ArrayList<String>();
		ArrayList<Integer> counts = new ArrayList<Integer>();
				
		String dailyUsage="";
				
		for (Record r: records) {
			if (r.isStudent && r.course == course && (withinLecture(r, course) || !withinLectureOnly) ) {
				Date d = r.date;
				String curDay = date_simple_day_format_mod.format(d);
				
				if (keys.contains(curDay)) {
					counts.set(counts.size()-1, counts.get(counts.size()-1)+1);
				} else {
					keys.add(curDay);
					counts.add(1);
				}
			}
		}
		int count = 0;
		for ( int i = 0; i < keys.size(); i++ ) {
//			System.out.println(""+keys.get(i)+ "\t"+ counts.get(i));
			count += counts.get(i);
			System.out.println(i+ " \""+keys.get(i)+ "\"\t"+ counts.get(i));
			dailyUsage=dailyUsage+keys.get(i)+"-"+counts.get(i)+";";
		}
		System.out.println("Total = "+ count);
		return dailyUsage;
	}
	
	public  ArrayList<Series> getProjectWiseAnalysis()
	{
		HashMap<String, ArrayList> rec = new HashMap();
		
		Collections.sort(records, new Comparator<Record>() 
		{
			@Override
		    public int compare(Record o1, Record o2) 
		    {
		    	return o1.date.compareTo(o2.date);
		    }
			
		});

		for (Record r : records) {
			if(r.course ==this.course){
				Long data[] = new Long[2];
				data[1] = 1L;
				// Add logic to increse the count to more than 1
				data[0] = r.date.getTime() / DurationFactor;
				data[0] = data[0] * DurationFactor;
				
				//With version of projects
				//String key = r.projectName + r.projectNumber + r.version;
				
				//Without version
				String TempProjName = r.projectName.replace("701_", "");
				String key = TempProjName; //+ r.projectNumber;
				
				if (rec.get(key) != null) {
					rec.get(key).add(data);
				} else {
					ArrayList temp = new ArrayList();
					rec.put(key, temp);
				}
			}
		}
		
		
		///////////////////
		//Logic to combine results
		for(Entry<String, ArrayList> entry : rec.entrySet()){
			String key = entry.getKey();
			ArrayList value = (ArrayList)entry.getValue();	
			entry.setValue(getCombinedResults(value));
		}		
		
		////////////////
				
		ArrayList<Series> seriesList = new ArrayList<Series>();		
		for (Entry<String, ArrayList> entry : rec.entrySet()) {
			Series series = new Series();
		    String key = entry.getKey();
		    Object value = entry.getValue();
		    series.name = entry.getKey();
		    series.data = (ArrayList) value;
		    seriesList.add(series);
		    // ...
		}
		
		
		return seriesList;
	}

	private  ArrayList getCombinedResults(ArrayList value) {
		ArrayList newRecords = new ArrayList();
		int count=0;
		ListIterator itr = value.listIterator();
		Long previousRow[] = null;
		while (itr.hasNext()) {
			Long currentRow[] =null; 
			if(itr.nextIndex()==0){
				currentRow = (Long []) itr.next();
			previousRow = currentRow;	
			newRecords.add(currentRow);
			} else{
				currentRow = (Long []) itr.next();
				if(currentRow[0].longValue()==previousRow[0].longValue()){
					Long[] tempRow = (Long[]) newRecords.get(count);
					tempRow[1] = tempRow[1].longValue()+1;
					newRecords.set(count, tempRow);					
				}else{
					newRecords.add(currentRow);
					count = count+1;
				}
				previousRow = currentRow;
			}		
		}
		return newRecords;
	}
	
	public  List getUsageVsMarksByStudent(){
		
		Map<String, float[]> user = countOccurrences(records);
		//User and his mark
	/*	for(Entry<String, float[]> entry : user.entrySet()){
			float[] val = (float[])entry.getValue();
			System.out.println(entry.getKey()+":"+val[0]);
		}*/
		
		user = parseAndSetMarks("name_marks.txt", user);
		
		List markVsUsageList= new ArrayList();
		
		for(Entry<String, float[]> entry : user.entrySet()){
		    String key = entry.getKey();
		    Object value = entry.getValue();
		    markVsUsageList.add(value);
		}
		System.out.println("Marks : "+markVsUsageList);
		
		return markVsUsageList;
	}
	
	 private  Map<String, float[]> parseAndSetMarks(String file,
			Map<String, float[]> userMap) {
			try {
				FileReader fr = new FileReader(new File(file));
				BufferedReader buf = new BufferedReader(fr);
				
				String line = null;
				int count = 0;				
				
				while ((line = buf.readLine())!= null) {
//				while ((line = buf.readLine())!= null && count < 10) {
					count++;
					
					StringTokenizer st = new StringTokenizer(line, ",");
					
					String user = st.nextToken();

					float marks = Float.parseFloat(st.nextToken());					
					User_Marks.put(user, marks);
					
					float[] temp = userMap.get(user);
					if(temp == null) {
						float[] newVal = new float[2];
						newVal[0]=0;
						newVal[1]=marks;
						userMap.put(user, newVal);
					} else {
						temp[1] = marks;
						userMap.put(user, temp);
					}

				}
//				System.out.println("num records: "+ records.size());
				buf.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
				return userMap;
	}
	 
	public  void parseMarks(String file){

		try {
			FileReader fr = new FileReader(new File(file));
			BufferedReader buf = new BufferedReader(fr);
			
			String line = null;
			int count = 0;				
			
			while ((line = buf.readLine())!= null) {
//			while ((line = buf.readLine())!= null && count < 10) {
				count++;
				
				StringTokenizer st = new StringTokenizer(line, ",");
				
				String user = st.nextToken();

				float marks = Float.parseFloat(st.nextToken());					
				User_Marks.put(user, marks);
			}

			buf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public  Map<String, float []> countOccurrences(ArrayList<Record> records){
	      Map<String, float []> occurrenceMap = new HashMap<String, float []>();

	      for(Record record: records){
	    	  float temp [] = occurrenceMap.get(record.user);
	           if(temp == null){
	        	   temp = new float[2];
	        	   temp[0]=1;	        	   
	                occurrenceMap.put(record.user, temp);
	           } else{
	        	   temp[0] = temp[0]+1;;
	                occurrenceMap.put(record.user, temp);
	           }
	      }
	      return occurrenceMap;
	 }
	
	public  Map<String, User> countOccurrencesByType(List<GroupingCategory> categories){
	      Map<String, User> UserMap = new HashMap<String, User>();

	      for(Record record: records){
	    	  boolean fallsIntoSomeCategory = false;
	    	  for (GroupingCategory category : categories){
	    		 if(checkCategory(record, category)){
	    			 //TODO remove
	    			 //For testing purpose
	    			 
	    			 if(sessionBean.getUsers().get(record.user) == null){
	    				break; 
	    			 }
	    			 
	    			 //User user = sessionBean.getUsers().get(record.user);
	    			 /*if (user == null){
	    				 // TODO Get the user from Session bean
	    				 // Add the details to session bean and also the user Map
	    				 User userTemp = new User();
	    				 userTemp.setUserId(record.user);
	    				 userTemp.categoryWiseUsage.put(category.getCategoryName(), 1);
	    				 UserMap.put(record.user, userTemp);
	    				 
	    			 } else*/ 
	    			 System.out.println("sad : "+sessionBean.getUsers().get(record.user).categoryWiseUsage.get(category.getCategoryName()));
	    			 if(null==sessionBean.getUsers().get(record.user).categoryWiseUsage.get(category.getCategoryName())) {
	    				 sessionBean.getUsers().get(record.user).categoryWiseUsage.put(category.getCategoryName(), 1);

	    			 } else {
	    				Integer countValue =  sessionBean.getUsers().get(record.user).categoryWiseUsage.get(category.getCategoryName());
	    				sessionBean.getUsers().get(record.user).categoryWiseUsage.put(category.getCategoryName(), countValue+1);
	    			 }
	    			 fallsIntoSomeCategory = true;
	    			 break;
	    		 }   		 
	    		 
	    	  }
	    	// if it does not fall into the specified timings add it as a new category
	    	  //TODO remove second part of condition
	    	 if (!fallsIntoSomeCategory && null!=sessionBean.getUsers().get(record.user)){
	    	    		 
	    		 /*	 User user = UserMap.get(record.user);
		    		 if (user == null){
	    				 User userTemp = new User();
	    				 userTemp.setUserId(record.user);
	    				 userTemp.categoryWiseUsage.put("otherTime", 1);
	    				 UserMap.put(record.user, userTemp);

	    			 } else*/ if (null==sessionBean.getUsers().get(record.user).categoryWiseUsage.get("otherTime")) {
	    				 sessionBean.getUsers().get(record.user).categoryWiseUsage.put("otherTime", 1);

	    			 } else {
	    				int countValue =  sessionBean.getUsers().get(record.user).categoryWiseUsage.get("otherTime").intValue();
	    				sessionBean.getUsers().get(record.user).categoryWiseUsage.put("otherTime", countValue+1);
	    			 }
	    	  }
	    	  
	      }
	      return sessionBean.getUsers();
	 }
	
	private boolean checkCategory(Record record, GroupingCategory category) {

		if (course == 701) {
			for (int i = 0; i < category.getStartTimesDate().size(); i++) {
				Date start = category.getStartTimesDate().get(i);
				Date end = category.getEndTimesDate().get(i);
				//TODO remove 701
				
				if (withinSpecifiedTime(record, start, end, false)) {
					if (!category.getCategoryName().equalsIgnoreCase("OwnTime")
							|| !withinLecture(record, 701)) {
						return true;
					}
				}
			}
		}
		return false;
	
	}
	
	public Map<String, List<User>> getUsageCategory(List<GroupingCategory> categories) {
		HashMap<String, List<User>> usersByCategoryMap = new HashMap();
		
		Map<String, User> userMap = countOccurrencesByType(categories);
		
		for (Map.Entry<String, User> entry : userMap.entrySet()) {
			User user = (User) entry.getValue();
			user.setTotalUsage(getTotalUsage(user));
			String categoryKey = "";
			for (GroupingCategory grpCategory : categories){							
				if(null!=user.categoryWiseUsage.get(grpCategory.getCategoryName()) && 
						doesUsageSatisfiesCategory(user.categoryWiseUsage.get(grpCategory.getCategoryName()), grpCategory, user.getTotalUsage())){
					categoryKey = categoryKey+grpCategory.getCategoryName()+"+";
				}				
			}
			if (categoryKey.equalsIgnoreCase("")){
				categoryKey = "others";
			}
			//Add the users to the formed keys which could be Category1, Category2+Category3 etc..
			if(null == usersByCategoryMap.get(categoryKey)){
				List userList = new ArrayList();
				userList.add(user);
				usersByCategoryMap.put(categoryKey, userList);
			} else {
				List userList = usersByCategoryMap.get(categoryKey);
				userList.add(user);
				usersByCategoryMap.put(categoryKey, userList);
			}			
		}
		return usersByCategoryMap;
	}
	
	private int getTotalUsage(User user) {
		int totalUsage =0;
		Map<String, Integer> categoryWiseUsage = user.categoryWiseUsage;
		for (Map.Entry<String, Integer> entry : categoryWiseUsage.entrySet()) {
			Integer value = (Integer) entry.getValue();
			totalUsage = totalUsage+value;				
		}
		return totalUsage;
	}

	/**
	 * If usage > the user entered threshold and greater than
	 * a user specified percentage of total usage, then returns true
	 * @param usage
	 * @param grpCategory
	 * @return
	 */
	private boolean doesUsageSatisfiesCategory(Integer usage,
			GroupingCategory grpCategory, int totalUsage) {
		if (usage >= grpCategory.getThreshold()
				&& (usage >= grpCategory.getPercentageValue()/100 * totalUsage )) {
			return true;
		}
		return false;
	}
	
	//Duplicate Parse - To be removed
public List<Record> parseNew(byte[] bytes) {
		
		try {
			InputStream is = null;			
			BufferedReader buf = null;
			is = new ByteArrayInputStream(bytes);
			buf = new BufferedReader(new InputStreamReader(is));

			String line = null;
			int count = 0;
			
			
			while ((line = buf.readLine())!= null) {
//			while ((line = buf.readLine())!= null && count < 10) {
				count++;
				
				StringTokenizer st = new StringTokenizer(line, "|");
				
				String univ = st.nextToken();
				
				String user = st.nextToken();
				boolean isStudent = !user.equals("admin") && !user.equals("ngia003") && !user.equals("acp"); 
				
				ActionType type = resolveActionType(st.nextToken());
				String projectLine = st.nextToken();
				
				int course = -1;
				if (projectLine.contains("701" ))
					course = 701;
				if (projectLine.contains("751" ))
					course = 751;
				
				if (course == this.course) {
					
				String projectName = projectLine.substring(0, projectLine.indexOf('('));
				int projectNumber = resolveProjectNumber(projectLine);
				
				int version = Integer.parseInt(st.nextToken());
				
				String dateStr = st.nextToken();
				String timeStr = st.nextToken();
				Date date = resolveDate(dateStr, timeStr);
				
				Record r = new Record(univ,user, course, isStudent, type, projectName, projectNumber, version, date);
				records.add(r);
				
	//				System.out.println(user);
	//				System.out.println(type);
	//				System.out.println(projectName);
	//				System.out.println(projectNumber);
	//				System.out.println(version);
	//				System.out.println(date);
	//				System.out.println(time);
	//				System.out.println();
				}
			}
//			System.out.println("num records: "+ records.size());
			buf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return records;
	}

	//Unwanted added for test purpose
	public void parseMarksTest(String fileName) {

		try {
			
			ClassLoader classLoader = getClass().getClassLoader();
			File file = new File(classLoader.getResource(fileName).getFile());

			FileReader fr = new FileReader(file);
			BufferedReader buf = new BufferedReader(fr);

			String line = null;
			int count = 0;
			Map<String, User> users = new HashMap<String, User>();
			while ((line = buf.readLine()) != null) {
				// while ((line = buf.readLine())!= null && count < 10) {
				count++;
				User userObj = new User();
				StringTokenizer st = new StringTokenizer(line, ",");
				String user = st.nextToken();
				userObj.setUserId(user);
				List marks = new ArrayList();
				System.out.println("st.countTokens() :"+st.countTokens());
				System.out.println("st.countTokens() :"+st.countTokens());
				System.out.println("st.countTokens() :"+st.countTokens());
				int numberOfTokens = st.countTokens();
				for (int i = 0; i < numberOfTokens; i++) {
					StringTokenizer st2 = new StringTokenizer(st.nextToken(),
							":");
					Marks mark = new Marks();
					mark.setName(st2.nextToken());
					mark.setMarks(new Float(st2.nextToken()));
					marks.add(mark);
				}
				userObj.setMarks(marks);
				users.put(user, userObj);
			}
			sessionBean.setUsers(users);			
			buf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int getNumberOfMarks() {
		return numberOfMarks;
	}

	public void setNumberOfMarks(int numberOfMarks) {
		this.numberOfMarks = numberOfMarks;
	}
	
}
