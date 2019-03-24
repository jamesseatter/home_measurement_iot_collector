package eu.seatter.homeheating.collector.services;

/**
 * Created by IntelliJ IDEA.
 * User: jas
 * Date: 28/01/2019
 * Time: 08:55
 */
public interface EncryptionService {
    String encryptString(String jsonString);
    String decrypteString(String base64String);
}
