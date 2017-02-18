package api;

import util.GroupMessage;

import java.util.regex.Pattern;

/**
 * Created by Eldath on 2017/1/28 0028.
 *
 * @author Eldath
 */
public abstract class GroupMessageAPI implements BaseAPI {
    abstract void doPost(GroupMessage message);

    public abstract String getHelpMessage();

    public abstract Pattern getKeyWordRegex();
}