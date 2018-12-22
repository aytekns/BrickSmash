package com.android.bricksmash;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;

public class XmlLevelParser 
{
	// I don't use name-spaces
    private final static String strNS = null;
    
	public static List<BrickXmlData> readLevel(String strFileName, Context oContext) throws XmlPullParserException, IOException
	{
		List<BrickXmlData> lstBrickList = new ArrayList<BrickXmlData>();
		XmlPullParserFactory oPullParserFactory = null;
		XmlPullParser xmlParser = null;
		InputStream in_stream = null;
		try
		{
			oPullParserFactory = XmlPullParserFactory.newInstance();
			xmlParser = oPullParserFactory.newPullParser();
			in_stream = oContext.getAssets().open(strFileName);
			
			xmlParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			xmlParser.setInput(in_stream, null);
			xmlParser.nextTag();
			
			xmlParser.require(XmlPullParser.START_TAG, strNS, "Bricks");
		    while (xmlParser.next() != XmlPullParser.END_TAG) 
		    {
		        if (xmlParser.getEventType() != XmlPullParser.START_TAG)
		            continue;
		        
		        String strTagName = xmlParser.getName();
		        // Starts by looking for the brick tag
		        if (strTagName.equals("Brick")) 
		        {
		            lstBrickList.add(readBrick(xmlParser));
		        } 
		        else 
		        {
		            skipTag(xmlParser);
		        }
		    }
		}
		catch(XmlPullParserException xml_ex)
		{
			xml_ex.printStackTrace();
		}
		catch(IOException io_ex)
		{
			io_ex.printStackTrace();
		}
		finally
		{
			if (in_stream != null)
				in_stream.close();
		}
		
		return lstBrickList;
	}
	
	private static BrickXmlData readBrick(XmlPullParser xmlParser) throws XmlPullParserException, IOException
	{
		BrickXmlData oBrick = null;
		xmlParser.require(XmlPullParser.START_TAG, strNS, "Brick");
		String strTagName = xmlParser.getName();
		if (strTagName.equals("Brick"))
		{
			int nRow	 		= Integer.parseInt(xmlParser.getAttributeValue(null, "row"));
			int nColumn			= Integer.parseInt(xmlParser.getAttributeValue(null, "col"));
			int nColor			= Integer.parseInt(xmlParser.getAttributeValue(null, "color"));
			int nStrength		= Integer.parseInt(xmlParser.getAttributeValue(null, "strength"));
			int nContent		= Integer.parseInt(xmlParser.getAttributeValue(null, "content"));
			boolean bExplosive 	= Boolean.parseBoolean(xmlParser.getAttributeValue(null, "explosive"));
			
			oBrick = new BrickXmlData(nRow, nColumn, nColor, nStrength, nContent, bExplosive);
			xmlParser.nextTag();
		}
		xmlParser.require(XmlPullParser.END_TAG, strNS, "Brick");
		return oBrick;
	}
	
	private static void skipTag(XmlPullParser xmlParser) throws XmlPullParserException, IOException
	{
		if (xmlParser.getEventType() != XmlPullParser.START_TAG) 
		{
	        throw new IllegalStateException();
	    }
		
	    int depth = 1;
	    while (depth != 0) 
	    {
	        switch (xmlParser.next()) 
	        {
		        case XmlPullParser.END_TAG:
		            depth--;
		            break;
		        case XmlPullParser.START_TAG:
		            depth++;
		            break;
	        }
	    }
	}
}
