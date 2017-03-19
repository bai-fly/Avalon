package extend;

import main.MainServlet;
import tool.Response;

/**
 * Created by Eldath on 2017/1/31 0031.
 *
 * @author Eldath
 */
public class ShowMsgTask implements Task {
    private static String message;

    ShowMsgTask(String input) {
        ShowMsgTask.message = input;
    }

    @Override
    public void run() {
        for (long thisNeedShow : MainServlet.followGroup)
            Response.sendToGroup(thisNeedShow, message);
    }
}