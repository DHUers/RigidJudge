package team.dhuacm.RigidJudge.local;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.channels.FileChannel;

/**
 * Created by wujy on 15-1-25.
 */
class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(Compile.class.getSimpleName());

    public static String getFileContent(File file) {
        String fileContent = "";
        try {
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            byte[] fileBytes = new byte[(int) raf.length()];
            raf.read(fileBytes);
            fileContent = new String(fileBytes);
            fileContent = fileContent.replace("\r\n", "\n");
            if (fileContent.endsWith("\n")) {
                fileContent = fileContent.substring(0, fileContent.length() - 1);
            }
            raf.close();
        } catch (IOException e) {
            logger.error(null, e);
        }
        return fileContent;
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public static void fileTransferCopy(File source, File target) throws IOException {
        FileChannel in = null;
        FileChannel out = null;
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            inStream = new FileInputStream(source);
            outStream = new FileOutputStream(target);
            in = inStream.getChannel();
            out = outStream.getChannel();
            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            logger.error(null, e);
        } finally {
            if (inStream != null) {
                inStream.close();
            }
            if (in != null) {
                in.close();
            }
            if (outStream != null) {
                outStream.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }
}
