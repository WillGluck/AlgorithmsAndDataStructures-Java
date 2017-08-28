import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;

public class Main {

    public static void main(String[] args) {
     
        if (3 != args.length) {
            System.out.println("Usage: VerifyingDigitalSignature publicKeyPath signatureFilePath fileSignedPath");
        } else {            
            try {
                
                File publicKeyFile = new File(args[0]);
                File signatureFile = new File(args[1]);
                File dataFile = new File(args[2]);
                InputStream inputStream = null;
                
                inputStream = new FileInputStream(publicKeyFile);                
                byte[] publicKeyEncoded = new byte[inputStream.available()];
                inputStream.read(publicKeyEncoded);
                inputStream.close();                
                X509EncodedKeySpec publicKeySpecification = new X509EncodedKeySpec(publicKeyEncoded);
                KeyFactory keyFactory = KeyFactory.getInstance("DSA", "SUN");
                PublicKey publicKey = keyFactory.generatePublic(publicKeySpecification);
                               
                inputStream = new FileInputStream(signatureFile);
                byte[] dataSignature = new byte[inputStream.available()];
                inputStream.read(dataSignature);
                inputStream.close();               

                Signature signature = Signature.getInstance("SHA1withDSA", "SUN");
                signature.initVerify(publicKey);
                inputStream = new BufferedInputStream(new FileInputStream(dataFile));
                byte[] dataBuffer = new byte[1024];
                int len;
                while((len = inputStream.read(dataBuffer)) > 0) {
                    signature.update(dataBuffer, 0, len);
                }
                inputStream.close();
                
                boolean verifies = signature.verify(dataSignature);
                System.out.println("Signature verifies: " + verifies);                
                
                
            } catch (Exception e) {
                System.out.println("Exception caugth " + e.toString());
            }
            
        }
        
    }
    
}
