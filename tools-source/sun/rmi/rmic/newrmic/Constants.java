package sun.rmi.rmic.newrmic;

public final class Constants {
   public static final String REMOTE = "java.rmi.Remote";
   public static final String EXCEPTION = "java.lang.Exception";
   public static final String REMOTE_EXCEPTION = "java.rmi.RemoteException";
   public static final String RUNTIME_EXCEPTION = "java.lang.RuntimeException";

   private Constants() {
      throw new AssertionError();
   }
}
