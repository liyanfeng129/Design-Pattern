package de.tum.cit.aet.pse;

public class EnterpriseHashing extends Hashing {
    public EnterpriseHashing() {
        super(new CryptoSecureHashAlgorithm());
    }

    @Override
    public String hashDocument(String document) {
        return getImplementation().calculateHashCode(document);
    }
}
