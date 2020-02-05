package org.ualibrary.awss3tool;



import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;


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
    private static final String DELIMITER = "/";
    private static final String DEFAULT_FOLDER = "";
    private static AmazonS3 S3;
    private static final String ORDERS = "0: List all objects.\n" +
            "1: List file & folders in current file.\n" +
            "2: Upload file/folder (Usage: 2 [path to file/folder] [target position])" +
            "3: Download file/folder (Usage : 3 [path to file/folder] [target position])" +
            "cd: Get in folder" +
            "q: Quit.";


    public static void main( String[] args )
    {

        // test parameters
        String filePath = "/home/wan/Desktop/ShawnJin_Workspace/testFolder/test.txt";
        String folderPath = "/home/wan/Desktop/ShawnJin_Workspace/testFolder";
        // end test


        S3 = AmazonS3ClientBuilder.standard().withRegion("us-west-1").build();

        // list and get buckets
        List<Bucket> buckets = getBuckets();
        Scanner scanner = new Scanner(System.in);
        // ask user and access bucket
        System.out.println("Input the index of bucket you want to access.");
        int index = scanner.nextInt();
        while (index > buckets.size()) {
            System.out.println("Invalid index. please try input again.");
            index = scanner.nextInt();
        }
        String currentBucketName = buckets.get(index).getName();
        System.out.printf("You are currently in bucket %s.\n", currentBucketName);
        // set current folder's path
        String prefix = "";
        System.out.println(ORDERS);

        // read commands from user
        String orders = scanner.next();
        String[] commands = orders.trim().split("");
        while (!commands[0].equals("q")) {
            switch (commands[0]) {
                case "0":
                    listAllObjects(currentBucketName);
                    break;

                case "1":
                    listObjects(currentBucketName, prefix);
                    break;
                case "2":



                case "cd":
                    System.out.println("still working on it");
                    break;

                default:
                    System.out.println("Invalid input, please try it again.");
                    System.out.println(ORDERS);
            }


        }
        System.exit(0);


        // check folder or file

        System.out.println("Enter file/folder path");
        String input = scanner.next();
        Path file = new File(input).toPath();
        // check if the file exist or not
        if (Files.exists(file)) {

        } else {
            // the file doesn't exist
            System.err.printf("Error: Cannot find file %s.\n", file);
        }
        // check the path is a file or folder
        if (Files.isDirectory(file)) {
            System.out.printf("Going to upload folder %s", file);


        } else {
            // other?
        }






    }


    /**
     *  list and return buckets that the user has
     * @return A List of Buckets
     */
    private static List<Bucket> getBuckets() {
        List<Bucket> bucketList = S3.listBuckets();

        // print out information
        System.out.println("===========================");
        System.out.println("Your Amazon S3 buckets are:");
        for (int i = 0; i < bucketList.size(); i++) {
            System.out.println(i + " - " + bucketList.get(i).getName());
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
    private static void uploadFile(AmazonS3 s3, String filePath, String bucketName) {
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

    private static void uploadFolder(AmazonS3 s3, String filePath, String bucketName) {
        final String scriptPath = "/home/wan/Desktop/ShawnJin_Workspace/awstool/src/main/bash_script/aws_s3_up_folder.sh";
        // formatting filePath?

        System.out.format("Uploading %s to S3 bucket %s...\n", filePath, bucketName);
        // running script

        ProcessBuilder processBuilder = new ProcessBuilder(scriptPath, "arg1");

        Process process = null;
        // run
        try {
            System.out.println("uploading...");
            process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        // get the exit status
        assert process != null;
        if (process.exitValue() == 0) {
            System.out.println("Done.");
        } else {
            System.out.println("Something Wrong.");
        }
        System.out.printf("exist value: %d", process.exitValue());


    }

    private static void listObjects(String bucket_name, String prefix) {

        // list first level
        System.out.println("************************");

        ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucket_name).withPrefix(prefix).withDelimiter(DELIMITER);
        ListObjectsV2Result listing = S3.listObjectsV2(req);
        for (String commonPrefix : listing.getCommonPrefixes()) {
            System.out.println(commonPrefix);
        }
        for (S3ObjectSummary summary: listing.getObjectSummaries()) {
            System.out.println(summary.getKey());
        }
    }


    private static void listAllObjects(String bucketName) {
        ListObjectsV2Result result = S3.listObjectsV2(bucketName);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        for (S3ObjectSummary os : objects) {
            System.out.println("* " + os.getKey());
        }
    }

    /**
     * pull file/folder from aws s3
     */
    private static void downloadFile(String filePath, String tartgetPath) {

    }

    private static void downloadFolder(String filePath, String targetPath) {
        // need to cp command
    }





}
