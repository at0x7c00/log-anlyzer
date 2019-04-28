package me.huqiao.loganlyzer.querylanguage.exception;

public class ExceptionUtil {

    public static String makeLQLErrorMsg(String lql,String near,String excepted){
        int index = lql.indexOf(near);
        StringBuffer sb = new StringBuffer();
        sb.append("Invalid query string:\r\n")
                .append(lql)
                .append("\r\n");
        for(int i = 0;i<index;i++){
            sb.append(" ");
        }
        sb.append("\\\r\n");
        for(int i = 0;i<index;i++){
            sb.append(" ");
        }
        sb.append(" \\_ ");
        sb.append("this is unbalanced (expect for " + excepted + ")");
        return sb.toString();
    }

    public static String makeLQLErrorMsgForStateMachine(String lql,String near,String excepted){
        int index = lql.indexOf(near);
        StringBuffer sb = new StringBuffer();
        sb.append("Invalid query string:\r\n")
                .append(lql)
                .append("\r\n");
        for(int i = 0;i<index;i++){
            sb.append(" ");
        }
        sb.append("\\\r\n");
        for(int i = 0;i<index;i++){
            sb.append(" ");
        }
        sb.append(" \\_ ");
        sb.append("unexpected character," + excepted);
        return sb.toString();
    }

}
