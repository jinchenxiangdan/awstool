package org.ualibrary.awss3tool;



import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;


import javax.xml.stream.events.StartDocument;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


/**
 * NOTE: Before running this program, please run 'aws configure' to configure your 'access key' and 'secret access key'
 *
 */
public class App 
{
    private static final String DELIMITER = "/";
//    private static final String DEFAULT_FOLDER = "";

    private static final String ORDERS = "0: List all objects.\n" +
            "ls: List file & folders in current file.\n" +
            "ul: Upload file/folder (Usage: ul [path to file/folder] [target position])\n" +
            "dl: Download file/folder (Usage : dl [path to file/folder] [target position])\n" +
            "cd: Get in folder\n" +
            "q: Quit.\n";

    private static AmazonS3 S3;
    private static Map<String, Bucket> BUCKETS_MAP = new HashMap<>();
    private static LinkedList<String> CURRENT_PATH = new LinkedList<>();

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
//        System.out.print(getCurrentPosition() + "$ ");
        int index = scanner.nextInt();
        while (index > buckets.size()) {
            System.out.println("Invalid index. please try input again.");
            index = scanner.nextInt();
        }
        String currentBucketName = buckets.get(index).getName();

        System.out.printf("You are currently in bucket %s.\n", currentBucketName);
        // set current folder's path
        System.out.println(ORDERS);

