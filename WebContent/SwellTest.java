package com.surfbuddy;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import com.mysql.jdbc.jdbc2.optional.*;
import java.sql.*;
import java.util.List;
import javax.sql.*;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

//imports as per site
//http://www.java2s.com/Code/Java/Database-SQL-JDBC/Setupmysqldatasource.htm
//
/**
 * Root resource (exposed at "swelltest" path)
 */
@Path("swelltest")
public class SwellTest {

    /**
     * Method handling HTTP GET requests. The returned object will be sent to
     * the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String gotIt() {
        String spotsAndDaysWithSwell = "Spots and days with swell > 1,5 :";
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUser("surfbuddyadmin");
        dataSource.setPassword("vegRoti2018");
        dataSource.setServerName("41.185.103.214");
        dataSource.setPort(3306);
        dataSource.setDatabaseName("surfbuddy");
        try {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT spot, hr_d FROM forecastdata WHERE SWELL1>1.5");
            // ResultSet rs = stmt.executeQuery("SELECT spot, hr_d FROM surfbuddy.forecastdata");
            while (rs.next()) {
                String spot = rs.getString(1);
                int dayOfMonth = rs.getInt(2);
                spotsAndDaysWithSwell += "\n" + spot + " day of this month: " + dayOfMonth;
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            //nothing
            spotsAndDaysWithSwell = "Error";
        }

        return spotsAndDaysWithSwell;
    }

    /**
     * Method handling HTTP GET requests. The returned object will be sent to
     * the client as "text/plain" media type.
     *NB: THIS METHOD TAKES key AS A PATH PARAM
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Path("/{key}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response anything(@PathParam("key") final String key) {
        String text = getSpotByID(Integer.parseInt(key));
        return Response.ok("Perfecto! Found key\n" + text).build();
    }

    /**
     * Method must take in: userID, userForecastID and return days when Forecast for
     * specific spot meets userForecastRequirements
     * NB THIS METHOD TAKES THE PARAMS AS QUERY PARAMS (eg swelltest/cookingDays?UserID=2&userForecastID=1
     */
    @GET
    @Path("/cookingDays")
    @Produces("text/plain")
    public String getCookingDays(
            @DefaultValue("All") @QueryParam(value = "userID") final String userID,
            @DefaultValue("All") @QueryParam(value = "userForecastID") final String userForecastID) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUser("root");
        dataSource.setPassword("LemonShunk1995");
        dataSource.setServerName("localhost");
        dataSource.setPort(3306);
        dataSource.setDatabaseName("surfbuddy");
        try 
        {
            Connection conn = dataSource.getConnection();
            //return the conditions set for minimum swell and period by user for the specified spot
            PreparedStatement stmt = conn.prepareStatement("SELECT swellMin, periodMin, ufrName FROM userforecastrequirements WHERE userID = ? AND ufrID = ?");
            stmt.setString(1, userID);
            stmt.setString(2, userForecastID);
            double swellMin=0;
            int periodMin=0;
            String forecastName="";
            ResultSet rs = stmt.executeQuery();
            String response="";
            while (rs.next())
            {
                swellMin = rs.getDouble(1);
                periodMin = rs.getInt(2);
                forecastName = rs.getString(3);
                response += "You asked for all days for spot: " + forecastName + " with swell greater than " + swellMin + " and period greater than " + periodMin + "\n";
            }
            //return days that the forecast for the given spot matches the requirements for swell and period specified by the user
            PreparedStatement stmt2 = conn.prepareStatement("SELECT hr_d FROM forecastdata WHERE SWELL1>? AND SWPER1>?");
            stmt2.setDouble(1, swellMin);
            stmt2.setDouble(2, periodMin);
            ResultSet daysOnResult = stmt2.executeQuery();
            String daysOn = "Days where " + forecastName + " is working:\n";
            int dayOfMonth;
            while (daysOnResult.next()) {
                dayOfMonth = daysOnResult.getInt(1);
                daysOn += "\n" + dayOfMonth;
            }
            daysOnResult.close();
            stmt2.close();
            rs.close();
            stmt.close();
            conn.close();
            
            return "" + response + daysOn;

        } 
        catch (Exception e) 
        {
            return "Error: " + e.toString();
        }
    }

    /**
     * Return all days where Donkey has swell > 2m and period > 12 seconds
     */
    @GET
    @Path("/donkey")
    @Produces(MediaType.TEXT_PLAIN)
    public String donkeyDays() {
        String spotsAndDaysWithSwell = "Days Donkey has swell>2m and period>12s :\n";
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUser("root");
        dataSource.setPassword("LemonShunk1995");
        dataSource.setServerName("localhost");
        dataSource.setPort(3306);
        dataSource.setDatabaseName("surfbuddy");
        try {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT hr_d, HTSGW, PERPW FROM forecastdata WHERE id_spot = 208269 AND HTSGW>2 AND PERPW > 12");
            while (rs.next()) {
                String day = rs.getString(1);
                float swell = rs.getFloat(2);
                int period = rs.getInt(3);
                spotsAndDaysWithSwell += " " + day + " swell height: " + swell + " period: " + period + "\n";
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            //nothing
            spotsAndDaysWithSwell = "Error on donkey page";
        }

        return spotsAndDaysWithSwell;
    }

    public String getSpotByID(int id) {
        String resp = "";
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUser("root");
        dataSource.setPassword("LemonShunk1995");
        dataSource.setServerName("localhost");
        dataSource.setPort(3306);
        dataSource.setDatabaseName("surfbuddy");
        try {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT spot, hr_d FROM forecastdata WHERE id_spot=" + id);
            while (rs.next()) {
                String spot = rs.getString(1);
                int dayOfMonth = rs.getInt(2);
                resp += " " + spot + " day of this month: " + dayOfMonth + "\n";
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            //nothing
            resp = "Error";
        }
        return resp;
    }
}
