package com.ldodds.slug.framework.config;

import java.text.*;
import java.util.*;

/**
 * @author ldodds
 *
 */
public class DateUtils
{
	private static final DateFormat FORMATTER 
		= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		
	public static Date getDate(String date)
	{
		try
		{
			return FORMATTER.parse(date);
		} catch (Exception e)
		{
			return null;
		}
		 
	}
	
	public static String getNow()
	{
		return FORMATTER.format(Calendar.getInstance().getTime());
	}
		
}
