package webservices;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


public class HttpGet {
	
	
	public String getweather(String lat, String longit) throws MalformedURLException, IOException {
	String url = "http://api.worldweatheronline.com/free/v1/weather.ashx";
	String charset = "UTF-8";
	String key = "faae919b44caa17304d676c846c34783de947a4b";
	String result = "error";
	
	

	url = url + "?key="+key+"&q="+lat+"%2C%20"+longit+"&format=json";
	
	URLConnection connection = new URL(url).openConnection();
	connection.setRequestProperty("Accept-Charset", charset);
	InputStream response = connection.getInputStream();

	result = convertInputStreamToString(response);
	return result;
	}
	
	private String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
 
        inputStream.close();
        return result;
 
    }
}
