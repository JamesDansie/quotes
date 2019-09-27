/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package quotes;

import com.google.gson.Gson;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

public class App {
    Quote[] quotes;


    public App() throws FileNotFoundException {
        getData();
    }

    public void getData() throws FileNotFoundException {
        Gson gson = new Gson();
        this.quotes = gson.fromJson(new FileReader("src/main/resources/recentquotes.json"), Quote[].class);
    }

    public Quote getOfflineQuote() throws FileNotFoundException {
        getData();
        int randomIndex = randInt(0, this.quotes.length);

        //checking that there is an author and a quote to return. If not keep looping
        while(this.quotes[randomIndex].author == null || this.quotes[randomIndex].text == null){
            randomIndex = randInt(0, this.quotes.length);
        }
        return this.quotes[randomIndex];
    }

    //copied from; https://stackoverflow.com/questions/363681/how-do-i-generate-random-integers-within-a-specific-range-in-java
    public int randInt(int min, int max){
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    //Shamelessly copied from; https://github.com/codefellows/seattle-java-401d6/blob/master/class-09/internetdemo/src/main/java/internetdemo/App.java
    //Thanks Michelle!
    public String getRonSwansonAPI() throws IOException {
//        URL jqueryUrl = new URL("https://api.forismatic.com/api/jsonp/");
//        URL jqueryUrl = new URL("http://swquotesapi.digitaljedi.dk/api/SWQuote/RandomStarWarsQuote");
//        URL jqueryUrl = new URL("http://numbersapi.com/12/12/date");
//        URL jqueryUrl = new URL("https://api.forismatic.com/api/1.0/?method=getQuote&format=json&lang=en");
        URL jqueryUrl = new URL("http://ron-swanson-quotes.herokuapp.com/v2/quotes");


        HttpURLConnection connection = (HttpURLConnection) jqueryUrl.openConnection();
        // this line of code actually takes like half a second
        // james says, "gross"
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder data = new StringBuilder();
        String line = reader.readLine();
        while(line != null) {
            data.append(line);
            line = reader.readLine();
        }
        return data.toString();
    }

    public Quote swansonConverter(String quote){
        //Gson to read the swanson json
        Gson gson = new Gson();
        String[] swanQuote = gson.fromJson(quote, String[].class);

        //Converting swanson quote to normal quote
        Quote newQuote = new Quote();
        newQuote.author = "Ron Swanson";
        newQuote.text = swanQuote[0];
        return newQuote;
    }

    public boolean uniqueQuote(Quote newQuote) throws FileNotFoundException {
        //imports the data to update this.quotes
        getData();
        for(int i = 0; i < this.quotes.length; i++){
            //checking if new quote text equals old quote text
            if(newQuote.text.equals(this.quotes[i].text)){
                return false;
            }
        }
        return true;
    }

    public void addToQuotes(Quote newQuote){
        //storing old quotes
        Quote[] oldQuotes = this.quotes;

        //making a new empty array with with one extra spot
        this.quotes = new Quote[oldQuotes.length + 1];
        for(int i = 0; i < oldQuotes.length; i++){
            this.quotes[i] = oldQuotes[i];
        }
        this.quotes[this.quotes.length-1] = newQuote;
    }

    public void storeQuotes() throws IOException {
        Gson gson = new Gson();
        FileWriter quoteWriter = new FileWriter("src/main/resources/recentquotes.json");
        gson.toJson(this.quotes, quoteWriter);
        quoteWriter.close();
    }

    public Quote swanson() throws IOException {
        String quote = getRonSwansonAPI();
        Quote swanQuote = swansonConverter(quote);

        System.out.println("old last quote was" + this.quotes[this.quotes.length-1].toString());

        if(uniqueQuote(swanQuote)){
            System.out.println("Unique!");
            addToQuotes(swanQuote);
            System.out.println("new last quote is" + this.quotes[this.quotes.length-1].toString());
            storeQuotes();
        }else{
            System.out.println("Not Unique");
        }

        return swanQuote;
    }

    public static void main(String[] args) throws FileNotFoundException {
        App quoteApp = new App();

        try {
            System.out.println("*******************************");
            System.out.println("Made it to try");
            System.out.println(quoteApp.swanson());
        } catch (IOException e){
            System.out.println("*******************************");
            System.out.println("Made it to catch");
            System.out.println(quoteApp.getOfflineQuote());
            System.out.println(e);
        }
    }
}
