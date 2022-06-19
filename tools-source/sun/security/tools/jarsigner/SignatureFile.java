package sun.security.tools.jarsigner;

import com.sun.jarsigner.ContentSigner;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.jar.Attributes.Name;
import java.util.zip.ZipFile;
import sun.security.util.ManifestDigester;
import sun.security.x509.AlgorithmId;
import sun.security.x509.X500Name;
import sun.security.x509.X509CertInfo;

class SignatureFile {
   Manifest sf;
   String baseName;

   public SignatureFile(MessageDigest[] var1, Manifest var2, ManifestDigester var3, String var4, boolean var5) {
      this.baseName = var4;
      String var6 = System.getProperty("java.version");
      String var7 = System.getProperty("java.vendor");
      this.sf = new Manifest();
      Attributes var8 = this.sf.getMainAttributes();
      var8.putValue(Name.SIGNATURE_VERSION.toString(), "1.0");
      var8.putValue("Created-By", var6 + " (" + var7 + ")");
      if (var5) {
         for(int var9 = 0; var9 < var1.length; ++var9) {
            var8.putValue(var1[var9].getAlgorithm() + "-Digest-Manifest", Base64.getEncoder().encodeToString(var3.manifestDigest(var1[var9])));
         }
      }

      ManifestDigester.Entry var16 = var3.get("Manifest-Main-Attributes", false);
      if (var16 == null) {
         throw new IllegalStateException("ManifestDigester failed to create Manifest-Main-Attribute entry");
      } else {
         for(int var10 = 0; var10 < var1.length; ++var10) {
            var8.putValue(var1[var10].getAlgorithm() + "-Digest-" + "Manifest-Main-Attributes", Base64.getEncoder().encodeToString(var16.digest(var1[var10])));
         }

         Map var17 = this.sf.getEntries();
         Iterator var11 = var2.getEntries().entrySet().iterator();

         while(true) {
            String var13;
            do {
               if (!var11.hasNext()) {
                  return;
               }

               Map.Entry var12 = (Map.Entry)var11.next();
               var13 = (String)var12.getKey();
               var16 = var3.get(var13, false);
            } while(var16 == null);

            Attributes var14 = new Attributes();

            for(int var15 = 0; var15 < var1.length; ++var15) {
               var14.putValue(var1[var15].getAlgorithm() + "-Digest", Base64.getEncoder().encodeToString(var16.digest(var1[var15])));
            }

            var17.put(var13, var14);
         }
      }
   }

   public void write(OutputStream var1) throws IOException {
      this.sf.write(var1);
   }

   public String getMetaName() {
      return "META-INF/" + this.baseName + ".SF";
   }

   public String getBaseName() {
      return this.baseName;
   }

   public Block generateBlock(PrivateKey var1, String var2, X509Certificate[] var3, boolean var4, String var5, X509Certificate var6, String var7, String var8, ContentSigner var9, String[] var10, ZipFile var11) throws NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException, CertificateException {
      return new Block(this, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public static class Block {
      private byte[] block;
      private String blockFileName;

      Block(SignatureFile var1, PrivateKey var2, String var3, X509Certificate[] var4, boolean var5, String var6, X509Certificate var7, String var8, String var9, ContentSigner var10, String[] var11, ZipFile var12) throws NoSuchAlgorithmException, InvalidKeyException, IOException, SignatureException, CertificateException {
         Principal var13 = var4[0].getIssuerDN();
         if (!(var13 instanceof X500Name)) {
            X509CertInfo var14 = new X509CertInfo(var4[0].getTBSCertificate());
            var13 = (Principal)var14.get("issuer.dname");
         }

         BigInteger var27 = var4[0].getSerialNumber();
         String var16 = var2.getAlgorithm();
         String var15;
         if (var3 == null) {
            if (var16.equalsIgnoreCase("DSA")) {
               var15 = "SHA256withDSA";
            } else if (var16.equalsIgnoreCase("RSA")) {
               var15 = "SHA256withRSA";
            } else {
               if (!var16.equalsIgnoreCase("EC")) {
                  throw new RuntimeException("private key is not a DSA or RSA key");
               }

               var15 = "SHA256withECDSA";
            }
         } else {
            var15 = var3;
         }

         String var17 = var15.toUpperCase(Locale.ENGLISH);
         if ((!var17.endsWith("WITHRSA") || var16.equalsIgnoreCase("RSA")) && (!var17.endsWith("WITHECDSA") || var16.equalsIgnoreCase("EC")) && (!var17.endsWith("WITHDSA") || var16.equalsIgnoreCase("DSA"))) {
            this.blockFileName = "META-INF/" + var1.getBaseName() + "." + var16;
            AlgorithmId var18 = AlgorithmId.get(var15);
            AlgorithmId var19 = AlgorithmId.get(var16);
            Signature var20 = Signature.getInstance(var15);
            var20.initSign(var2);
            ByteArrayOutputStream var21 = new ByteArrayOutputStream();
            var1.write(var21);
            byte[] var22 = var21.toByteArray();
            var20.update(var22);
            byte[] var23 = var20.sign();
            if (var10 == null) {
               var10 = new TimestampedSigner();
            }

            URI var24 = null;

            try {
               if (var6 != null) {
                  var24 = new URI(var6);
               }
            } catch (URISyntaxException var26) {
               throw new IOException(var26);
            }

            JarSignerParameters var25 = new JarSignerParameters(var11, var24, var7, var8, var9, var23, var15, var4, var22, var12);
            this.block = ((ContentSigner)var10).generateSignedData(var25, var5, var6 != null || var7 != null);
         } else {
            throw new SignatureException("private key algorithm is not compatible with signature algorithm");
         }
      }

      public String getMetaName() {
         return this.blockFileName;
      }

      public void write(OutputStream var1) throws IOException {
         var1.write(this.block);
      }
   }
}
