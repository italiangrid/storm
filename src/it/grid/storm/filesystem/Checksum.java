package it.grid.storm.filesystem;

import it.grid.storm.config.Configuration;
import it.grid.storm.ea.StormEA;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Checksum extends Thread {

    public enum ChecksumType {
        CRC32("CRC32"), ADLER32("Adler32"), MD2("MD2"), MD5("MD5"), SHA_1("SHA-1"), SHA_256("SHA-256"), SHA_384(
                "SHA-384"), SHA_512("SHA-512");

        private final String value;

        ChecksumType(String value) {
            this.value = value;
        }

        public String toString() {
            return value;
        }
    }

    private static final int MAX_BUFFER_SIZE = 1024 * 8;

    private static final BlockingQueue<String> queue = new LinkedBlockingQueue<String>();

    private static final Logger log = LoggerFactory.getLogger(Checksum.class);
    private static Checksum instance = null;
    private ChecksumType checksumType = null;

    private Checksum() {

        String configChecksumType = Configuration.getInstance().getChecksumType().toLowerCase();

        for (ChecksumType chkType : ChecksumType.values()) {

            log.info("PIPPO: " + chkType.toString().toLowerCase());
            if (chkType.toString().toLowerCase().equals(configChecksumType)) {
                checksumType = chkType;
                break;
            }
        }

        if (checksumType == null) {
            log.error("Unsupported checksum type (property checksum.type): " + Configuration.getInstance().getChecksumType());
        }
    }

    private Checksum(String alg) {

        String configChecksumType = alg;

        for (ChecksumType chkType : ChecksumType.values()) {

            if (chkType.toString().toLowerCase().equals(configChecksumType.toLowerCase())) {
                this.checksumType = chkType;
                break;
            }
        }

        if (checksumType == null) {
            log.error("Unsupported checksum type (property checksum.type): " + configChecksumType);
        }
    }

    public static Checksum getInstance() {

        if (instance == null) {
            instance = new Checksum();
            instance.start();
        }

        return instance;
    }

    public static void main(String[] args) {

        if (args.length != 2) {
            System.out.println("usage: <fileName> <alg>");
            System.exit(1);
        }

        String fileName = args[0];
        String alg = args[1];

        System.out.println("Computing checksum for file: " + fileName + "...");
        Checksum chk = new Checksum(alg);
        String checksum = chk.computeChecksum(fileName);
        System.out.println("Checksum: " + checksum);

    }

    public String computeAndSetChecksum(String fileName) {

        String checksum = "task in queue";

        StormEA.setChecksum(fileName, checksum, checksumType.toString());

        try {
            queue.put(fileName);
        } catch (InterruptedException e) {
            return null;
        }

        return checksum;
    }

    /**
     * Computes checksum of the file. The algorithm used to compute the checksum is defined in the configuration. Use
     * {@link Checksum#getChecksumType()} to retrieve the used algorithm.
     * 
     * @param fileName file to compute the checksum for.
     * @return a String containing the computed checksum, an empty String if the file is zero length or
     *         <code>null</code> if some error occurred (e.g. file not found, I/O error, etc.).
     */
    public String computeChecksum(String fileName) {

        if (checksumType == null) {
            return null;
        }

        if (checksumType == ChecksumType.CRC32) {
            return computeChecksumCRC32(fileName);
        }

        if (checksumType == ChecksumType.ADLER32) {
            return computeChecksumAdler32(fileName);
        }

        for (ChecksumType chkType : ChecksumType.values()) {
            if (chkType == checksumType) {
                return computeChecksumMD(fileName, chkType);
            }
        }

        log.error("BUG: unknown algorithm \"" + checksumType.toString()
                + "\", cannot compute checksum for file: " + fileName);

        return null;
    }

    /**
     * Returns the algorithm that is used to compute the checksum.
     * 
     * @return a String identifying the algorithm.
     */
    public ChecksumType getChecksumType() {
        return checksumType;
    }

    public void run() {

        while (true) {

            try {

                String fileName = queue.take();

                StormEA.setChecksum(fileName, "computation in progress", checksumType.toString());

                Long startTime = System.currentTimeMillis();

                String checksum = computeChecksum(fileName);

                log.info((System.currentTimeMillis() - startTime) + " millis to compute "
                        + checksumType.toString() + " checksum for: " + fileName);

                StormEA.setChecksum(fileName, checksum, checksumType.toString());

            } catch (InterruptedException e) {
                break;
            }
        }

    }

    private String computeChecksumCRC32(String fileName) {
        String checksum = null;

        try {

            File file = new File(fileName);

            FileInputStream in = new FileInputStream(file);

            long fileSize = file.length();

            if (fileSize == 0) {
                return "";
            }

            int bufferSize;

            if (fileSize > MAX_BUFFER_SIZE) {
                bufferSize = MAX_BUFFER_SIZE;
            } else {
                bufferSize = (int) fileSize;
            }

            byte[] bArray = new byte[bufferSize];
            CRC32 crc32 = new CRC32();

            while (true) {

                int count = in.read(bArray, 0, bufferSize);

                if (count == -1) {
                    break;
                }

                crc32.update(bArray, 0, count);
            }

            in.close();

            checksum = Long.toHexString(crc32.getValue());

        } catch (FileNotFoundException e) {

            log.error("Error computing checksum because file not found: " + fileName);
            return null;

        } catch (IOException e) {
            log.error("Cannot compute checksum. Error reading file: " + fileName);
            return null;
        }

        return checksum;
    }

    private String computeChecksumAdler32(String fileName) {

        String checksum = null;
        File file = new File(fileName);

        if (file.length() == 0) {
            return "";
        }

        try {

            byte[] tempBuf = new byte[128];
            CheckedInputStream cis = new CheckedInputStream(new FileInputStream(file), new Adler32());
            while (cis.read(tempBuf) >= 0) {
            }
            checksum = Long.toHexString(cis.getChecksum().getValue());

        } catch (IOException e) {
            log.error("Error computing checksum of file: " + fileName, e);
        }
        return checksum;
    }

    private String computeChecksumMD(String fileName, ChecksumType checksumType) {

        String algorithm = checksumType.toString();
        String checksum = null;

        try {

            File file = new File(fileName);

            FileInputStream in = new FileInputStream(file);

            long fileSize = file.length();

            if (fileSize == 0) {
                return "";
            }

            int bufferSize;

            if (fileSize > MAX_BUFFER_SIZE) {
                bufferSize = MAX_BUFFER_SIZE;
            } else {
                bufferSize = (int) fileSize;
            }

            byte[] bArray = new byte[bufferSize];
            MessageDigest md = MessageDigest.getInstance(algorithm);

            while (true) {

                int count = in.read(bArray, 0, bufferSize);

                if (count == -1) {
                    break;
                }

                md.update(bArray, 0, count);
            }

            in.close();

            byte[] hash = md.digest();

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < hash.length; i++) {
                sb.append(Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(1));
            }

            checksum = sb.toString();

        } catch (FileNotFoundException e) {

            log.error("Error computing checksum because file not found: " + fileName);
            return null;

        } catch (NoSuchAlgorithmException e) {

            log.error("BUG: unknown algorithm, cannot compute checksum.", e);
            return null;

        } catch (IOException e) {
            log.error("Cannot compute checksum. Error reading file: " + fileName);
            return null;
        }

        return checksum;
    }
}
