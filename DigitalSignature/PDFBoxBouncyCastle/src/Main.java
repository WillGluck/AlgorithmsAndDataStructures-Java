public class Main {

    public static void main(String[] args) {

        if (4 != args.length) {
            usage();
        } else {
            Signature signature = new Signature(args[0], args[1], args[2], args[3]);
            try {
                signature.sign();
            } catch (Exception e) {
               System.out.println("Exception caugth: + " + e.toString());
               e.printStackTrace();
            }
        }

    }
    
    private static void usage() {
        System.out.println("Usage: SignaturePDFBox pkcs12Path password inFilePath outFilePath");
    }

}
