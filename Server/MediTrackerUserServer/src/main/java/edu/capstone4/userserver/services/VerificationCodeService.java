package edu.capstone4.userserver.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class VerificationCodeService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    // 生成一个6位的随机验证码
    public String generateCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }

    // 保存验证码到 Redis，设置过期时间（默认5分钟）
    public void saveCode(String email, String code) {
        redisTemplate.opsForValue().set(email, code, 5, TimeUnit.MINUTES);
    }

    // 获取 Redis 中存储的验证码
    public String getCode(String email) {
        return redisTemplate.opsForValue().get(email);
    }

    // 验证验证码是否正确
    public boolean verifyCode(String email, String code) {
        String storedCode = getCode(email);
        return storedCode != null && storedCode.equals(code);
    }

    // 删除验证码（验证成功后删除）
    public void removeCode(String email) {
        redisTemplate.delete(email);
    }
}
