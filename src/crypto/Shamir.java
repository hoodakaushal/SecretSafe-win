package crypto;

import com.tiemens.secretshare.main.cli.MainCombine;
import com.tiemens.secretshare.main.cli.MainSplit;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hooda on 2/3/2015.
 */
public class Shamir {

    public static ArrayList<String> shamirSplit(String inputString, int numPieces, int minPieces) {

        ArrayList<String> parts = new ArrayList<>();
        String[] splitArgs = {"-n", Integer.toString(numPieces), "-k", Integer.toString(minPieces), "-sS", inputString, "-primeNone"};
        MainSplit.SplitInput splitInput = MainSplit.SplitInput.parse(splitArgs);
        MainSplit.SplitOutput splitOutput = splitInput.output();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        splitOutput.print(ps);
        String content = baos.toString(); // e.g. ISO-8859-1
        BufferedReader reader = new BufferedReader(new StringReader(content));
        String line;
        int i = 0;
        try {
            while ((line = reader.readLine()) != null && i < numPieces) {
                if (line.startsWith("Share (x")) {
                    i++;
                    parts.add(line.trim());
                }
            }
        } catch (Exception e) {
            //TODO Catch
        }
        return parts;
    }

    public static String shamirCombine(ArrayList<String> parts, ArrayList<String> flags, int k) {
        ArrayList<String> args = new ArrayList<>();
        args.add("-primeNone");
        args.add("-k");
        args.add(Integer.toString(k));
//        for (int i = 0; i < flags.size(); i++) {
//            args.add(flags.get(i));
//            args.add(parts.get(i));
//        }

        for(int i = 0; i < k; i++){
            String part = parts.get(i);
            String partSecret = part.split("=")[1].trim();
            String partNum = part.split("=")[0].split(":")[1].split("\\)")[0].trim();
            System.out.println("part ".concat(partSecret));
            args.add("-s".concat(partNum));
            args.add(partSecret);
        }

        for (int i = 0; i < args.size(); i++) {
            System.out.println(args.get(i));
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        String[] combineArgs = args.toArray(new String[args.size()]);
        MainCombine.CombineInput combineInput = MainCombine.CombineInput.parse(combineArgs, null, ps);
        MainCombine.CombineOutput combineOutput = combineInput.output();
        combineOutput.print(ps);
        String content = baos.toString(); // e.g. ISO-8859-1
        Pattern pattern = Pattern.compile("secret.string = '");
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return (content.substring(matcher.end(), content.length() - 3));
        } else {
            return "";
        }
    }


}

