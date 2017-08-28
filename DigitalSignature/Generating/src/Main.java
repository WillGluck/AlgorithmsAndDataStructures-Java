import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;

public class Main {

    public static void main(String[] args) {
        
        if (1 != args.length && 2 != args.length) {
            System.out.println("Usage: GeneratingDigitalSignature fileToSignPath or GeneratingDigitalSignature fileToSignPath privateKeyPath");
        } else {            
            try {
                
                //KeyStore
                /*
                KeyStore ks = KeyStore.getInstance("JKS");
                FileInputStream ksfis = new FileInputStream(ksName); 
                BufferedInputStream ksbufin = new BufferedInputStream(ksfis);
                ks.load(ksbufin, spass);
                PrivateKey priv = (PrivateKey) ks.getKey(alias, kpass);
                 */
                
                
                
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("DSA", "SUN");
                SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG", "SUN");
                //SecureRandom secureRandom = SecureRandom.getInstanceStrong();
                keyPairGenerator.initialize(1024, secureRandom);
                
                PrivateKey privateKey = null;
                PublicKey publicKey = null;
                
                if (1 == args.length) {
                    KeyPair keyPair = keyPairGenerator.generateKeyPair();
                    privateKey = keyPair.getPrivate();
                    publicKey = keyPair.getPublic();
                } else {
                   
                    InputStream inputStream = new FileInputStream(args[1]);
                    byte[] privateKeyEncoded = new byte[inputStream.available()];
                    inputStream.read(privateKeyEncoded);
                    inputStream.close();
                    
                    PKCS8EncodedKeySpec privateKeySpecification = new PKCS8EncodedKeySpec(privateKeyEncoded);
                    
                    KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
                    privateKey = keyFactory.generatePrivate(privateKeySpecification);
                }
                
                Signature signature = Signature.getInstance("SHA1WithDSA", "SUN");
                signature.initSign(privateKey);
                
                InputStream inputStream = new BufferedInputStream(new FileInputStream(args[0]));
                OutputStream os = new BufferedOutputStream(new FileOutputStream("asdasdasd.pdf"));
                byte[] buffer = new byte[1024];                
                int len;
                while ((len = inputStream.read(buffer)) > 0) {
                    signature.update(buffer, 0, len);
                    os.write(buffer);
                }   
                os.write(signature.sign());
                os.close();
                inputStream.close();
                
                OutputStream outputStream = null;
                                
                byte[] digitalSignature = signature.sign();
                outputStream = new FileOutputStream("signature");
                outputStream.write(digitalSignature);
                outputStream.close();
                
                if (1 == args.length) {                    
                    byte[] publicKeyEncoded = publicKey.getEncoded();
                    outputStream = new FileOutputStream("publicKey");
                    outputStream.write(publicKeyEncoded);
                    outputStream.close();
                    
                    byte[] privateKeyEncoded = privateKey.getEncoded();
                    outputStream = new FileOutputStream("privateKey");
                    outputStream.write(privateKeyEncoded);
                    outputStream.close();
                }                
                
            } catch (Exception e) {
                System.out.println("Caugth exception " + e.toString());
                e.printStackTrace();
            }
        }        
    }    
}
