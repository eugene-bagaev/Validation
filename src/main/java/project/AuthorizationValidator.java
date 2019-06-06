package project;

import project.Emails.MailService;
import project.Rules.Constants;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.UUID;

public class AuthorizationValidator {
    public static String cryptPassword(String st) {
        MessageDigest messageDigest = null;
        byte[] digest = new byte[0];

        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(st.getBytes());
            digest = messageDigest.digest();
        } catch (Exception e) {
            e.printStackTrace();
        }

        BigInteger bigInt = new BigInteger(1, digest);
        String md5Hex = bigInt.toString(16);

        while (md5Hex.length() < 32) {
            md5Hex = "0" + md5Hex;
        }

        return md5Hex;
    }

    public static AuthorizationInfoWrapper checkCreds(String login, String password) throws FileNotFoundException {

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("src/main/resources/AutorizationCredentials")))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] userData = line.split(":::::");
                if (userData[0].equalsIgnoreCase(login) && userData[1].equalsIgnoreCase(cryptPassword(password))) {
                    br.close();
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/UserSessions", true))) {
                        String token = UUID.randomUUID().toString();
                        writer.append("\n" + userData[2] + ":::::" + token);
                        writer.close();

                        return new AuthorizationInfoWrapper(userData[2], token);
                    } catch (Exception e) {
                        MailService.getInstance().setSubject("Произошло исключкние")
                                .setBody(e.toString())
                                .sendMail();
                    }
                    break;
                }
            }
        } catch (IOException ioex) {
            System.out.println(ioex.toString());
        }
        return null;
    }


    public static Boolean ifSessionIsExisted(String userName, String token) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("src/main/resources/UserSessions")))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] sessionInfo = line.split(":::::");
                if(sessionInfo.length < 2) continue;
                if (sessionInfo[0].equalsIgnoreCase(userName) && sessionInfo[1].equalsIgnoreCase(token)) {
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