        // read commands from user
        System.out.print(getCurrentPosition() + "$ ");
        scanner.nextLine();
        String orders = scanner.nextLine();
        String[] commands = orders.trim().split(" ");
        while (!commands[0].equals("q")) {
            switch (commands[0]) {
                case "0":
                    listAllObjects(currentBucketName);
                    System.out.print(getCurrentPosition() + "$ ");
                    orders = scanner.nextLine();
                    commands = orders.trim().split(" ");
                    break;

                case "ls":
                    listObjects(currentBucketName, getCurrentPosition());
                    System.out.print(getCurrentPosition() + "$ ");
                    orders = scanner.nextLine();
                    commands = orders.trim().split(" ");
                    break;

                case "ul":
                    if (commands.length != 3) {
                        System.out.println("Usage: ul [path to file/folder] [target position]");
                        System.out.print(getCurrentPosition() + "$ ");
                        orders = scanner.nextLine();
                        commands = orders.trim().split(" ");
                        break;
                    }
                    // check the file path is valid or not
                    int type = fileChecker(commands[1]);
                    if (type == -1) {
                        System.out.println("Error: Invalid path.");
                        System.out.print(getCurrentPosition() + "$ ");
                        orders = scanner.nextLine();
                        commands = orders.trim().split(" ");
                        break;
                    }
                    // check the target bucket is valid or not
                    if (!BUCKETS_MAP.containsKey(commands[2])) {
                        System.out.println("Error: Invalid bucket name.");
                        System.out.print(getCurrentPosition() + "$ ");
                        orders = scanner.nextLine();
                        commands = orders.trim().split(" ");
                        break;
                    }
                    // upload

                    System.out.printf("target path is : %s\n", getCurrentPosition());
                    if (type == 1) {            // upload file
                        System.out.println(getCurrentPosition());
                        if (!uploadFolder(commands[1], currentBucketName + getCurrentPosition())) {
                            System.err.println("Upload failed!");
                            System.out.print(getCurrentPosition() + "$ ");
                            orders = scanner.nextLine();
                            commands = orders.trim().split(" ");
                            break;
                        }

                    } else if (type == 2) {     // upload folder
                        if (!uploadFile(commands[1], currentBucketName + getCurrentPosition())) {
                            System.err.println("Upload failed!");
                            System.out.print(getCurrentPosition() + "$ ");
                            orders = scanner.nextLine();
                            commands = orders.trim().split(" ");
                            break;
                        }
                    }
                    System.out.print(getCurrentPosition() + "$ ");
                    orders = scanner.nextLine();
                    commands = orders.trim().split(" ");
                    break;

                case "dl":
                    if (commands.length != 3) {
                        System.out.println("Usage : dl [path to file/folder] [target position]");
                        System.out.print(getCurrentPosition() + "$ ");
                        orders = scanner.nextLine();
                        commands = orders.trim().split(" ");
                        break;
                    }
                    int fileType = fileChecker(commands[2]);
                    if (fileType == -1) {
                        System.out.println("Error: Invalid path.");
                        System.out.print(getCurrentPosition() + "$ ");
                        orders = scanner.nextLine();
                        commands = orders.trim().split(" ");
                        break;
                    }
                    if (!downloadFiles(currentBucketName + "/" + getCurrentPosition() + commands[1],
                            commands[2])) {
                        System.out.println("Script runs failed.");
                        System.out.print(getCurrentPosition() + "$ ");
                        orders = scanner.nextLine();
                        commands = orders.trim().split(" ");
                        break;
                    } else {
                        System.out.println("Done.");
                    }
                    System.out.print(getCurrentPosition() + "$ ");
                    orders = scanner.nextLine();
                    commands = orders.trim().split(" ");
                    break;
                case "cd":
                    // check the user input 
                    if (commands.length != 2) {
                        System.out.println("Usage: cd [target position]");
                        System.out.print(getCurrentPosition() + "$ ");
                        orders = scanner.nextLine();
                        commands = orders.trim().split(" ");
                        break;
                    }
                    // check the command and path is valid or not 
                    if (commands[1].equals("..") || commands[1].equals("../")) {
                        CURRENT_PATH.pollLast();
                    }else {
                        CURRENT_PATH.add(commands[1]);
                        System.out.print(getCurrentPosition() + "$ ");
                        orders = scanner.nextLine();
                        commands = orders.trim().split(" ");
                        break;
                    }




                    System.out.print(getCurrentPosition() + "$ ");
                    orders = scanner.nextLine();
                    commands = orders.trim().split(" ");
                    break;

                default:
                    System.out.printf("Invalid input \"%s\", please try it again.\n", orders);
                    System.out.println(ORDERS);
                    System.out.print(getCurrentPosition() + "$ ");
                    orders = scanner.nextLine();
                    commands = orders.trim().split(" ");
            }


        }
        System.exit(0);


    }

    /**
     * Check the path is point to a file or folder
     * @param input a path in string
     * @return -1: invalid path
     *          1: is directory
     *          2: is regular file
     */
    private static int fileChecker(String input) {
        if (input == null || input.equals("")) {
            return -1;
        }
        Path file = new File(input).toPath();
        // check if the file exist or not
        if (!Files.exists(file)) {
            System.err.printf("Error: Cannot find file %s.\n", file);
            return -1;
        }
        // check the path is a file or folder
        if (Files.isDirectory(file)) {
            return 1;
        } else if (Files.isRegularFile(file)) {
            return 2;
        } else {
            System.out.println("Unknown file type");
            return -1;
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
            BUCKETS_MAP.put(bucketList.get(i).getName(), bucketList.get(i));
        }
        System.out.println("===========================");
        return bucketList;
    }


    /**
     * upload file to the aws s3
     * @param filePath string path to file
     * @param bucketName the bucket name will be upload to
     */
    private static boolean uploadFile(String filePath, String bucketName) {
        System.out.format("Uploading %s to S3 bucket %s...\n", filePath, bucketName);
        String keyName = Paths.get(filePath).getFileName().toString();
        try {

            S3.putObject(bucketName, keyName, new File(filePath));

        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            return false;
        }
        return true;
    }

    private static boolean uploadFolder(String filePath, String bucketName) {
        final String scriptPath = "src/main/bash_script/aws_s3_up_folder.sh";
        // formatting filePath?

        System.out.format("Uploading %s to S3 bucket %s...\n", filePath, bucketName);
        // running script

        ProcessBuilder processBuilder = new ProcessBuilder(scriptPath, filePath, bucketName);

        Process process;
        // run
        try {
            System.out.println("uploading...");
            process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }

        // get the exit status
//        assert process != null;
        if (process.exitValue() == 0) {
            System.out.println("Done.");
        } else {
            System.out.println("Something Wrong.");
            return false;
        }
//        System.out.printf("exist value: %d\n", process.exitValue());
        return process.exitValue() == 0;

    }


    private static void listObjects(String bucket_name, String prefix) {
        if (prefix.length() > 0) {
            prefix += "/";
        }
        // list first level
        System.out.println("************************");

        ListObjectsV2Request req = new ListObjectsV2Request().withBucketName(bucket_name)
                .withPrefix(prefix).withDelimiter(DELIMITER);
        ListObjectsV2Result listing = S3.listObjectsV2(req);
        for (String commonPrefix : listing.getCommonPrefixes()) {
            System.out.println(commonPrefix);
        }
        for (S3ObjectSummary summary: listing.getObjectSummaries()) {
            System.out.println(summary.getKey());
        }
        System.out.println("************************");
    }


    private static void listAllObjects(String bucketName) {
        ListObjectsV2Result result = S3.listObjectsV2(bucketName);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        System.out.println("************************");
        for (S3ObjectSummary os : objects) {
            System.out.println("* " + os.getKey());
        }
        System.out.println("************************");
    }

    /**
     * pull file/folder from aws s3
     */
    private static boolean downloadFiles(String filePath, String targetPath) {
        final String scriptPath = "src/main/bash_script/aws_s3_download.sh";
        System.out.format("Downloading %s to folder %s...\n", filePath, targetPath);
        // TODO: package it to folder then store in target path

        ProcessBuilder processBuilder = new ProcessBuilder(scriptPath, filePath, targetPath);
        Process process;
//        System.out.printf("filePath: %s, targetPath: %s: \n",filePath, targetPath);
        try {
            System.out.println("Downloading...");
            process = processBuilder.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            return false;
        }
//        System.out.printf("return value: %d\n",process.exitValue());

        return process.exitValue() == 0;
    }

    private static String getCurrentPosition() {
        if (CURRENT_PATH == null || CURRENT_PATH.size() == 0) {
            return "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (String path: CURRENT_PATH) {
            stringBuilder.append(path).append("/");
        }
        stringBuilder.deleteCharAt(stringBuilder.length()-1);
//        System.out.println("OUTPUT: " + stringBuilder.toString());
        return stringBuilder.toString();
    }


}
