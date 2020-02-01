package org.ualibrary.awss3tool;



import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;

import java.io.FileInputStream;
import java.util.List;
import java.io.File;

import java.nio.file.Paths;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion("us-west-1").build();


        List<Bucket> buckets = getBuckets(s3);

        String filePath = "/home/wan/Desktop/ShawnJin_Workspace/testFolder/test.txt";
        String folderPath = "/home/wan/Desktop/ShawnJin_Workspace/testFolder";
        String bucketName = buckets.get(2).getName();

        putObject(s3, filePath, bucketName);

//        test part
        try {




            FileInputStream stream = new FileInputStream(folderPath);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, Paths.get(filePath).getFileName().toString() ,stream , objectMetadata);
        } catch (Exception e) {
            e.printStackTrace();
        }



    }







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








}