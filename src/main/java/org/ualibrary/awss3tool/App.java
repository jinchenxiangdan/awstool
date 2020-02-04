package org.ualibrary.awss3tool;



import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;



import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.io.File;

import java.nio.file.Paths;
import java.util.Scanner;


/**
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion("us-west-1").build();

        // check folder or file
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter file/folder path");
        String input = scanner.next();
        Path file = new File(input).toPath();
        // check if the file exist or not
        if (Files.exists(file)) {

        } else {
            // the file doesn't exist
        }
        // check the path is a file or folder
        if (Files.isDirectory(file)) {

        } else {
            // other?
        }


        List<Bucket> buckets = getBuckets(s3);

        String filePath = "/home/wan/Desktop/ShawnJin_Workspace/testFolder/test.txt";
        String folderPath = "/home/wan/Desktop/ShawnJin_Workspace/testFolder";
        String bucketName = buckets.get(2).getName();

//        putObject(s3, filePath, bucketName);


        // get folder

        // running script
        ProcessBuilder processBuilder = new ProcessBuilder("/home/wan/Desktop/ShawnJin_Workspace/awstool/src/main/bash_script/awss3tool.sh", "arg1");

        Process process = null;
        // run
        try {
            System.out.println("uploading...");
            process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        assert process != null;
        if (process.exitValue() == 0) {
            System.out.println("Done.");
        } else {
            System.out.println("Something Wrong.");
        }
        System.out.printf("exist value: %d", process.exitValue());



    }


    /**
     *  get buckets that the user has
     * @param s3 AmazonS3 service
     * @return A List of Buckets
     */
    private static List<Bucket> getBuckets(AmazonS3 s3) {
        List<Bucket> bucketList = s3.listBuckets();

        // print out information
        System.out.println("===========================");
        System.out.println("Your Amazon S3 buckets are:");
        for (Bucket b : bucketList) {
            System.out.println(" * " + b.getName());
        }
        System.out.println("===========================");
        return bucketList;
    }


    /**
     * upload file to the aws s3
     * @param s3 AmazonS3 service
     * @param filePath string path to file
     * @param bucketName the bucket name will be upload to
     */
    private static void putObject(AmazonS3 s3, String filePath, String bucketName) {
        System.out.format("Uploading %s to S3 bucket %s...\n", filePath, bucketName);
        String keyName = Paths.get(filePath).getFileName().toString();
        try {

            s3.putObject(bucketName, keyName, new File(filePath));

        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }

        System.out.println("Done.");
    }

    /**
     * pull file/folder from aws s3
     */
    private static void pullFile() {

    }





}
