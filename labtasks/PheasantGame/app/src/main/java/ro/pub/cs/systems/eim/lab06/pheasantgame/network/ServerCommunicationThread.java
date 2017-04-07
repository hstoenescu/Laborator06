package ro.pub.cs.systems.eim.lab06.pheasantgame.network;

import android.util.Log;
import android.util.StringBuilderPrinter;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Random;

import ro.pub.cs.systems.eim.lab06.pheasantgame.general.Constants;
import ro.pub.cs.systems.eim.lab06.pheasantgame.general.Utilities;

public class ServerCommunicationThread extends Thread {

    private Socket socket;
    private TextView serverHistoryTextView;

    private Random random = new Random();

    private String expectedWordPrefix = new String();

    public ServerCommunicationThread(Socket socket, TextView serverHistoryTextView) {
        if (socket != null) {
            this.socket = socket;
            Log.d(Constants.TAG, "[SERVER] Created communication thread with: " + socket.getInetAddress() + ":" + socket.getLocalPort());
        }
        this.serverHistoryTextView = serverHistoryTextView;
    }

    public void run() {
        try {
            if (socket == null) {
                return;
            }
            boolean isRunning = true;
            BufferedReader requestReader = Utilities.getReader(socket);
            PrintWriter responsePrintWriter = Utilities.getWriter(socket);

            while (isRunning) {

                // TODO exercise 7a
                // if the server received "End Game"
                String word = requestReader.readLine();
                if (word.equals(Constants.END_GAME))
                    break;
                else {
                    // if the word is valid
                    if (Utilities.wordValidation(word) == true) {

                        // verify if the word has the first two chars expected
                        if (word.startsWith(expectedWordPrefix)) {
                            // add the new prefix
                            expectedWordPrefix = word.substring(word.length() - 2);

                            // verify to find the words that respect this rule
                            List<String> listOfWords = Utilities.getWordListStartingWith(expectedWordPrefix);
                            if (!listOfWords.isEmpty()) {
                                String new_word = listOfWords.get(random.nextInt(listOfWords.size()));
                                responsePrintWriter.append(new_word);
                            } else {
                                responsePrintWriter.append(Constants.END_GAME);
                            }

                        }
                    }
                }
            }
            socket.close();
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
    }
}
