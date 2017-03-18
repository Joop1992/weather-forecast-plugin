/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package joop.student.hu.nl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

@ActionID(
        category = "View",
        id = "joop.student.hu.nl.WeatherForecast"
)
@ActionRegistration(
        iconBase = "joop/student/hu/nl/rsz_weather-icon.png",
        displayName = "#CTL_WeatherForecast"
)
@ActionReference(path = "Menu/Tools", position = 0)
@Messages("CTL_WeatherForecast=WeatherForecast")
public final class WeatherForecast implements ActionListener {
    public static final int forecastsPerDay = 8;
    public static final int offsetToFindAfternoonForecast = -3;

    @Override
    public void actionPerformed(ActionEvent e) {
         int msgType = NotifyDescriptor.INFORMATION_MESSAGE;
         String msg = getWeatherForecast();
         NotifyDescriptor d = new NotifyDescriptor.Message(msg, msgType);
         DialogDisplayer.getDefault().notify(d);
    }
    
    public static void main(String[] args){
        new WeatherForecast().getWeatherForecast();
    }

    public String getWeatherForecast(){
        String str = "http://api.openweathermap.org/data/2.5/forecast?q=Utrecht,NL&units=metric&appid=2e3d33f283c7f8440004fdd1d6a244ab";

        try {
            URL url = new URL(str);

            URLConnection urlc = url.openConnection();

            BufferedReader bfr = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
            String json = "";
            
            String line;
            while ((line = bfr.readLine()) != null) {
                json += line;
            }
            return constructWeatherForecastFromJsonResponse(json, 1);
        }catch(Exception e){

        }
        return "Something went wrong";
    }

    private String constructWeatherForecastFromJsonResponse(String response, int daysInTheFuture){
        Gson gson = new GsonBuilder().create();
        JsonObject job = gson.fromJson(response, JsonObject.class);
        JsonObject j = gson.fromJson(job.getAsJsonArray("list").get(offsetToFindAfternoonForecast + forecastsPerDay *
                daysInTheFuture).getAsJsonObject().toString(), JsonObject.class);
        String temperature = j.getAsJsonObject("main").get("temp").toString();
        String humidity = j.getAsJsonObject("main").get("humidity").toString();
        String overallForecast = j.getAsJsonArray("weather").get(0).getAsJsonObject().get("description").toString()
                .replaceAll("\"", "");
        return overallForecast + ". With an average temperature of " + temperature + " degrees Celsius and a " +
                "humidity level of "+ humidity + " percent";
    }
}
