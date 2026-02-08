package de.tum.cit.aet.pse;

public class PreviewHashing extends Hashing {
    private static final int PREVIEW_HASH_MAXIMUM_LENGTH = 1000;
    public PreviewHashing() {
        super(new SimpleHashAlgorithm());
    }

    @Override
    public String hashDocument(String document) {
        if (document.length() <= PREVIEW_HASH_MAXIMUM_LENGTH)
            return getImplementation().calculateHashCode(document);
        else
            throw new IllegalArgumentException("Document too long for Preview Hashing");
    }
}
