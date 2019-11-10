package verteiltesysteme.aws;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;

/**
 *
 * In order to use the services in this sample, you need:
 *
 * - A valid Amazon Web Services account. You can register for AWS at:
 * https://aws-portal.amazon.com/gp/aws/developer/registration/index.html
 *
 * - Your account's Access Key ID and Secret Access Key:
 * http://aws.amazon.com/security-credentials
 *
 * - A subscription to Amazon EC2. You can sign up for EC2 at:
 * http://aws.amazon.com/ec2/
 *
 */
public class StartVertSysServersFrankfurt {

	/*
	 * Before running the code: Fill in your AWS access credentials in the provided
	 * credentials file template, and be sure to move the file to the default
	 * location (C:\\Users\\<username>\\.aws\\credentials) where the sample code will
	 * load the credentials from.
	 * https://console.aws.amazon.com/iam/home?#security_credential
	 *
	 * WARNING: To avoid accidental leakage of your credentials, DO NOT keep the
	 * credentials file in your source directory.
	 */

	public static void main(String[] args) throws Exception {

		System.out.println("===========================================");
		System.out.println("Verteilte Systeme AWS Demo (AWS Java SDK)");
		System.out.println("===========================================");

		/*
		 * The ProfileCredentialsProvider will return your [default] credential profile
		 * by reading from the credentials file located at
		 * (C:\\Users\\<username>\\.aws\\credentials).
		 */
		ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
		try {
			credentialsProvider.getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
					+ "Please make sure that your credentials file is at the correct "
					+ "location (C:\\Users\\<username>\\.aws\\credentials), and is in valid format.", e);
		}

		// Frankfurt
		System.out.println("Instantiating EC2 Client for Frankfurt...");
		AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard().withCredentials(credentialsProvider)
				.withRegion("eu-central-1").build();

		System.out.println("Loading user-data from file...");

		byte[] encoded = Files.readAllBytes(Paths.get("src\\verteiltesysteme\\aws\\user-data.txt"));
		String userData = Base64.getEncoder().encodeToString(encoded);

		System.out.println("Starting instance in Frankfurt...");
		RunInstancesRequest runInstancesRequest = new RunInstancesRequest();
		// amzn2-ami-hvm-2017.12.0.20180109-x86_64-gp2 (ami-f573e19a)
		runInstancesRequest.withImageId("ami-f573e19a").withInstanceType("t2.nano").withMinCount(1).withMaxCount(1)
				.withKeyName("srieger-amazon-aws-frankfurt").withUserData(userData)
				.withSecurityGroups("VerteilteSysteme-Demo");
		RunInstancesResult result = ec2.runInstances(runInstancesRequest);
		System.out.println(result);

	}
}
