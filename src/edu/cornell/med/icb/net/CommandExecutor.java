package edu.cornell.med.icb.net;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * @author Fabien Campagne
 *         Date: 1/22/13
 *         Time: 9:48 AM
 */
public class CommandExecutor {
    private static final org.apache.log4j.Logger LOG = Logger.getLogger(CommandExecutor.class);

    private String remoteServer;
    private String username;
    boolean quiet;
    final boolean local;

    /**
     * Turn the executor silent. Silent executor do not copy the process outputs.
     *
     * @param quiet
     */
    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }

    /**
     * Construct a command executor with username and remote server.
     *
     * @param username     user for login on remote server.
     * @param remoteServer Name of remote server.
     */
    public CommandExecutor(String username, String remoteServer) {
        this.remoteServer = remoteServer;
        this.username = username;
        this.local = false;
    }

    /**
     * Construct a command executor for local commands.
     *
     */
    public CommandExecutor() {
        this.local = true;
    }

    /**
     * Remote copy files from remote server.
     *
     * @param remotePath    the path on the remote server
     * @param localFilename the local destination path.
     * @return scpFromRemote return status.
     * @throws IOException
     * @throws InterruptedException
     */
    public int scpFromRemote(String remotePath, String localFilename) throws IOException, InterruptedException {
        if (this.local)
            throw new IOException("This instance of CommandExecutor can be used only for local commands");
        return exec(String.format("scp -o StrictHostKeyChecking=no %s@%s:%s %s", username, remoteServer, remotePath, localFilename));
    }

    /**
     * Remote copy dirs from remote server.
     *
     * @param remotePath    the path on the remote server
     * @param localPath the local destination path.
     * @return scpFromRemote return status.
     * @throws IOException
     * @throws InterruptedException
     */
    public int scpDirFromRemote(String remotePath, String localPath) throws IOException, InterruptedException {
        if (this.local)
            throw new IOException("This instance of CommandExecutor can be used only for local commands");

        return exec(String.format("scp -r -o StrictHostKeyChecking=no %s@%s:%s %s", username, remoteServer, remotePath, localPath));
    }

    /**
     * Remote copy files from remote server.
     *
     * @param remotePath    the path on the remote server
     * @param localFilename the local destination path.
     * @return scpFromRemote return status.
     * @throws IOException
     * @throws InterruptedException
     */
    public int scpToRemote(String localFilename, String remotePath) throws IOException, InterruptedException {
        if (this.local)
            throw new IOException("This instance of CommandExecutor can be used only for local commands");

        return exec(String.format("scp -o StrictHostKeyChecking=no %s %s@%s:%s", localFilename, username, remoteServer, remotePath));
    }



    /**
     * Remote copy a directory from remote server.
     *
     * @param remotePath    the path on the remote server
     * @param localPath the local destination path.
     * @return scpFromRemote return status.
     * @throws IOException
     * @throws InterruptedException
     */
    public int scpDirToRemote(String localPath, String remotePath) throws IOException, InterruptedException {
        if (this.local)
            throw new IOException("This instance of CommandExecutor can be used only for local commands");

        return exec(String.format("scp -r -o StrictHostKeyChecking=no %s %s@%s:%s", localPath, username, remoteServer, remotePath));
    }

    /**
     * Execute a command on the remote server.
     *
     * @param command Command line to execute.
     * @param envp    variables in the format key=value
     * @return ssh return status.
     * @throws IOException
     * @throws InterruptedException
     */
    public int ssh(String command, String... envp) throws IOException, InterruptedException {
        if (this.local)
            throw new IOException("This instance of CommandExecutor can be used only for local commands");

        // if envp has any variable definitions, we need to send these variables to the remote host:
        StringBuilder exportStatement = new StringBuilder();
        boolean hasExports=false;
        if (envp.length > 0) {
            hasExports=true;
            exportStatement.append("bash -c \'");
            for (String varAssignment : envp) {
                //String variableName = varAssignment.split("=")[0];
                exportStatement.append("export ");
                exportStatement.append(varAssignment);
            }
        }

        final String []statement = String.format("ssh -o StrictHostKeyChecking=no %s@%s",
                username, remoteServer).split(" ");
        if (hasExports) {
            exportStatement.append(" ; ");
        }
        exportStatement.append(command);
        if (hasExports) {
            exportStatement.append('\'');
        }
        String middle=exportStatement.toString();

        ObjectArrayList<String> commands=new ObjectArrayList<String>();
        commands.addAll(new ObjectArrayList<String>(statement));
        commands.add(middle);

        LOG.debug(commands.toString());
        return exec(commands.toArray(new String[commands.size()]), envp);
    }

    /**
     * Execute a local command.
     * @param command
     * @param envp
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public int exec(String command, String... envp) throws IOException, InterruptedException {
        return exec(command, Runtime.getRuntime(), command.split(" "), envp);
    }

    /**
     * Execute a list of local commands .
     * @param commands
     * @param envp
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public int exec(String[] commands, String... envp) throws IOException, InterruptedException {
        return exec(ObjectArrayList.wrap(commands).toString(), Runtime.getRuntime(), commands, envp);
    }

    private int exec(String command, Runtime rt, String[] commands, String[] envp) throws IOException, InterruptedException {
        Process pr = rt.exec(commands, envp);
        if (LOG.isTraceEnabled()) LOG.trace("executing command: " + command);
        new Thread(new SyncPipe(quiet, pr.getErrorStream(), System.err, LOG)).start();
        new Thread(new SyncPipe(quiet, pr.getInputStream(), System.out, LOG)).start();

        int exitVal = pr.waitFor();
        if (LOG.isTraceEnabled()) LOG.trace("Remote command  exited with error code " + exitVal);
        return exitVal;
    }

}
