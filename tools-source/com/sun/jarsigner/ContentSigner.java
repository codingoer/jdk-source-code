package com.sun.jarsigner;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import jdk.Exported;

@Exported
public abstract class ContentSigner {
   public abstract byte[] generateSignedData(ContentSignerParameters var1, boolean var2, boolean var3) throws NoSuchAlgorithmException, CertificateException, IOException;
}
