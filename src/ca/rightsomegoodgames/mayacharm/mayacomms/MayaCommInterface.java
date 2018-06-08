package ca.rightsomegoodgames.mayacharm.mayacomms;

import com.intellij.notification.Notifications;
import ca.rightsomegoodgames.mayacharm.resources.MayaNotifications;
import ca.rightsomegoodgames.mayacharm.resources.PythonStrings;
import com.intellij.openapi.application.PathManager;

import java.io.*;
import java.net.Socket;
import java.text.MessageFormat;

public class MayaCommInterface {
    public static final String LOG_FILENAME_STRING = "/mayalog%s.txt";

    final private String host;
    final private int port;
    final private String logFilename;

    public MayaCommInterface(String host, int port) {
        this.host = host;
        this.port = port;
        this.logFilename = String.format(LOG_FILENAME_STRING, this.port);
    }

    private File writeFile(String text) {
        File tempFile = null;
        BufferedWriter bw;

        try {
            tempFile = File.createTempFile("MayaCharmTemp", ".py");
            if (!tempFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                tempFile.createNewFile();
            }

            bw = new BufferedWriter(new FileWriter(tempFile));
            bw.write(text);
            bw.close();
            tempFile.deleteOnExit();
        }
        catch (IOException e) {
            Notifications.Bus.notify(MayaNotifications.INSTANCE.getFILE_FAIL());
            e.printStackTrace();
        }
        return tempFile;
    }

    private void sendToPort(File message) {
        Socket client = null;
        PrintWriter out = null;

        try {
            client = new Socket(host, port);
            out = new PrintWriter(client.getOutputStream(), true);

            String outString = MessageFormat.format(
                    PythonStrings.INSTANCE.getEXECFILE(), message.toString().replace("\\", "/"));
            out.println(outString);
        }
        catch (IOException e) {
            Notifications.Bus.notify(MayaNotifications.INSTANCE.getCONNECTION_REFUSED());
            e.printStackTrace();
        }
        finally {
            if (out != null)
                out.close();
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void sendCodeToMaya(String message) {
        File file = writeFile(message);
        sendToPort(file);
    }

    public void sendFileToMaya(String path) {
        File file = new File(path);
        sendToPort(file);
    }

    public void pyDevSetup() {
        sendCodeToMaya(PythonStrings.INSTANCE.getPydevSetupScript());
    }

    public void setTrace(int port, boolean suspend, boolean print) {
        sendCodeToMaya(String.format(PythonStrings.INSTANCE.getSETTRACE(),
                host, port, suspend ? "True" : "False", print ? "True" : "False"));
    }

    public void stopTrace() {
        sendCodeToMaya(PythonStrings.INSTANCE.getSTOPTRACE());
    }

    public void connectMayaLog() {
        final String mayaLogPath = PathManager.getPluginTempPath() + logFilename;
        String message = PythonStrings.INSTANCE.getCLOSE_LOG();
        message += System.lineSeparator() + MessageFormat.format(PythonStrings.INSTANCE.getOPEN_LOG(), mayaLogPath);

        try {
            createMayaLog(mayaLogPath);
            sendCodeToMaya(message);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private File createMayaLog(String path) throws IOException {
        final File mayaLog = new File(path);
        if (!mayaLog.exists()) {
            //noinspection ResultOfMethodCallIgnored
            mayaLog.createNewFile();
        }
        return mayaLog;
    }
}
