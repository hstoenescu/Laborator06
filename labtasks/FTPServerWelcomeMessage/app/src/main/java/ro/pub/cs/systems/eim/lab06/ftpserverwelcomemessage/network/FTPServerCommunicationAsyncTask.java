package ro.pub.cs.systems.eim.lab06.ftpserverwelcomemessage.network;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

import ro.pub.cs.systems.eim.lab06.ftpserverwelcomemessage.general.Constants;
import ro.pub.cs.systems.eim.lab06.ftpserverwelcomemessage.general.Utilities;

public class FTPServerCommunicationAsyncTask extends AsyncTask<String, String, Void> {

    private TextView welcomeMessageTextView;

    public FTPServerCommunicationAsyncTask(TextView welcomeMessageTextView) {
        this.welcomeMessageTextView = welcomeMessageTextView;
    }

    // thead that communicates with the FTP server
    @Override
    protected Void doInBackground(String... params) {
        Socket socket = null;
        try {
            // TODO exercise 4
            // open socket with FTPServerAddress.getText().toString() (taken from param[0]) and port (Constants.FTP_PORT = 21)
            // get the BufferedReader attached to the socket (call to the Utilities.getReader() method)
            // should the line start with Constants.FTP_MULTILINE_STARTCODE = "220-", the welcome message is processed
            // read lines from server while
            // - the value is different from Constants.FTP_MULTILINE_END_CODE1 = "220"
            // - the value does not start with Constants.FTP_MULTILINE_END_CODE2 = "220 "
            // append the line to the welcomeMessageTextView text view content (on the UI thread !!!) - publishProgress(...)
            // close the socket
            socket = new Socket (params[0], Constants.FTP_PORT);
            BufferedReader bufferedReader;
            bufferedReader = Utilities.getReader(socket);

            String line = bufferedReader.readLine();
            if (line.startsWith(Constants.FTP_MULTILINE_START_CODE)) {
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.equals(Constants.FTP_MULTILINE_END_CODE1) | line.startsWith(Constants.FTP_MULTILINE_END_CODE2))
                        break;
                    else
                        publishProgress(line);
                }
                /*while (! line.equals(Constants.FTP_MULTILINE_END_CODE1) || ! line.startsWith(Constants.FTP_MULTILINE_END_CODE2)) {
                    publishProgress(line);
                }*/
            }

        } catch (Exception exception) {
            Log.d(Constants.TAG, exception.getMessage());
            if (Constants.DEBUG) {
                exception.printStackTrace();
            }
        }

        // close the socket
        try {
            if (socket != null)
                socket.close();
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "Exception: " + ioException);
        }

        return null;
    }

    @Override
    protected void onPreExecute() {
        welcomeMessageTextView.setText("");
    }

    @Override
    protected void onProgressUpdate(String... progres) {
        // TODO exercise 4
        // append the progress[0] to the welcomeMessageTextView text view
        welcomeMessageTextView.append(progres[0] + "\n");
    }

    @Override
    protected void onPostExecute(Void result) {}

}
