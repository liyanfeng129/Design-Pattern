package de.tum.cit.aet.pse;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

public final class ExamSystem {

	private ExamSystem() {
	}

	// TODO 5: Change signature, make use of the bridge pattern
	public static String hashFile(String document, Hashing hashing) {
		return hashing.hashDocument(document);
	}

	public static void main(String[] args) {
		 String file1 = readFile("exams/short_exam.txt");
                String file2 = readFile("exams/long_exam.txt");  //This file is too big for Preview Hashing

                // TODO 6: Change SimpleHash to PreviewHashing
                Hashing previewHashing = new PreviewHashing();

                System.out.println(hashFile(file1, previewHashing));
                try {
                        System.out.println(hashFile(file2, previewHashing));
                        throw new IllegalStateException("Hashing this file with preview hashing should not work!");
                } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                }

                // TODO 6: Change CryptoSecureHashAlgorithm to EnterpriseHashing
                Hashing enterpriseHashing = new EnterpriseHashing();

                System.out.println(hashFile(file1, enterpriseHashing));


                System.out.println(hashFile(file1, enterpriseHashing));
                System.out.println(hashFile(file2, enterpriseHashing));

                System.out.println(hashFile(file1, enterpriseHashing));


                System.out.println(hashFile(file1, enterpriseHashing));
                System.out.println(hashFile(file2, enterpriseHashing));

	}

	public static String readFile(String filepath) {
		Path path = Path.of(filepath);
		// TODO 4: Return the content of the passed file as a String.
		try {
			String content = Files.readString(path);
			return content;
		} catch (IOException e) {
			throw new RuntimeException("Error reading file: " + filepath, e);
		}
	}

}
