
import java.io.*;
import java.util.*;
public class MyUtils{

    public static PrintWriter out;

    public static void init(){
        try {
            FileWriter outFile = new FileWriter("YUNOWORK.log");
            out = new PrintWriter(outFile, true);
        } catch (IOException x) {
        }
    }
    public static void log(Object... messages){
        StringBuilder bld = new StringBuilder(new Date(System.currentTimeMillis()).toString()).append(" : ");
        for(Object obj : messages){
            bld.append(obj);
        }
        out.println(bld);
    }

}
