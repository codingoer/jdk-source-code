package com.sun.jarsigner;

import java.net.URI;
import java.security.cert.X509Certificate;
import java.util.zip.ZipFile;
import jdk.Exported;

@Exported
public interface ContentSignerParameters {
   String[] getCommandLine();

   URI getTimestampingAuthority();

   X509Certificate getTimestampingAuthorityCertificate();

   default String getTSAPolicyID() {
      return null;
   }

   byte[] getSignature();

   String getSignatureAlgorithm();

   X509Certificate[] getSignerCertificateChain();

   byte[] getContent();

   ZipFile getSource();
}
