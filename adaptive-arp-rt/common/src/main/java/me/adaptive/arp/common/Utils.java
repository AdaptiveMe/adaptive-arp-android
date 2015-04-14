package me.adaptive.arp.common;

import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.adaptive.arp.api.FileDescriptor;
import me.adaptive.arp.api.Service;
import me.adaptive.arp.api.ServiceEndpoint;
import me.adaptive.arp.api.ServicePath;
import me.adaptive.arp.api.ServiceToken;
import me.adaptive.arp.common.parser.xml.XmlParser;

/**
 * Utils class for public static methods
 */
public class Utils {

    /**
     * Validates a string with a regular expression passed by parameter
     *
     * @param s String to validate
     * @param r Regular expression
     */
    public static boolean validateRegexp(String s, String r) {
        try {
            Pattern pattern = Pattern.compile(r);
            Matcher matcher = pattern.matcher(s);
            return matcher.matches();
        } catch (RuntimeException e) {
            return false;
        }
    }

    /**
     * This method returns a byte Array from an input stream
     *
     * @param is Input Stream to read
     * @return Array of bytes
     */
    public static byte[] getBytesFromInputStream(InputStream is) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream();) {
            byte[] buffer = new byte[0xFFFF];

            for (int len; (len = is.read(buffer)) != -1; )
                os.write(buffer, 0, len);

            os.flush();

            return os.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Remove file extension from path
     *
     * @param filePath File path
     * @return File without extension
     */
    public static String removeExtension(String filePath) {

        File f = new File(filePath);
        if (f.isDirectory()) return filePath;

        String name = f.getName();

        final int lastPeriodPos = name.lastIndexOf('.');
        if (lastPeriodPos <= 0) {
            return filePath;
        } else {
            File renamed = new File(f.getParent(), name.substring(0, lastPeriodPos));
            return renamed.getPath();
        }
    }

    /**
     * Returns a FileDescriptor from a java File
     * @param file to read
     * @return the FileDescriptor
     */
    public static FileDescriptor toArp(File file){

        FileDescriptor fd = new FileDescriptor();
        fd.setName(file.getName());
        fd.setDateCreated(file.lastModified());
        fd.setDateModified(file.lastModified());
        fd.setPath(file.getPath());
        fd.setPathAbsolute(file.getAbsolutePath());
        fd.setSize(file.getTotalSpace());

        return fd;

    }

    /**
     * Checks if external storage is available for read and write
     */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /**
     * Returns the byte[] content of a File
     * @param file to read
     * @return the byte[] data
     * @throws IOException
     */
    public static byte[] readFile(String file) throws IOException {
        return readFile(new File(file));
    }

    /**
     * Returns a byte[]
     * @param file to read
     * @return the byte[]
     * @throws IOException
     */
    public static byte[] readFile(File file) throws IOException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }

    /**
     * Validate an url against a regexp
     * @param test url
     * @param regex test
     * @return true if valid, false otherwise
     */
    public static boolean validateURI(String test, String regex){
        return test.matches(regex);
    }

    /**
     * Add element to APIBean Array
     * @param a source array
     * @param e element
     * @return the new array
     */
    public static <APIBean> APIBean[] addElement(APIBean[] a, APIBean e) {
        a = Arrays.copyOf(a, a.length + 1);
        a[a.length - 1] = e;
        return a;
    }

    /**
     * Check whether a service should be handled or not
     * @param serviceToken to validate
     * @return true if should be handled, false otherwise
     */
    public static boolean validateService(ServiceToken serviceToken){
        Service serv = XmlParser.getInstance().getServices().get(serviceToken.getServiceName());
        for (ServiceEndpoint serviceEndpoint : serv.getServiceEndpoints()) {
            String endpointName = serviceToken.getEndpointName();
            if(endpointName.equals(serviceEndpoint.getHostURI())){
                Pattern pattern = Pattern.compile(endpointName);
                Matcher m = pattern.matcher(serviceToken.getEndpointName());
                if(m.matches()){
                    for (ServicePath servicePath : serviceEndpoint.getPaths()) {
                        pattern = Pattern.compile(servicePath.getPath());
                        m = pattern.matcher(serviceToken.getFunctionName());
                        if(m.matches()) return true;
                    }
                }

            }
        }
        return false;

    }

    /**
     * Check whether an url should be handled or not
     * @param urlString to validate
     * @return true if should be handled, false otherwise
     * @throws MalformedURLException
     */
    public static boolean validateUrl(String urlString) throws MalformedURLException {
        Pattern pattern = null;
        Matcher m = null;
        URL url = new URL(urlString);
        for (Service service : XmlParser.getInstance().getServices().values()) {
            for (ServiceEndpoint serviceEndpoint : service.getServiceEndpoints()) {
                if(url.getProtocol().concat("://").concat(url.getHost()).equals(serviceEndpoint.getHostURI())){
                    for (ServicePath servicePath : serviceEndpoint.getPaths()) {
                        pattern = Pattern.compile(servicePath.getPath());
                        m = pattern.matcher(url.getPath());
                        if(m.matches()) return true;
                    }
                }
            }
        }

        for (String resource : XmlParser.getInstance().getResources()) {
            pattern = Pattern.compile(resource);
            m = pattern.matcher(urlString);
            if(m.matches()) return true;
        }


        return false;

    }


}
