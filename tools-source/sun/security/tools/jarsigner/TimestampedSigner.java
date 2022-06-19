package sun.security.tools.jarsigner;

import com.sun.jarsigner.ContentSigner;
import com.sun.jarsigner.ContentSignerParameters;
import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import sun.security.pkcs.PKCS7;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import sun.security.util.ObjectIdentifier;
import sun.security.x509.AccessDescription;
import sun.security.x509.GeneralName;
import sun.security.x509.URIName;

public final class TimestampedSigner extends ContentSigner {
   private static final String SUBJECT_INFO_ACCESS_OID = "1.3.6.1.5.5.7.1.11";
   private static final ObjectIdentifier AD_TIMESTAMPING_Id;

   public byte[] generateSignedData(ContentSignerParameters var1, boolean var2, boolean var3) throws NoSuchAlgorithmException, CertificateException, IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         String var4 = var1.getSignatureAlgorithm();
         X509Certificate[] var5 = var1.getSignerCertificateChain();
         byte[] var6 = var1.getSignature();
         byte[] var7 = var2 ? null : var1.getContent();
         URI var8 = null;
         if (var3) {
            var8 = var1.getTimestampingAuthority();
            if (var8 == null) {
               var8 = getTimestampingURI(var1.getTimestampingAuthorityCertificate());
               if (var8 == null) {
                  throw new CertificateException("Subject Information Access extension not found");
               }
            }
         }

         String var9 = "SHA-256";
         if (var1 instanceof JarSignerParameters) {
            var9 = ((JarSignerParameters)var1).getTSADigestAlg();
         }

         return PKCS7.generateSignedData(var6, var5, var7, var1.getSignatureAlgorithm(), var8, var1.getTSAPolicyID(), var9);
      }
   }

   public static URI getTimestampingURI(X509Certificate var0) {
      if (var0 == null) {
         return null;
      } else {
         try {
            byte[] var1 = var0.getExtensionValue("1.3.6.1.5.5.7.1.11");
            if (var1 == null) {
               return null;
            }

            DerInputStream var2 = new DerInputStream(var1);
            var2 = new DerInputStream(var2.getOctetString());
            DerValue[] var3 = var2.getSequence(5);

            for(int var7 = 0; var7 < var3.length; ++var7) {
               AccessDescription var4 = new AccessDescription(var3[var7]);
               if (var4.getAccessMethod().equals(AD_TIMESTAMPING_Id)) {
                  GeneralName var5 = var4.getAccessLocation();
                  if (var5.getType() == 6) {
                     URIName var6 = (URIName)var5.getName();
                     if (var6.getScheme().equalsIgnoreCase("http") || var6.getScheme().equalsIgnoreCase("https")) {
                        return var6.getURI();
                     }
                  }
               }
            }
         } catch (IOException var8) {
         }

         return null;
      }
   }

   static {
      ObjectIdentifier var0 = null;

      try {
         var0 = new ObjectIdentifier("1.3.6.1.5.5.7.48.3");
      } catch (IOException var2) {
      }

      AD_TIMESTAMPING_Id = var0;
   }
}
