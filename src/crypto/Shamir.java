package crypto;

import com.tiemens.secretshare.main.cli.MainCombine;
import com.tiemens.secretshare.main.cli.MainSplit;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.min;
import static java.lang.Integer.toString;
import static java.util.Arrays.copyOfRange;

/**
 * Created by hooda on 2/3/2015.
 */
public class Shamir {

    //The encoding that will be used when splitting and combining files.
    static String encoding = "ISO-8859-1";

    public static ArrayList<String> shamirSplit(String inputString, int numPieces, int minPieces) {
        return shamirSplit(inputString, numPieces, minPieces, 0);
    }

    public static ArrayList<String> shamirSplit(String inputString, int numPieces, int minPieces, int mode) {

        String type = "-sS";
        if (mode == 1) {
            type = "-sN";
        }

        ArrayList<String> parts = new ArrayList<>();
        String[] splitArgs = {"-n", Integer.toString(numPieces), "-k", Integer.toString(minPieces), type, inputString, "-primeNone"};
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

        for (int i = 0; i < k; i++) {
            String part = parts.get(i);
            String partSecret = part.split("=")[1].trim();
            String partNum = part.split("=")[0].split(":")[1].split("\\)")[0].trim();
            args.add("-s".concat(partNum));
            args.add(partSecret);
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

    public static String shamirCombineInt(ArrayList<String> parts, ArrayList<Integer> partNums, ArrayList<String> flags, int k) {
        ArrayList<String> args = new ArrayList<>();
        args.add("-primeNone");
        args.add("-k");
        args.add(Integer.toString(k));

        for (int i = 0; i < k; i++) {
            String partSecret = parts.get(i);
            String partNum = partNums.get(i).toString();
            args.add("-s".concat(partNum));
            args.add(partSecret);
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

    /**
     * Splits the given file into numPieces, of which at least minPieces are needed to recover the original.
     * @param filePath Path to the file to be encrypted.
     * @param numPieces Number of files to split into.
     * @param minPieces Minimum splitted files needed to recover original.
     * @return
     * @throws IOException
     */
    public static ArrayList<FileOutputStream> fileSplit(String filePath, int numPieces, int minPieces) throws IOException {

        //Create files to which encrypted pieces will b written.
        ArrayList<FileOutputStream> splitFiles = new ArrayList<>(numPieces);
        for (int i = 0; i < numPieces; i++) {
//            File temp = File.createTempFile(file.getName(), Integer.toString(i));
            //TODO
            FileOutputStream temp = new FileOutputStream("E://".concat("dummy.txt.".concat(Integer.toString(i + 1))));
            splitFiles.add(i, temp);
        }

        //Get the file as a byte array.
        byte[] fileAsBytes = Files.readAllBytes(Paths.get(filePath));
        System.out.println("File had ".concat(Integer.toString(fileAsBytes.length)));

        //Do the encryption.
        for (int i = 0; i < fileAsBytes.length; ) {

            //We want to partition the byte array into pieces of length 4/8/16 whatever, but if length is not multiple (eg there are 15 bytes)
            //then the last piece should be shorter. j takes care of that.
            int j = min(fileAsBytes.length - i, 96);
            byte[] piece = copyOfRange(fileAsBytes, i, i + j);
            i = i + j;

            String pieceAsString = "$".concat(new String(piece, encoding));

            ArrayList<String> splitPieces = Shamir.shamirSplit(pieceAsString.toString(), numPieces, minPieces, 0);
//            assert(Shamir.shamirCombineInt(splitPieces, null, minPieces).equals(pieceAsInt.toString()));

            Shamir.writeBytesToFiles(splitPieces, splitFiles);

        }

        for (FileOutputStream f : splitFiles) {
            f.close();
        }


        //TESTING CODE. TODO remove
        System.out.println("testing the decryption");
        ArrayList<String> files = new ArrayList<>();
        files.add("E://dummy.txt.1");
        files.add("E://dummy.txt.2");
        files.add("E://dummy.txt.3");
        Shamir.fileCombine(files, minPieces);


        return splitFiles;
    }

    public static void writeBytesToFiles(ArrayList<String> shamirOutput, ArrayList<FileOutputStream> files) throws IOException {
        for (int i = 0; i < shamirOutput.size(); i++) {
            String partSecret = shamirOutput.get(i).split("=")[1].trim();
//            System.out.println(shamirOutput.get(i));

            byte[] toWrite = (new BigInteger(partSecret)).toByteArray();
            assert(toWrite.length<=255);
//            System.out.println(toWrite.length);
            System.out.println(toWrite.length);
            files.get(i).write((byte) (toWrite.length));
            files.get(i).write(toWrite);
        }
    }


    public static void fileCombine(ArrayList<String> files, int k) throws IOException {

        ArrayList<byte[]> filesAsBytes = new ArrayList<>(files.size());
        ArrayList<Integer> partNums = new ArrayList<>(files.size());
        for (int i = 0; i < files.size(); i++) {
            filesAsBytes.add(i, Files.readAllBytes(Paths.get(files.get(i))));
            partNums.add(i, Integer.parseInt(files.get(i).substring(files.get(i).lastIndexOf(".") + 1, files.get(i).length())));
        }

        ArrayList<ArrayList<BigInteger>> filesAsInts = new ArrayList<>();
        for (int i = 0; i < filesAsBytes.size(); i++) {
            ArrayList<BigInteger> temp = new ArrayList<>();
            for (int j = 0; j < filesAsBytes.get(i).length; ) {


                int bytesToRead = (int) filesAsBytes.get(i)[j] ;
//                System.out.println("Bytes to read : ".concat(Integer.toString(bytesToRead)));
                assert (bytesToRead >= 0);
                j = j + 1;
//                System.out.print(i);
//                System.out.print(j);
//                System.out.println(bytesToRead);
                BigInteger bigInteger = new BigInteger(copyOfRange(filesAsBytes.get(i), j, j + bytesToRead));
//                System.out.println("Read : ".concat(bigInteger.toString()));
                j += bytesToRead;
                temp.add(bigInteger);
            }
            filesAsInts.add(i, temp);
        }

        ArrayList<String> decryptedStrings = new ArrayList<>(filesAsInts.get(0).size());
        for (int i = 0; i < filesAsInts.get(0).size(); i++) {
//            System.out.println(i);
            ArrayList<String> intsAsStrings = new ArrayList<>();
            for (int j = 0; j < filesAsInts.size(); j++) {
                intsAsStrings.add(filesAsInts.get(j).get(i).toString());
            }
            String decrypted = Shamir.shamirCombineInt(intsAsStrings, partNums, null, k);
            decryptedStrings.add(i, (decrypted.substring(1,decrypted.length())));
        }

        FileOutputStream fileOutputStream = new FileOutputStream(files.get(0).substring(0, files.get(0).length()-2));
        for (int i = 0; i < decryptedStrings.size(); i++) {
            fileOutputStream.write(decryptedStrings.get(i).getBytes(encoding));
        }
        fileOutputStream.close();
        System.out.println("File decrypted!");
    }


}

