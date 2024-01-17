package xyz.dimeng.cli;

import picocli.CommandLine;
import xyz.dimeng.cli.command.ConfigCommand;
import xyz.dimeng.cli.command.GenerateCommand;
import xyz.dimeng.cli.command.ListCommand;
import  picocli.CommandLine.Command;
/**
 * 命令执行器
 */
@Command(name = "qxp", mixinStandardHelpOptions = true)
public class CommandExecutor implements Runnable {

    private final CommandLine commandLine;

    {
        commandLine = new CommandLine(this)
                .addSubcommand(new GenerateCommand())
                .addSubcommand(new ConfigCommand())
                .addSubcommand(new ListCommand());
    }

    @Override
    public void run() {
        // 不输入子命令时，给出友好提示
        System.out.println("请输入具体命令，或者输入 --help 查看命令提示");
    }

    /**
     * 执行命令
     *
     * @param args
     * @return
     */
    public Integer doExecute(String[] args) {
        return commandLine.execute(args);
    }
}
