package com.fnproject.fn.runtime;

import com.fnproject.fn.api.exception.FunctionInputHandlingException;
import com.fnproject.fn.runtime.exception.FunctionInitializationException;
import com.fnproject.fn.runtime.ntv.UnixServerSocket;
import com.fnproject.fn.runtime.ntv.UnixSocket;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Map;
import java.util.Random;

public class SocketDelegate implements org.crac.Resource {
    private static final String FN_LISTENER = "FN_LISTENER";
    private final Map<String, String> env;
    public File socketFile;
    public UnixServerSocket socket;

    public File tempFile;

    public SocketDelegate(Map<String, String> env) {
        this.env = env;
        org.crac.Core.getGlobalContext().register(this);
    }

    @Override
    public void beforeCheckpoint(org.crac.Context<? extends org.crac.Resource> context) throws Exception {
        System.out.println("Checkpointing");
    }

    @Override
    public void afterRestore(org.crac.Context<? extends org.crac.Resource> context) throws Exception {
        System.out.println("Restoring");
    }


    private String getRequiredEnv(String name) {
        String val = env.get(name);
        if (val == null) {
            throw new FunctionInputHandlingException("Required environment variable " + name + " is not set - are you running a function outside of fn run?");
        }
        return val;
    }
    public void openSocket() {
        String listenerAddress = getRequiredEnv(FN_LISTENER);

        if (!listenerAddress.startsWith("unix:/")) {
            throw new FunctionInitializationException("Invalid listener address - it should start with unix:/ :'" + listenerAddress + "'");
        }
        String listenerFile = listenerAddress.substring("unix:".length());

        socketFile = new File(listenerFile);


        UnixServerSocket serverSocket = null;
        File listenerDir = socketFile.getParentFile();
        tempFile = new File(listenerDir, randomString() + ".sock");
        try {

            serverSocket = UnixServerSocket.listen(tempFile.getAbsolutePath(), 1);
            // Adjust socket permissions and move file
            Files.setPosixFilePermissions(tempFile.toPath(), PosixFilePermissions.fromString("rw-rw-rw-"));
            Files.createSymbolicLink(socketFile.toPath(), tempFile.toPath().getFileName());

            socket = serverSocket;
        } catch (IOException e) {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException ignored) {
                }

            }
            throw new FunctionInitializationException("Unable to bind to unix socket in " + socketFile, e);
        }
    }

    private void dump(String save) {
        try (PrintWriter aa = new PrintWriter("/cracdata/aaa")) {
            aa.write(save);

            try {
               socket.close();
               finish();
            } catch (Exception e) {

                e.printStackTrace(aa);
            }
        }
        catch (Exception e) {

        }

        try {
            org.crac.Core.checkpointRestore();
        } catch (Exception e) {

            try (PrintWriter aa = new PrintWriter("/cracdata/bbb")) {
                e.printStackTrace(aa);
            }
            catch (Exception e2) {
            }
        }

        openSocket();
    }

    private String randomString() {
        int leftLimit = 97;
        int rightLimit = 122;
        int targetStringLength = 10;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int)
                    (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

    public void finish() {
        socketFile.delete();
        tempFile.delete();
    }
    int qt = 0;
    public UnixSocket accept() throws IOException {
        if (qt==5) {
            dump("Save");
        }
        UnixSocket socketData = socket.accept(100);
        qt++;
        return socketData;
    }
}
