package MyPackage;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Servlet implementation class MyServlet
 */
public class MyServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MyServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String inputData=request.getParameter("userInput");
		
		//APi Key Setup
		String apiKey="c4e4046e8965bf4484a1c2163edd1a82";
		
		//Get City from the user
		String city=request.getParameter("city");		
		
		//Create the URL for the open Weather API request
		String apiUrl="https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;
		
		//URL Integration
		URL url = new URL(apiUrl);
		
		// Create a connection to the web address (URL)
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		//retrieve data from the server using a "GET" request
		connection.setRequestMethod("GET");
        
		// Get data from the network in the form of stream
		InputStream inputStream = connection.getInputStream();

		// Prepare to read the data
		InputStreamReader reader = new InputStreamReader(inputStream);
	
		// We want to store the received data in a text format (as a string)
		StringBuilder responseContent = new StringBuilder();

		// Read data input from the network
		Scanner scanner = new Scanner(reader);

		while (scanner.hasNext()) {
		    // Append each line of data to our storage
		    responseContent.append(scanner.nextLine());
		}
			// At this point, responseContent contains the entire data received from the network

        
		// Close the scanner
		scanner.close();

        
		// Parse the JSON response into structured data
		Gson gson = new Gson(); // Gson is a library for working with JSON data

		JsonObject jsonObject = gson.fromJson(responseContent.toString(), JsonObject.class); // Parse the JSON into a JsonObject

        
		// Extract the date and time information from the JSON response
		// The 'dt' value in the JSON response represents a timestamp in seconds, so we convert it to milliseconds by multiplying by 1000
		long dateTimestamp = jsonObject.get("dt").getAsLong() * 1000;
		// Create a human-readable date and time string from the timestamp
		String date = new Date(dateTimestamp).toString();
     
		// Extract temperature information from the JSON response
		// Get the temperature in Kelvin from the "main" section of the JSON
		double temperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
		// Convert the temperature from Kelvin to Celsius and store it as an integer
		int temperatureCelsius = (int) (temperatureKelvin - 273.15);
     
		// Extract humidity information from the JSON response
		int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();

		// Extract wind speed information from the JSON response
		double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();

		// Extract weather condition information from the JSON response
		String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();

        
		// Set the data as request attributes (for sending to the JSP page)
		// Here, you're associating various pieces of data with attribute names so that they can be accessed in the JSP page.
		request.setAttribute("date", date); // Associate the 'date' attribute with the date data.
		request.setAttribute("city", city); // Associate the 'city' attribute with the city name.
		request.setAttribute("temperature", temperatureCelsius); // Associate the 'temperature' attribute with the temperature in Celsius.
		request.setAttribute("weatherCondition", weatherCondition); // Associate the 'weatherCondition' attribute with the weather condition.
		request.setAttribute("humidity", humidity); // Associate the 'humidity' attribute with the humidity value.
		request.setAttribute("windSpeed", windSpeed); // Associate the 'windSpeed' attribute with the wind speed value.
		request.setAttribute("weatherData", responseContent.toString()); // Associate the 'weatherData' attribute with the complete JSON response data.

		// These attributes can now be accessed and used in the JSP page to display weather information.

        
        connection.disconnect();
        
     // Send the request to the "index.jsp" page for display
        request.getRequestDispatcher("index.jsp").forward(request, response);

	}

}
