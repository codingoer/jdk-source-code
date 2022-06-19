package sun.security.tools.jarsigner;

import com.sun.jarsigner.ContentSignerParameters;
import java.net.URI;
import java.security.cert.X509Certificate;
import java.util.zip.ZipFile;

class JarSignerParameters implements ContentSignerParameters {
   private String[] args;
   private URI tsa;
   private X509Certificate tsaCertificate;
   private byte[] signature;
   private String signatureAlgorithm;
   private X509Certificate[] signerCertificateChain;
   private byte[] content;
   private ZipFile source;
   private String tSAPolicyID;
   private String tSADigestAlg;

   JarSignerParameters(String[] var1, URI var2, X509Certificate var3, String var4, String var5, byte[] var6, String var7, X509Certificate[] var8, byte[] var9, ZipFile var10) {
      if (var6 != null && var7 != null && var8 != null && var5 != null) {
         this.args = var1;
         this.tsa = var2;
         this.tsaCertificate = var3;
         this.tSAPolicyID = var4;
         this.tSADigestAlg = var5;
         this.signature = var6;
         this.signatureAlgorithm = var7;
         this.signerCertificateChain = var8;
         this.content = var9;
         this.source = var10;
      } else {
         throw new NullPointerException();
      }
   }

   public String[] getCommandLine() {
      return this.args;
   }

   public URI getTimestampingAuthority() {
      return this.tsa;
   }

   public X509Certificate getTimestampingAuthorityCertificate() {
      return this.tsaCertificate;
   }

   public String getTSAPolicyID() {
      return this.tSAPolicyID;
   }

   public String getTSADigestAlg() {
      return this.tSADigestAlg;
   }

   public byte[] getSignature() {
      return this.signature;
   }

   public String getSignatureAlgorithm() {
      return this.signatureAlgorithm;
   }

   public X509Certificate[] getSignerCertificateChain() {
      return this.signerCertificateChain;
   }

   public byte[] getContent() {
      return this.content;
   }

   public ZipFile getSource() {
      return this.source;
   }
}
