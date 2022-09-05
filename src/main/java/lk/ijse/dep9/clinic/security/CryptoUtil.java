package lk.ijse.dep9.clinic.security;

import org.apache.commons.codec.digest.DigestUtils;



public class CryptoUtil {
    public static void main(String[] args) {



        System.out.println(getSha265Hex("12345"));
    }
    public static String getSha265Hex(String a){
        return  DigestUtils.sha256Hex(a);

    }
}

