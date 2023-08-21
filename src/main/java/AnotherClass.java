

import com.google.gson.*;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class AnotherClass {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println();
        Human human = new Human(42, "Test User");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(human);


        String url = "https://jsonplaceholder.typicode.com/posts";

        String url1 = "https://jsonplaceholder.typicode.com/posts/1";
        String url2 = "https://jsonplaceholder.typicode.com/users";
        String url3 = "https://jsonplaceholder.typicode.com/users/1/posts";
        String url4 ="https://jsonplaceholder.typicode.com/posts/10/comments";
        String url5 = "https://jsonplaceholder.typicode.com/users/1/todos";


        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-type","application/json; charset=UTF-8")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        HttpRequest request1 = HttpRequest.newBuilder()
                .uri(URI.create(url1))
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-type","application/json; charset=UTF-8")
                .build();
        HttpResponse<String> response1 = client.send(request1, HttpResponse.BodyHandlers.ofString());
        System.out.println(response1.body());

        HttpRequest request2 = HttpRequest.newBuilder()
                .uri(URI.create(url1))
                .DELETE()
                .header("Content-type","application/json; charset=UTF-8")
                .build();
        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
        System.out.println(response2.statusCode());
        //Отримати всіх юзерів
        try {
            Document document = Jsoup.connect(url2).ignoreContentType(true).get();
            String jsonResponse = document.body().text();
            Gson gson1 = new Gson();
            JsonArray usersArray = gson1.fromJson(jsonResponse, JsonArray.class);
            for (JsonElement userElement : usersArray) {
                JsonObject userObject = userElement.getAsJsonObject();
                String name = userObject.get("name").getAsString();
                String email = userObject.get("email").getAsString();
                int id = userObject.get("id").getAsInt();

                System.out.println("Name: " + name);
                System.out.println("Email: " + email);
                System.out.println("id:" + id);
                System.out.println();
            }

            System.out.println(getById(9, document).get("name").getAsString());
            System.out.println(getByUsername("Bret", document).get("name").getAsString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        getComments(url3, url4);
        printTasks(url5);
    }

    //Отримати юзера за id
    public static JsonObject getById(int id, Document document) {

        JsonObject user = null;
        String jsonResponse = document.body().text();
        Gson gson1 = new Gson();
        JsonArray usersArray = gson1.fromJson(jsonResponse, JsonArray.class);
        for (JsonElement userElement : usersArray) {
            JsonObject userObject = userElement.getAsJsonObject();
            int idRecieved = userObject.get("id").getAsInt();
            if (idRecieved == id) {
                user = userObject;
            }

        }
        return user;
    }

    //Отримати юзера за username
    public static JsonObject getByUsername(String username, Document document) {

        JsonObject user = null;
        String jsonResponse = document.body().text();
        Gson gson1 = new Gson();
        JsonArray usersArray = gson1.fromJson(jsonResponse, JsonArray.class);
        for (JsonElement userElement : usersArray) {
            JsonObject userObject = userElement.getAsJsonObject();
            String usernameRecieved = userObject.get("username").getAsString();
            if (username.equals(usernameRecieved)) {
                user = userObject;
            }

        }
        return user;
    }

    //Коментарі
    public static void getComments(String url1, String url2) throws IOException {
        Document document = Jsoup.connect(url1).ignoreContentType(true).get();
        String jsonResponse = document.body().text();
        Gson gson1 = new Gson();
        JsonArray usersArray = gson1.fromJson(jsonResponse, JsonArray.class);
        JsonObject user = usersArray.get(0).getAsJsonObject();
        for (JsonElement userElement : usersArray) {
            JsonObject userObject = userElement.getAsJsonObject();
            int id = userObject.get("id").getAsInt();
            user = user.get("id").getAsInt()>id ? user:userObject;
        }

        Document docComments = Jsoup.connect(url2).ignoreContentType(true).get();
        String jsonComments = docComments.body().text();
        Gson gsonC = new Gson();
        JsonArray commentsArray = gsonC.fromJson(jsonComments, JsonArray.class);
        String file = "user-" +user.get("userId").getAsInt()+"-post-"+user.get("id").getAsInt()+"-comments.json";
        FileWriter writer = new FileWriter(file);
        for(JsonElement comment:commentsArray){
            int id = comment.getAsJsonObject().get("postId").getAsInt();
            if(user.get("id").getAsInt() == id){
                String body = comment.getAsJsonObject().get("body").getAsString();
                System.out.println(body);
                writer.write(new Gson().toJson(comment));

            }
        }
        writer.flush();
        writer.close();
    }

    //Виводити задачі
    public static void printTasks(String url) throws IOException {
        Document document = Jsoup.connect(url)
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .get();
        String jsonResponse = document.body().text();
        System.out.println(jsonResponse);
        Gson gson1 = new Gson();
        JsonArray usersArray = gson1.fromJson(jsonResponse, JsonArray.class);
        for(JsonElement element:usersArray){
            JsonObject userObject = element.getAsJsonObject();
            Boolean task = userObject.get("completed").getAsBoolean();
            if(!task){
                System.out.println("task not finished, task id:"+userObject.get("id").getAsInt());
            }
        }
    }
}




