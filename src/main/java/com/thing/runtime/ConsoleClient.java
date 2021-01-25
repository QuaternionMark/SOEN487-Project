package com.thing.runtime;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConsoleClient {
    enum State {
        MainMenu,
        AlbumRepo,
        HIGH
    }

    private State currState = State.MainMenu;

    private boolean quit;

    public boolean wantsToQuit() {
        return quit;
    }

    public void setQuit(boolean quit) {
        this.quit = quit;
    }

    private Scanner keyboardScanner;

    public ConsoleClient() {
        keyboardScanner = new Scanner(System.in);
    }

    public void quit() {
        keyboardScanner.close();
    }

    public void update() throws IOException {
        System.out.print("\n\n=================\n\n");

        switch (currState) {
            case MainMenu: {
                updateMainMenu();
            }
            break;

            case AlbumRepo: {
                updateAlbumRepo();
            }
            break;
        }
    }

    private void updateMainMenu() {
        System.out.print("Select an option below:\n\n");

        final int ALBUM = 1, ARTIST = 2, QUIT = 3;

        System.out.println(ALBUM + ". Album repository");
        System.out.println(ARTIST + ". Artist repository");
        System.out.println(QUIT + ". Quit application");

        int choice = 0;
        do {
            choice = getInlineInteger("\nChoice");
            switch (choice) {
                case ALBUM: {
                    currState = State.AlbumRepo;
                }
                break;

                case ARTIST: {
                    System.out.println(ARTIST);
                }
                break;

                case QUIT: {
                    setQuit(true);
                }
                break;

                default: {
                    choice = -1;
                    System.out.print("Please select a valid choice.");
                }
            }
        } while (choice < 0);
    }

    private void updateAlbumRepo() throws IOException {
        System.out.print("Select an option below:\n\n");

        final int FIND = 1, LIST = 2, ADD = 3, UPDATE = 4, DELETE = 5, EXIT = 6;

        System.out.println(FIND + ". Find an album");
        System.out.println(LIST + ". List all albums");
        System.out.println(ADD + ". Add an album");
        System.out.println(UPDATE + ". Update an album");
        System.out.println(DELETE + ". Delete an album");
        System.out.println(EXIT + ". Back to main menu");

        int choice = 0;
        do {
            choice = getInlineInteger("\nChoice");
            switch (choice) {
                case FIND: {
                    System.out.println(FIND);
                }
                break;

                case LIST: {
                    System.out.println(LIST);
                }
                break;

                case ADD: {
                    addAlbum();
                }
                break;

                case UPDATE: {
                    System.out.println(UPDATE);
                }
                break;

                case EXIT: {
                    currState = State.MainMenu;
                }
                break;

                default: {
                    choice = -1;
                    System.out.print("Please select a valid choice.");
                }
            }
        } while (choice < 0);
    }

    private void addAlbum() throws IOException {
        System.out.println("Start entering the album's info below:");
        String isrc = getInlineString("ISRC");
        String title = getInlineString("Title");
        int year = getInlineInteger("Release Year");
        String desc = getInlineString("Description (optional)", true);

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://localhost:8080/myapp/album/insert");
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("isrc", isrc));
        params.add(new BasicNameValuePair("title", title));
        params.add(new BasicNameValuePair("year", Integer.toString(year)));
        params.add(new BasicNameValuePair("desc", desc));
        httpPost.setEntity(new UrlEncodedFormEntity(params));

        HttpResponse httpResponse = httpclient.execute(httpPost);
        HttpEntity responseEntity = httpResponse.getEntity();
        System.out.println(responseEntity != null ? EntityUtils.toString(responseEntity) : null);
    }

    private String getInlineString(String msg, boolean optional) {
        String val = "";
        do {
            System.out.print(msg + ": ");
            val = keyboardScanner.nextLine();
        } while (!optional && val.trim().length() == 0);

        return val;
    }

    private String getInlineString(String msg) {
        return getInlineString(msg, false);
    }

    private int getInlineInteger(String msg) {
        Integer uh = null;
        System.out.print(msg + ": ");

        do {
            String val = keyboardScanner.nextLine();

            try {
                uh = Integer.parseInt(val);
            } catch (NumberFormatException ignored) {
                System.out.print("Please enter a valid integer number: ");
            }
        } while (uh == null);

        return uh;
    }
}
