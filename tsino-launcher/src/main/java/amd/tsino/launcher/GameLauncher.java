package amd.tsino.launcher;

import amd.tsino.launcher.version.Library;
import amd.tsino.launcher.version.MinecraftVersion;
import amd.tsino.launcher.version.VersionFiles;
import net.minecraft.launcher.Launcher;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.Path;

import java.io.PrintStream;

public class GameLauncher {

    private static void addLogger(Project proj, PrintStream ps) {
        DefaultLogger logger = new DefaultLogger();
        logger.setOutputPrintStream(ps);
        logger.setErrorPrintStream(ps);
        logger.setMessageOutputLevel(Project.MSG_INFO);
        proj.addBuildListener(logger);
    }

    private static void addClientArgs(Java java, MinecraftVersion version, String sessionID) {
        String[] args = version.getMinecraftArguments().split(" ");
        for (String arg : args) {
            switch (arg) {
                case "${auth_player_name}":
                case "${auth_username}":
                    java.createArg().setValue(Launcher.getInstance().getAuth().getCredentials().getUser());
                    break;
                case "${auth_session}":
                    java.createArg().setValue(sessionID);
                    break;
                case "${version_name}":
                    java.createArg().setValue(version.getID());
                    break;
                case "${game_directory}":
                    java.createArg().setFile(Launcher.getInstance().getWorkDir());
                    break;
                case "${game_assets}":
                    java.createArg().setFile(LauncherUtils.getFile(LauncherConstants.RESOURCES_BASE));
                    break;
                default:
                    java.createArg().setValue(arg);
                    break;
            }
        }
    }

    public static void launchGame(VersionFiles version, String sessionID) throws Exception {
        Project project = new Project();
        project.setBaseDir(Launcher.getInstance().getWorkDir());
        project.init();

        addLogger(project, System.out);
        addLogger(project, Launcher.getInstance().getLog().getPS());

        project.fireBuildStarted();

        try {
            Java javaTask = new Java();
            javaTask.setNewenvironment(true);
            javaTask.setTaskName("client");
            javaTask.setProject(project);
            javaTask.setFork(true);
            javaTask.setFailonerror(true);
            javaTask.setClassname(version.getVersion().getMainClass());

            Commandline.Argument jvmArgs = javaTask.createJvmarg();
            jvmArgs.setLine("-Xms512m -Xmx512m");

            addClientArgs(javaTask, version.getVersion(), sessionID);

            Path classPath = new Path(project);
            classPath.setPath(LauncherUtils.getFile(version.getVersion().getVersionJar()).getAbsolutePath());
            for (Library lib : version.getVersion().getLibraries()) {
                if (!lib.isNative()) {
                    Path libPath = new Path(project);
                    libPath.setPath(lib.getFile().getAbsolutePath());
                    classPath.append(libPath);
                }
            }
            javaTask.setClasspath(classPath);

            Environment.Variable variable = new Environment.Variable();
            variable.setKey("java.library.path");
            variable.setValue(version.getNativesDir().getAbsolutePath());
            javaTask.addSysproperty(variable);

            javaTask.init();
            Launcher.getInstance().getLog().log("%n%s%n", javaTask.getCommandLine().toString());
            int ret = javaTask.executeJava();
            Launcher.getInstance().getLog().log("Return code: " + ret);
            if (ret != 0) {
                throw new BuildException("Return code != 0");
            }
            project.fireBuildFinished(null);
        } catch (BuildException e) {
            project.fireBuildFinished(e);
            throw new Exception("Launch failed.", e);
        }
    }
}