import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonParserDemo {

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private static JSONObject readJsonFromUrl() throws IOException, JSONException {
        try (InputStream is = new URL("https://raw.githubusercontent.com/opendatajson/football.json/master/2017-18/en.1.json").openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        }
    }

    private Connection connect() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String connectionURL = "jdbc:mysql://104.197.152.81:3306/Football";
        Connection conn = null;
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        try {
            conn = DriverManager.getConnection(connectionURL, "root", "root");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    private void insert(String DateOfMatch, String HomeTeam, String AwayTeam) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        String sql = "INSERT INTO Fixtures (DateOfMatch, HomeTeam, AwayTeam) VALUES(?,?,?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             pstmt.setString(1, DateOfMatch);
             pstmt.setString(2, HomeTeam);
             pstmt.setString(3, AwayTeam);
             pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException, JSONException, IllegalAccessException, ClassNotFoundException, InstantiationException {

            JSONObject json = readJsonFromUrl();
            JSONArray sportsArray1 = json.getJSONArray("rounds");

            JSONArray jr = new JSONArray();
            for (int i = 0; i < sportsArray1.length(); i++) {
                jr = sportsArray1.getJSONObject(i).getJSONArray("matches");

                String toUpper = "", toUpper1 = "";
                String teamKey1 = "", teamKey2 = "";
                String value = "";

                for (int p = 0; p < jr.length(); p++) {
                    ArrayList<String> places = new ArrayList<>(Arrays.asList("arsenal", "bournemouth", "brightonhovealbion", "burnley", "chelsea", "crystalpalace", "everton",
                            "huddersfieldtown", "leicester", "liverpool", "mancity", "manutd", "newcastle", "southampton",
                            "stoke", "swansea", "tottenham", "watford", "westbrom", "westham"));

                    ArrayList<String> places1 = new ArrayList<>(Arrays.asList("Arsenal", "Bournemouth", "Brightonhovealbion", "Burnley", "Chelsea", "Crystalpalace", "Everton",
                            "Huddersfieldtown", "Leicester", "Liverpool", "Mancity", "Manutd", "Newcastle", "Southampton",
                            "Stoke", "Swansea", "Tottenham", "Watford", "Westbrom", "Westham"));

                    ArrayList<String> places2 = new ArrayList<>(Arrays.asList("Arsenal", "Bournemouth", "Brighton", "Burnley", "Chelsea", "Crystal Palace", "Everton",
                            "Huddersfield", "Leicester", "Liverpool", "Man City", "Man United", "Newcaslte", "Southampton",
                            "Stoke", "Swansea", "Tottenham", "Watford", "West Brom", "West Ham"));

                    JSONObject jb = jr.getJSONObject(p);
                    value = jb.getString("date");

                    JSONObject value1 = jb.getJSONObject("team1");
                    String key = value1.getString("key");

                    JSONObject value2 = jb.getJSONObject("team2");
                    String key1 = value2.getString("key");


                    if (places.contains(key)) {
                        toUpper = key.substring(0, 1).toUpperCase() + key.substring(1);
                        for (int j = 0; j < places1.size(); j++) {
                            if (toUpper.contains(places1.get(j))) {
                                teamKey1 = toUpper.replace(places1.get(j), places2.get(j));
                            }
                        }
                    }

                    if (places.contains(key1)) {
                        toUpper1 = key1.substring(0, 1).toUpperCase() + key1.substring(1);
                        for (int j = 0; j < places1.size(); j++) {
                            if (toUpper1.contains(places1.get(j))) {
                                teamKey2 = toUpper1.replace(places1.get(j), places2.get(j));
                            }
                        }
                    }
                    JsonParserDemo app = new JsonParserDemo();
                    app.insert(value, teamKey1,teamKey2);

                    System.out.println("Date: " + value + " Team 1: " + teamKey1 + " Team 2: " + teamKey2);
                }
            }
        }
    }