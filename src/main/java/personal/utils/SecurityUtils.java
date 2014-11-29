package personal.utils;

import static org.apache.commons.codec.binary.Hex.encodeHexString;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityUtils {

    private static MessageDigest DIGEST;

    private static synchronized MessageDigest getDigest()
	    throws NoSuchAlgorithmException {
	if (DIGEST == null) {
	    DIGEST = MessageDigest.getInstance("SHA-512");
	}
	return DIGEST;
    }

    public static synchronized String sha512(String text) {
	String hexString;
	try {
	    byte[] hash = getDigest().digest(text.getBytes("UTF-8"));
	    hexString = encodeHexString(hash);
	} catch (UnsupportedEncodingException e) {
	    hexString = null;
	    e.printStackTrace();
	} catch (ArrayIndexOutOfBoundsException e) {
	    hexString = null;
	    e.printStackTrace();
	} catch (NoSuchAlgorithmException e) {
	    hexString = null;
	    e.printStackTrace();
	}

	return hexString;
    }
}
