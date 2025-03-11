package main;
import java.security.*;
import java.util.Base64;

public class DigitalWallet {
    private KeyPair keyPair;

    public DigitalWallet() throws NoSuchAlgorithmException {
        // Gera o par de chaves para o usuário
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        keyPair = keyGen.generateKeyPair();
    }

    public String generatePaymentToken(String userId, String storeId, double amount) throws Exception {
        String tokenData = "User:" + userId + "|Store:" + storeId + "|Amount:" + amount + "|Timestamp:" + System.currentTimeMillis();
        return signData(tokenData);
    }

    private String signData(String data) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(keyPair.getPrivate());
        signature.update(data.getBytes());
        byte[] signedData = signature.sign();
        return Base64.getEncoder().encodeToString(signedData);
    }

    public boolean verifyPayment(String data, String signedToken, PublicKey publicKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(data.getBytes());
        return signature.verify(Base64.getDecoder().decode(signedToken));
    }

    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    public static void main(String[] args) throws Exception {
        DigitalWallet wallet = new DigitalWallet();

        // Simulando pagamento
        String userId = "UserXYZ";
        String storeId = "StoreABC";
        double amount = 100.50;
        String paymentToken = wallet.generatePaymentToken(userId, storeId, amount);

        // Loja verifica a transação
        boolean isValid = wallet.verifyPayment("User:" + userId + "|Store:" + storeId + "|Amount:" + amount + "|Timestamp:...", paymentToken, wallet.getPublicKey());

        if (isValid) {
            System.out.println("✅ Pagamento aprovado!");
        } else {
            System.out.println("❌ Pagamento inválido!");
        }
    }
}
