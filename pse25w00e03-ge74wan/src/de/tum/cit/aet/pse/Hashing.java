package de.tum.cit.aet.pse;

public abstract class Hashing 
{
    private HashFunction implementation;

    public Hashing(HashFunction hashFunction)
    {
        this.implementation = hashFunction;
    }

    public HashFunction getImplementation() {
        return implementation;
    }

    public abstract String hashDocument(String document);
}
