package com.sbc.common.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: pavel.bonev
 * Date: 18-6-1
 * Time: 13:47
 * To change this template use File | Settings | File Templates.
 */
public class Util {
  // 30 minutes
  public static final long TRANSACTION_EXPIRATION_IN_MILLIS = 30*60*1000;

  public static final int LEFT_PADDED = 0;
  public static final int RIGHT_PADDED = 1;

  public static SimpleDateFormat format = new SimpleDateFormat("[dd.MM.yyyy HH:mm:ss.SSS]", Locale.US);
  public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.US);


  public static ArrayList<String> parse(String values) {
    ArrayList<String> list = new ArrayList<String>();

    StringTokenizer tokenizer = new StringTokenizer(values, ",");
    while (tokenizer.hasMoreTokens()) {
      list.add(tokenizer.nextToken());
    }

    return list;
  }

  public static Date constructFromSQLDate(java.sql.Date dt) {
    if (dt == null) {
      return null;
    }

    return new Date(dt.getTime());
  }

  public static int compareOnlyDatePart(Date d1, Date d2) {
    if (d1.getYear() != d2.getYear())
      return d1.getYear() - d2.getYear();
    if (d1.getMonth() != d2.getMonth())
      return d1.getMonth() - d2.getMonth();
    return d1.getDate() - d2.getDate();
  }

  public static String boolToYN(Boolean flag) {
    if (flag == null) {
      return "N";
    }

    if (flag) {
      return "Y";
    } else {
      return "N";
    }
  }

  public static boolean ynToBool(String yn) {
    if (yn == null) {
      return false;
    }

    if (yn.trim().equalsIgnoreCase("Y")) {
      return true;
    } else {
      return false;
    }
  }

  public static Date getFirstDayOfWeek(Date date) {
    GregorianCalendar cal = new GregorianCalendar();
    cal.setTime(date);

    int currentDOW = cal.get(GregorianCalendar.DAY_OF_WEEK) - 2;
    if (currentDOW < 0) {
      currentDOW = 6;
    }
    cal.add(GregorianCalendar.DAY_OF_YEAR, -currentDOW);

    return cal.getTime();
  }


  public static String pad(int length, int padding, char fillCharacter, String value) {
    StringBuffer paddedValue = new StringBuffer(length);

    for (int i = 0; i < length; i++) {
      paddedValue.append(fillCharacter);
    }

    if (value != null) {
      int index = 0;
      int len = value.length();
      if (padding == LEFT_PADDED) {
        index = length - len;
      }

      if (index < 0) {
        index = 0;
      }

      if (len > length) {
        len = length;
      }

      for (int i = 0; i < len; i++) {
        paddedValue.setCharAt(index + i, value.charAt(i));
      }
    }

    return paddedValue.toString();
  }

  public static byte[] pad(int length, int padding, byte fillByte, byte[] value) {
    byte[] paddedValue = new byte[length];

    for (int i = 0; i < length; i++) {
      paddedValue[i] = fillByte;
    }

    if (value != null) {
      int index = 0;
      int len = value.length;
      if (padding == LEFT_PADDED) {
        index = length - len;
      }

      if (index < 0) {
        index = 0;
      }

      if (len > length) {
        len = length;
      }

      for (int i = 0; i < len; i++) {
        paddedValue[index + i] = value[i];
      }
    }

    return paddedValue;
  }

  public static String byteToHex(byte b) {
    int i = b & 0xFF;
    return pad(2, LEFT_PADDED, '0', Integer.toHexString(i));
  }

  public static String bytesToHex(byte[] b) {
    StringBuffer buff = new StringBuffer();

    for (int i = 0; i < b.length; i++) {
      buff.append(byteToHex(b[i]));
    }

    return buff.toString();
  }


  public static byte[] hexToBytes(String hex) {
    byte[] bytes = new byte[hex.length() / 2 + hex.length() % 2];
    int i = 0;
    int i1 = 0;
    while (i < bytes.length) {
      int i2 = Math.min(hex.length(), i1 + 2);
      String str = hex.substring(i1, i2);
      bytes[i] = (byte) Integer.parseInt(str, 16);
      i++;
      i1 += 2;
    }

    return bytes;
  }


  public static String now() {
    return format.format(GregorianCalendar.getInstance().getTime());
  }

  public static String formatDate(Date date) {
    return dateFormat.format(date);
  }

  public String base64Encode(String str) {
    //   BASE64Encoder encoder = new BASE64Encoder();
    return str;
  }


  public static String exceptionTrace(Throwable t) {
    StringWriter writer = new StringWriter();
    PrintWriter ps = new PrintWriter(writer);

    t.printStackTrace(ps);
    ps.flush();
    try {
      ps.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return writer.toString();
  }

  public static String encrypt(String str) throws Exception {
    Cipher cipher = Cipher.getInstance("DESede");
    DESedeKeySpec desKeySpec = new DESedeKeySpec(desKeyData);
    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
    SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

    cipher.init(Cipher.ENCRYPT_MODE, secretKey);

    byte[] encoded = cipher.doFinal(str.getBytes());
    String hex = Util.bytesToHex(encoded);

    return hex;
  }

  public static String decrypt(String hex) throws Exception {
    Cipher cipher = Cipher.getInstance("DESede");

    DESedeKeySpec desKeySpec = new DESedeKeySpec(desKeyData);
    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DESede");
    SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

    cipher.init(Cipher.DECRYPT_MODE, secretKey);
    byte[] bytes = Util.hexToBytes(hex);
    byte[] encoded = cipher.doFinal(bytes);

    return new String(encoded);
  }

  public static boolean stringToBool(String str) {
    if (str == null) {
      return false;
    }

    str = str.trim();
    if (str.equalsIgnoreCase("yes") ||
            str.equalsIgnoreCase("true")) {
      return true;
    }

    return false;
  }

  public static boolean intToBool(int i) {
    if (i == 1) {
      return true;
    } else {
      return false;
    }
  }

  public static int boolToInt(boolean b) {
    if (b) {
      return 1;
    } else {
      return 0;
    }
  }


  public static double trunc(double a, int digitsAfterPoint) {
    double pow = Math.pow(10, digitsAfterPoint);
    double b = a * pow;
    int ib = (int) b;
    b = ib / pow;

    return b;
  }


  public static boolean isTransactionExpired(Date creationDate) {
    long currentTimeMillis = System.currentTimeMillis();

    long creationTimeMillis = creationDate.getTime();

    long dif = currentTimeMillis - creationTimeMillis;

    if (dif > TRANSACTION_EXPIRATION_IN_MILLIS) {
      return true;
    } else {
      return false;
    }
  }


  public static void main(String args[]) {


  }

  private static byte[] desKeyData = {
          (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08,
          (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04,
          (byte) 0x05, (byte) 0x02, (byte) 0x06, (byte) 0x14,
          (byte) 0x04, (byte) 0x01, (byte) 0x08, (byte) 0x02,
          (byte) 0x02, (byte) 0x03, (byte) 0x02, (byte) 0x05,
          (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x18};
}

