import org.jsoup.nodes.Document;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.lang.*;
import java.util.*;

/** TopHits class that scrapes weekly top hits from 1990-2021 and find all artist that collaborated closely with the given artist
 * Author Clay Lee
 * Date December 16, 2021
 */
public class TopHits{
    /** Map object that stores artist collaboration info and accessed by other methods in class
     */
    protected Map<String, List<String>> artists = new HashMap<String, List<String>>(); //arraylist for artist mapping
    /**TopHits constructor that scrapes and truncate all subpages from top40weekly.com down to 1990-2021 only
     * @throws IOException
     */
    public TopHits() throws IOException{
        Document document = Jsoup.connect("http://top40weekly.com").get();
        Elements links = document.select("a[href]");
        List<String> allLinks = new ArrayList<>();
        int i = 0;
        for (Element link : links) {
            allLinks.add(link.attr("href"));
        }
        List<String> fourDecadesList = new ArrayList<>();
        for(int j = 137; j < 147; j++){//90s URL in string literal
            fourDecadesList.add(allLinks.get(j));
        }
        for(int j = 149; j <= 158; j++){//2000s URL in string literal
            fourDecadesList.add(allLinks.get(j));
        }
        for(int j = 161; j <= 170; j++){//2010s URL in string literal
            fourDecadesList.add(allLinks.get(j));
        }
        for(int j = 173; j < 175; j++){//2020 & 2021 URL in string literal
            fourDecadesList.add(allLinks.get(j));
        }
        for(int j = 0; j < 32; j++){
            Document page = Jsoup.connect(fourDecadesList.get(j)).get();
            Elements pageSections = page.getElementsByTag("p");
            suggest(pageSections);
        }
    }

    /**public suggest function called by user that takes in an artist name and prints out collaborators and their first layer of collaborators
     * @param artistName
     */
    public void suggest(String artistName){
        if(!artists.containsKey(artistName) && ! artists.containsValue(artistName)){
            System.out.println("This artist has yet to make the Top Hits playlist.");
        }
        else if(artists.containsKey(artistName)){//is a main artist in a song
            List<String> collaborators = artists.get(artistName);
            if(collaborators.size() != 0){
                System.out.println(artistName + " has collaborations with: " + collaborators.toString());
                System.out.println("Other recommended artists: ");
                for(int i = 0; i < collaborators.size(); i++){
                    List<String> additional = artists.get(collaborators.get(i));
                    if(additional != null){
                        System.out.println(collaborators.get(i) + " has collaborations with: " + additional.toString());

                    }
                }
            }
        }
    }

    /** private suggest function called by constructor
     * @param docs
     */

    private void suggest(Elements docs){
        String par = docs.text();
        suggestHelper(par);
    }

    /** suggest helper method that parses through HTML text and populates hashmap with artist and their collaborators
     * @param eachWeek
     */

    private void suggestHelper(String eachWeek){
        String[] song = eachWeek.split("–•–"); //parse to Artist first string array
        String[] allSongs = new String[40]; //trunc list
        System.arraycopy(song, 1, allSongs, 0, 40); //trunc list
        for(int i = 0; i < allSongs.length; i++){
            int toRemove = allSongs[i].indexOf("–");//truncate useless info
            int alsoRemove = allSongs[i].indexOf("(");
            if(toRemove != -1){
                allSongs[i] = allSongs[i].substring(0, toRemove);//truc^
            }
            if(alsoRemove < allSongs[i].length() && alsoRemove != -1){
                allSongs[i] = allSongs[i].substring(0, alsoRemove);
            }
            allSongs[i] = allSongs[i].trim();
//            System.out.println(i + ": " + allSongs[i]);
            if(allSongs[i].contains("X") || allSongs[i].contains("&") || allSongs[i].contains(",") || allSongs[i].contains("+") || allSongs[i].contains("featuring") || allSongs[i].contains("Featuring")){
                List<String> additionalArtists = Arrays.asList(allSongs[i].split("featuring|Featuring|X|&|,|\\,"));
                String mainart = additionalArtists.get(0).trim();
                artists.putIfAbsent(mainart, new ArrayList<>());
                for(int j = 1; j < additionalArtists.size(); j++){
                    String nunu = additionalArtists.get(j).trim();
                    if(!artists.get(mainart).contains(nunu)){
                        artists.get(mainart).add(nunu);
                    }
                }
            }
            else{
                if(!artists.containsKey(allSongs[i])) artists.put(allSongs[i], null);
            }
        }
    }



    public static void main(String[] args) throws IOException {
        System.out.println("Give me a minute...");
        System.out.println("While you wait, I've got a question for you: why does Snoop Dogg always use conditioner?");
        System.out.println(" ..fo-frizzles");
        TopHits t = new TopHits();
        t.suggest("Nicki Minaj");

    }
}
