package edu.capstone4.userserver.utils;

import java.util.Random;

public class VerificationCodeGenerator {

    // 生成一个6位的随机验证码
    public static String generateCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }
}
