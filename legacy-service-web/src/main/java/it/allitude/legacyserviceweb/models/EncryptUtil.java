package it.allitude.legacyserviceweb.models;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.stereotype.Component;

@Component
public class EncryptUtil {

    static final String g_keyesDirectory = "run_time_resources/static/keyes";

    PrivateKey privateKey;
    PublicKey publicKey;
    Cipher encryptCipher;
    Cipher decryptCipher;

    public EncryptUtil() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, FileNotFoundException, java.io.IOException, InvalidKeySpecException {

        Path keyesPath = Paths.get(g_keyesDirectory);
        if (!Files.exists(keyesPath)) {
            Files.createDirectories(keyesPath);
        }

        Date currentDate = new Date();
        Path privateKeyFilePath = keyesPath.resolve(String.format("private_%s%s.key", currentDate.getMonth(), currentDate.getYear()));
        Path publicKeyFilePath = keyesPath.resolve (String.format("public_%s%s.pub", currentDate.getMonth(), currentDate.getYear()));
        // Se non esistono i file delle chiavi creo le chiavi e le salvo
        if (!Files.exists(privateKeyFilePath) || !Files.exists(publicKeyFilePath)) {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair pair = generator.generateKeyPair();
            privateKey = pair.getPrivate();
            publicKey = pair.getPublic();

            FileOutputStream privateKeyFos = new FileOutputStream(privateKeyFilePath.toAbsolutePath().toString());
            privateKeyFos.write(privateKey.getEncoded());
            privateKeyFos.close();

            FileOutputStream publicKeyFos = new FileOutputStream(publicKeyFilePath.toAbsolutePath().toString());
            publicKeyFos.write(publicKey.getEncoded());
            publicKeyFos.close();
        } else {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            File publicKeyFile = new File(publicKeyFilePath.toAbsolutePath().toString());
            byte[] publicKeyBytes = Files.readAllBytes(publicKeyFile.toPath());
            EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
            publicKey = keyFactory.generatePublic(publicKeySpec);

            File privateKeyFile = new File(privateKeyFilePath.toAbsolutePath().toString());
            byte[] privateKeyBytes = Files.readAllBytes(privateKeyFile.toPath());
            EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            privateKey = keyFactory.generatePrivate(privateKeySpec);
        }
        encryptCipher = Cipher.getInstance("RSA");
        encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
        decryptCipher = Cipher.getInstance("RSA");
        decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
    }

    public String encrypt(String aVal) throws IllegalBlockSizeException, BadPaddingException {
        byte[] secretMessageBytes = aVal.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedMessageBytes = encryptCipher.doFinal(secretMessageBytes);
        // String encodedMessage = Base64.getEncoder().encodeToString(encryptedMessageBytes);
        String encodedMessage = encodeHexString(encryptedMessageBytes);
        return encodedMessage;
    }

    public String decrypt(String aVal) throws IllegalBlockSizeException, BadPaddingException {
        // byte[] encryptedMessageBytes = Base64.getDecoder().decode(aVal);
        byte[] encryptedMessageBytes = decodeHexString(aVal);

        byte[] decryptedMessageBytes = decryptCipher.doFinal(encryptedMessageBytes);
        String decryptedMessage = new String(decryptedMessageBytes, StandardCharsets.UTF_8);
        return decryptedMessage;
    }

    public String encodeHexString(byte[] byteArray) {
        StringBuffer hexStringBuffer = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            hexStringBuffer.append(byteToHex(byteArray[i]));
        }
        return hexStringBuffer.toString();
    }

    public byte[] decodeHexString(String hexString) {
        if (hexString.length() % 2 == 1) {
            throw new IllegalArgumentException(
                    "Invalid hexadecimal String supplied.");
        }

        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            bytes[i / 2] = hexToByte(hexString.substring(i, i + 2));
        }
        return bytes;
    }

    private String byteToHex(byte num) {
        char[] hexDigits = new char[2];
        hexDigits[0] = Character.forDigit((num >> 4) & 0xF, 16);
        hexDigits[1] = Character.forDigit((num & 0xF), 16);
        return new String(hexDigits);
    }

    private byte hexToByte(String hexString) {
        int firstDigit = toDigit(hexString.charAt(0));
        int secondDigit = toDigit(hexString.charAt(1));
        return (byte) ((firstDigit << 4) + secondDigit);
    }

    private int toDigit(char hexChar) {
        int digit = Character.digit(hexChar, 16);
        if (digit == -1) {
            throw new IllegalArgumentException(
                    "Invalid Hexadecimal Character: " + hexChar);
        }
        return digit;
    }
}
