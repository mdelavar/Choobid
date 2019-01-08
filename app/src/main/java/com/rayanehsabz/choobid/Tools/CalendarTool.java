

package com.rayanehsabz.choobid.Tools;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.codec.binary.Base64;

public class CalendarTool {


private static double len = 365.24219879;
private static double base = 2346;

private static double greg_len = 365.2425;
private static double greg_origin_from_jalali_base = 629964;
private static String greg_month_names[]={"","Jan","Feb","Mar","Apr","May","June","Jul","Aug","Sep","Oct","Nov","Dec"};

//-------------------------------------------------------------------
/** Converts specified gregorian date to persian date in form of (yyyy/mm/dd) */
public static String getPersianDate(int gregYear,int gregMonth,int gregDay){
// passed days from Greg orig
double d = Math.ceil((gregYear-1)*greg_len);
// passed days from jalali base
double d_j = d + greg_origin_from_jalali_base + getGregDayOfYear(gregYear,gregMonth,gregDay);

//first result! jalali year
double j_y = Math.ceil(d_j/len)-2346;
// day of the year
double j_days_of_year = Math.floor(((d_j/len) - Math.floor(d_j/len))*365)+1;


System.out.println(j_days_of_year);
StringBuffer result = new StringBuffer();

result.append((int)j_y+"/"+(int)month(j_days_of_year)+"/"+(int)dayOfMonth(j_days_of_year));
return result.toString();
}

/** Converts specified gregorian date to persian date in form of (yyyy/mm/dd) */
public static String getPersianDate(Date d){
GregorianCalendar gc = new GregorianCalendar();
gc.setTime(d);
int year = gc.get(Calendar.YEAR);
return GregorianToJalali(year, (gc.get(Calendar.MONTH)) + 1,
		gc.get(Calendar.DAY_OF_MONTH));
}

/** Returns persian year according to specified gregorian date. */
public static int getPersianYear(Date dt){
GregorianCalendar gc = new GregorianCalendar();
gc.setTime(dt);
int gregYear = gc.get(Calendar.YEAR);
int gregMonth = gc.get(Calendar.MONTH)+1;
int gregDay = gc.get(Calendar.DAY_OF_MONTH);

double d = Math.ceil((gregYear-1)*greg_len);
double d_j = d + greg_origin_from_jalali_base + getGregDayOfYear(gregYear,gregMonth,gregDay);
double j_y = Math.ceil(d_j/len)-2346;
double j_days_of_year = Math.floor(((d_j/len) - Math.floor(d_j/len))*365);
return (int)j_y;
}

/** returns the persian month number according to specified gregorian date (Months:1..12) */
public static int getPersianMonth(Date dt){
	  int[] g_days_in_month = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	  int[] j_days_in_month = {31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29};

	  GregorianCalendar gc = new GregorianCalendar();
	  gc.setTime(dt);
	  int gregYear = gc.get(Calendar.YEAR);
	  int gregMonth = gc.get(Calendar.MONTH)+1;
	  int gregDay = gc.get(Calendar.DAY_OF_MONTH);

	  int gy = gregYear-1600;
	    int gm = gregMonth-1;
	    int gd = gregDay-1;

	        int g_day_no = 365*gy+div(gy+3,4)-div(gy+99,100)+div(gy+399,400);

	        for (int i=0; i < gm; ++i)
	          g_day_no += g_days_in_month[i];
	        if (gm>1 && ((gy%4==0 && gy%100!=0) || (gy%400==0)))
	         // leap and after Feb 
	          g_day_no++;
	        g_day_no += gd;

	       int j_day_no = g_day_no-79;

	        int j_np = div(j_day_no, 12053); //12053 = 365*33 + 32/4 
	        j_day_no = j_day_no % 12053;

	        int jy = 979+33*j_np+4*div(j_day_no,1461); // 1461 = 365*4 + 4/4 

	        j_day_no %= 1461;

	        if (j_day_no >= 366) {
	          jy += div(j_day_no-1, 365);
	          j_day_no = (j_day_no-1)%365;
	        }

	        int j;
	        for (j=0; j < 11 && j_day_no >= j_days_in_month[j]; ++j)
	          j_day_no -= j_days_in_month[j];
	       int jm = j+1;
	       return (int)dayOfMonth(jm);
}

/** Returns day number (1..31) */
public static int getPersianDayOfMonth(Date dt){
	  int[] g_days_in_month = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
	  int[] j_days_in_month = {31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29};

	  GregorianCalendar gc = new GregorianCalendar();
	  gc.setTime(dt);
	  int gregYear = gc.get(Calendar.YEAR);
	  int gregMonth = gc.get(Calendar.MONTH)+1;
	  int gregDay = gc.get(Calendar.DAY_OF_MONTH);

	  int gy = gregYear-1600;
	    int gm = gregMonth-1;
	    int gd = gregDay-1;

	        int g_day_no = 365*gy+div(gy+3,4)-div(gy+99,100)+div(gy+399,400);

	        for (int i=0; i < gm; ++i)
	          g_day_no += g_days_in_month[i];
	        if (gm>1 && ((gy%4==0 && gy%100!=0) || (gy%400==0)))
	         // leap and after Feb 
	          g_day_no++;
	        g_day_no += gd;

	       int j_day_no = g_day_no-79;

	        int j_np = div(j_day_no, 12053); //12053 = 365*33 + 32/4 
	        j_day_no = j_day_no % 12053;

	        int jy = 979+33*j_np+4*div(j_day_no,1461); // 1461 = 365*4 + 4/4 

	        j_day_no %= 1461;

	        if (j_day_no >= 366) {
	          jy += div(j_day_no-1, 365);
	          j_day_no = (j_day_no-1)%365;
	        }

	        int j;
	        for (j=0; j < 11 && j_day_no >= j_days_in_month[j]; ++j)
	          j_day_no -= j_days_in_month[j];
	       int jm = j+1;
	       int jd = j_day_no;
return (int)dayOfMonth(jd);
}

/** Returns Hour : Minute */
public static String getHourMinute(Date dt){
	GregorianCalendar gc = new GregorianCalendar();
	gc.setTime(dt);
	return gc.get(Calendar.HOUR_OF_DAY) + ":" + gc.get(Calendar.MINUTE);
}

/** Returns Hour : Minute : Second*/
public static String getHourMinuteSecond(Date dt){
	GregorianCalendar gc = new GregorianCalendar();
	gc.setTime(dt);
	return gc.get(Calendar.HOUR_OF_DAY) + ":" + gc.get(Calendar.MINUTE) + ":" + gc.get(Calendar.SECOND);
}

//******************************
private static double month(double day){

if(day<=6*31)
return Math.ceil(day/31);
else
return Math.ceil((day-6*31)/30)+6;
}

private static double dayOfMonth(double day){

double m = month(day);
if(m<=6)
return day - 31*(m-1);
else
return day-(6*31)-(m-7)*30;
}

private static double getGregDayOfYear(double year,double month,double day){
int greg_moneths_len[] = {0,31,28,31, 30,31,30, 31,31,30 ,31,30,31};
boolean leap=false;
if(((year%4)==0)&&(((year%400)!=0)))leap = true;
if(leap)greg_moneths_len[2]=29;
int sum=0;
for(int i=0;i<month;i++)
sum+=greg_moneths_len[i];
return sum+day-2;
}

private static String GregorianToJalali(int g_y, int g_m, int g_d)
{

  int[] g_days_in_month = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
  int[] j_days_in_month = {31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29};

    int gy = g_y-1600;
    int gm = g_m-1;
    int gd = g_d-1;

        int g_day_no = 365*gy+div(gy+3,4)-div(gy+99,100)+div(gy+399,400);

        for (int i=0; i < gm; ++i)
          g_day_no += g_days_in_month[i];
        if (gm>1 && ((gy%4==0 && gy%100!=0) || (gy%400==0)))
         // leap and after Feb 
          g_day_no++;
        g_day_no += gd;

       int j_day_no = g_day_no-79;

        int j_np = div(j_day_no, 12053); //12053 = 365*33 + 32/4 
        j_day_no = j_day_no % 12053;

        int jy = 979+33*j_np+4*div(j_day_no,1461); // 1461 = 365*4 + 4/4 

        j_day_no %= 1461;

        if (j_day_no >= 366) {
          jy += div(j_day_no-1, 365);
          j_day_no = (j_day_no-1)%365;
        }

        int j;
        for (j=0; j < 11 && j_day_no >= j_days_in_month[j]; ++j)
          j_day_no -= j_days_in_month[j];
       int jm = j+1;
       int jd = j_day_no+1;


        String Result= jy+"/"+jm+"/"+jd;

        return (Result);
     }  


  private static int div(float a, float b)
  {  
      return (int)(a/b); 
  }


public static String getCoded(String d){
	byte[]   bytesEncoded = Base64.encodeBase64(d.getBytes());
	String encodedEmail = new String(bytesEncoded);
	return encodedEmail;
}

}