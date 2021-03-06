package eu.seatter.homemeasurement.collector.services.encryption;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;


/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 28/01/2019
 * Time: 08:56
 */
@Service
public class EncyptionServiceASE implements EncryptionService {
    SecureRandom random = new SecureRandom();

    private static final String KEY = "7h4d9j4em@[7g3ed";

    @Override
    public String encryptString(String unencrypted) {
        byte[] bytesIV = new byte[16];
        random.nextBytes(bytesIV);

        /* KEY + IV setting */
        if(unencrypted.length() <= 100) {
            try {
                IvParameterSpec iv = new IvParameterSpec(bytesIV);
                SecretKeySpec skeySpec = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "AES");

                Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

                byte[] encrypted = cipher.doFinal(unencrypted.getBytes());
                return Base64.getEncoder().encodeToString(encrypted);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            return null;
        }
        return null;
    }

    @Override
    public String decrypteString(String encrypted) {
        byte[] bytesIV = new byte[16];
        random.nextBytes(bytesIV);

        try {
            IvParameterSpec iv = new IvParameterSpec(bytesIV);
            SecretKeySpec skeySpec = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "AES");

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}
