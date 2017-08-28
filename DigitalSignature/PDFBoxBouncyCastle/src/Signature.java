import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.NoSuchFileException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.Store;

/**
 * 
 * @author TECBMWMG
 * @created 28 de ago de 2017 10:40:26
 *
 */
public class Signature implements SignatureInterface {
    
    private String keyPath;
    private String password;
    private String inFilePath;
    private String outFilePath;
    private PrivateKey privateKey;
    private Certificate certificate;
    private Certificate[] certificateChain;
    
    public Signature(String keyPath, String password, String inFilePath, String outFilePath) {
        this.keyPath = keyPath;
        this.password = password;
        this.inFilePath = inFilePath;
        this.outFilePath = outFilePath;
    }
    
    
    public void sign() throws Exception {
        
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        char[] passwordCharArray = password.toCharArray();
        keyStore.load(new FileInputStream(keyPath), passwordCharArray);
        
        Enumeration<String> aliases = keyStore.aliases();
        String alias;
        
        while (aliases.hasMoreElements()) {            
            alias = aliases.nextElement();            
            privateKey = (PrivateKey) keyStore.getKey(alias, passwordCharArray);            
            certificateChain = keyStore.getCertificateChain(alias);
            if (certificateChain == null) {
                continue;
            }            
            certificate = keyStore.getCertificate(alias);
            if (certificate instanceof X509Certificate){
                ((X509Certificate) certificate).checkValidity();
            }            
            break;
        }

        if (certificate == null) {
            throw new IOException("Could not find certificate");
        }
        
        File inFile = new File(inFilePath);
        
        if (!inFile.exists()) {
            throw new NoSuchFileException("File does not exist");
        }
        
        PDDocument document = PDDocument.load(inFile);
        
        
        PDSignature signature = new PDSignature();
        signature.setFilter(PDSignature.FILTER_ADOBE_PPKLITE);
        signature.setSubFilter(PDSignature.SUBFILTER_ADBE_PKCS7_DETACHED);
        signature.setName("Usuário");
        signature.setLocation("Blumenau, SC");
        signature.setReason("Testing");
        signature.setSignDate(Calendar.getInstance());
        
        
        COSDictionary sigDict = signature.getCOSObject();

        // DocMDP specific stuff
        COSDictionary transformParameters = new COSDictionary();
        transformParameters.setItem(COSName.TYPE, COSName.getPDFName("TransformParams"));
        transformParameters.setInt(COSName.P, 2);
        transformParameters.setName(COSName.V, "1.2");
        transformParameters.setNeedToBeUpdated(true);

        COSDictionary referenceDict = new COSDictionary();
        referenceDict.setItem(COSName.TYPE, COSName.getPDFName("SigRef"));
        referenceDict.setItem("TransformMethod", COSName.getPDFName("DocMDP"));
        referenceDict.setItem("DigestMethod", COSName.getPDFName("SHA1"));
        referenceDict.setItem("TransformParams", transformParameters);
        referenceDict.setNeedToBeUpdated(true);

        COSArray referenceArray = new COSArray();
        referenceArray.add(referenceDict);
        sigDict.setItem("Reference", referenceArray);
        referenceArray.setNeedToBeUpdated(true);

        COSDictionary catalogDict = document.getDocumentCatalog().getCOSObject();
        COSDictionary permsDict = new COSDictionary();
        catalogDict.setItem(COSName.PERMS, permsDict);
        permsDict.setItem(COSName.DOCMDP, signature);
        catalogDict.setNeedToBeUpdated(true);
        permsDict.setNeedToBeUpdated(true);
        
        document.addSignature(signature, this);
        OutputStream outputStream = new FileOutputStream(outFilePath);
        document.saveIncremental(outputStream);        
    }
    
    public byte[] sign(InputStream content) throws IOException {
        try {            
            List<Certificate> certList = new ArrayList<Certificate>();
            certList.addAll(Arrays.asList(certificateChain));
            certList.add(certificate);
            Store<?> certs = new JcaCertStore(certList);
            CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
            org.bouncycastle.asn1.x509.Certificate cert = org.bouncycastle.asn1.x509.Certificate.getInstance(ASN1Primitive.fromByteArray(certificate.getEncoded()));
            ContentSigner sha1Signer = new JcaContentSignerBuilder("SHA256WithRSA").build(privateKey);
            gen.addSignerInfoGenerator(new JcaSignerInfoGeneratorBuilder(new JcaDigestCalculatorProviderBuilder().build()).build(sha1Signer, new X509CertificateHolder(cert)));
            gen.addCertificates(certs);
            CMSProcessableInputStream msg = new CMSProcessableInputStream(content);
            CMSSignedData signedData = gen.generate(msg, false);
            return signedData.getEncoded();
        }
        catch (GeneralSecurityException e) {
            throw new IOException(e);
        }
        catch (CMSException e) {
            throw new IOException(e);
        }
        catch (OperatorCreationException e) {
            throw new IOException(e);
        }
    }    
}
