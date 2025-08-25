package com.example.HospitalSystemBackend.Util;

import java.util.Base64;
import kr.re.nsr.seed.KISA_SEED_CTR;

/**
 * @file KISA_SEED_CTR.java
 * @brief SEED CTR 암호 알고리즘
 * @author Copyright (c) 2013 by KISA
 * @remarks http://seed.kisa.or.kr/
 */
public strictfp class MymdSeedCtrUtil {

    private static final int KEY_BIT_LENGTH = 256; // 키 길이 : 256 비트
    private static final int KEY_BYTE_LENGTH = KEY_BIT_LENGTH / 8;
    private static final byte[] bszCTR;

    // 카운터
    static {
        bszCTR = new byte[KEY_BYTE_LENGTH];
        bszCTR[KEY_BYTE_LENGTH - 1] = (byte) (0xFE & 0xff);
    }

    public static String SEED_CTR_Encrypt(String key, String plainText) {

        byte[] pbszUserKey = Base64.getDecoder().decode(key.getBytes());
        byte[] pbszPlainText = plainText.getBytes();
        int nPlainTextLen = pbszPlainText.length;

        byte[] defaultCipherText = KISA_SEED_CTR.SEED_CTR_Encrypt(pbszUserKey, bszCTR, pbszPlainText, 0, nPlainTextLen);
        String defaultCipherString = Base64.getEncoder().encodeToString(defaultCipherText);

        return defaultCipherString;
    }
}
